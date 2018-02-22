/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月5日 下午1:19:42
*
* @Package com.arch.solution.distributedlock.service  
* @Title: UpdateGoodsOfNumServiceImpl.java
* @Description: UpdateGoodsOfNumServiceImpl.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.arch.solution.distributedlock.example;

import org.I0Itec.zkclient.ZkClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arch.solution.distributedlock.DistributedLock;
import com.arch.solution.distributedlock.zookeeper.ZkDistributedLock;

/**
 * ClassName:UpdateGoodsOfNumServiceImpl
 * @Description: UpdateGoodsOfNumServiceImpl.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月5日 下午1:19:42
 */
public class UpdateGoodsOfNumServiceImpl implements UpdateGoodsOfNumService {
	
	/**
	 * Logger the LOGGER 
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * int the count 
	 */
	private static int count = 100;
	
	/**
	* @MethodName: update
	* @Description: the update
	*/
	public void update() {
		ZkClient zkClient = new ZkClient("192.168.233.130:2181");
		DistributedLock lock = new ZkDistributedLock(zkClient, "/locker");
		// 获取锁，超时时间
		// lock.acquire(10, TimeUnit.SECONDS);
		// 获取锁，无超时时间
		lock.acquire();
		count--;
		LOGGER.info("|--------商品数量：" + count);
		lock.release();
	}
}
