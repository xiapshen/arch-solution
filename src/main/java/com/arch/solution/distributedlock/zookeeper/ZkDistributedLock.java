/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月5日 下午1:19:42
*
* @Package com.arch.solution.distributedlock.zookeeper
* @Title: ZkDistributedLock.java
* @Description: ZkDistributedLock.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.arch.solution.distributedlock.zookeeper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arch.solution.distributedlock.BaseDistributedLock;
/**
 * ClassName:ZkDistributedLock
 * @Description: ZkDistributedLock.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月4日 下午8:32:58
 */
public class ZkDistributedLock extends BaseDistributedLock {
	/**
	 * Logger the LOGGER 
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	
    /**
     * ZkClient the zkClient 
     */
    private final ZkClient zkClient;
    
    /**
     * String the rootNode 
     */
    private final String rootNode;
    
    /**
     * String the currentNode 
     */
    private String currentNode;
    
    /**
     * String the fullDistribNode 
     */
    private final String fullDistribNode;
    
    /**
     * String the LOCK_NODE 
     */
    private static final String LOCK_NODE = "lock_";
    
    /**
     * int the RETRY_COUNT 
     */
    private static final int RETRY_COUNT = 5;
    
    /**
     * int the retryCount 
     */
    private int retryCount = 0;

	/**
	 * Constructor com.arch.solution.distributedlock.zookeeper.ZkDistributedLock
	 * @param zkClient
	 * @param rootNode
	 */
	public ZkDistributedLock(ZkClient zkClient, String rootNode) {
		this.zkClient = zkClient;
		this.rootNode = rootNode;
		this.fullDistribNode = rootNode + "/" + LOCK_NODE;
		if (!this.zkClient.exists(rootNode)) {
			this.zkClient.createPersistent(this.rootNode);
		}
	}
	
	/**
	* @MethodName: lock
	* @Description: the lock
	* @param time
	* @param unit
	*/
	protected void lock(long time, TimeUnit unit) {
		try {
			// 1.创建当前线程的临时顺序节点
			this.createLockNode();
			// 2.尝试获取锁
			this.attempGetLock(time, unit);
		} catch (ZkNoNodeException ex) {
			LOGGER.warn(ex.getMessage());
			if (retryCount++ < RETRY_COUNT) {
				lock(time, unit);
			} else {
				throw ex;
			}
		} finally {
			this.releaseLock();
		}
	}
	
	/**
	* @MethodName: attempGetLock
	* @Description: the attempGetLock
	* @param time
	* @param unit
	*/
	private void attempGetLock(long time, TimeUnit unit) {
        long startMillis = System.currentTimeMillis();
        long millisToWait = (unit != null) ? unit.toMillis(time) : 0;
		// 所有子节点排序
		List<String> sortedLockChildrenList = getSortedChildrenList();
		String currentChildNode = currentNode.substring(rootNode.length() + 1);
		int curretEphSeqIndex = sortedLockChildrenList.indexOf(currentChildNode);
		if (curretEphSeqIndex < 0) {
			throw new ZkNoNodeException("CurrentLockNode '" + currentNode + "' is not exsist!");
		}
		boolean hasGetLock = (curretEphSeqIndex == 0) ? true : false;
		if (hasGetLock) {
			LOGGER.debug("|--------child node path:'" + currentNode + "' get lock!");
			return;
		}
		final String thePreviousChildNode = hasGetLock ? "" 
				: (rootNode + "/" + sortedLockChildrenList.get(curretEphSeqIndex-1));
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		IZkDataListener previousListener = new IZkDataListener() {
			
			public void handleDataDeleted(String dataPath) throws Exception {
				LOGGER.debug("|--------previous child node path '" + thePreviousChildNode + "' is delete from zookeeper");
				countDownLatch.countDown();
			}
			
			public void handleDataChange(String dataPath, Object data) throws Exception {
				//empty
			}
		};
		try {
			zkClient.subscribeDataChanges(thePreviousChildNode, previousListener);
			if (millisToWait > 0) {
			    millisToWait -= (System.currentTimeMillis() - startMillis);
			    startMillis = System.currentTimeMillis();
                if (millisToWait <= 0 ) {
                	throw new TimeoutException("acquired lock time out!");
                }
                countDownLatch.await(millisToWait, TimeUnit.MILLISECONDS);
                millisToWait -= (System.currentTimeMillis() - startMillis);
                if (millisToWait <= 0) {
                	throw new TimeoutException("acquired lock time out!");
                }
			} else {
				countDownLatch.await();
			}
			attempGetLock(millisToWait, unit);
		} catch (Exception e) {
			LOGGER.error("acquired lock exception", e);
			this.releaseLock();
		} finally {
			zkClient.unsubscribeDataChanges(thePreviousChildNode, previousListener);
		}
	}
	
	/**
	* @MethodName: getSortedChildrenList
	* @Description: the getSortedChildrenList
	* @return List<String>
	*/
	private List<String> getSortedChildrenList() {
		List<String> lockChildrenList = zkClient.getChildren(rootNode);
		Collections.sort(lockChildrenList, new Comparator<String>() {
			public int compare(String ephSeqNum1, String ephSeqNum2) {
				return getEphSeqFromChildNode(ephSeqNum1).compareTo(getEphSeqFromChildNode(ephSeqNum2));
			}
		});
		return lockChildrenList;
	}
	
	/**
	* @MethodName: getEphSeqFromChildNode
	* @Description: the getEphSeqFromChildNode
	* @param childNode
	* @return String
	*/
	private String getEphSeqFromChildNode(String childNode) {
		int seqBeginIndx = LOCK_NODE.length();
		return childNode.substring(seqBeginIndx);
	}
	
	/**
	* @MethodName: 创建临时顺序节点
	* @Description: the createLockNode
	*/
	private void createLockNode() {
		currentNode = zkClient.createEphemeralSequential(fullDistribNode, null);
	}
	
	/**
	* @MethodName: 删除当前lock_当前节点对应的序列数的path
	* @Description: the releaseLock
	*/
	protected void releaseLock() {
		zkClient.delete(currentNode);
		zkClient.close();
	}
}
