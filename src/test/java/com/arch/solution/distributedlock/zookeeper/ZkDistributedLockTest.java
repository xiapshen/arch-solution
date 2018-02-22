/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月5日 下午1:13:43
*
* @Package com.arch.solution.distributedlock.zookeeper  
* @Title: ZkDistributedLockTest.java
* @Description: ZkDistributedLockTest.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.arch.solution.distributedlock.zookeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.arch.solution.distributedlock.example.UpdateGoodsOfNumService;
import com.arch.solution.distributedlock.example.UpdateGoodsOfNumServiceImpl;
import com.arch.solution.distributedlock.example.UpdateGoodsThread;

/**
 * ClassName:ZkDistributedLockTest
 * @Description: ZkDistributedLockTest.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月5日 下午1:13:43
 */
public class ZkDistributedLockTest {
	
	/**
	 * Logger the LOGGER 
	 */
	private static final Logger LOGGER = LogManager.getLogger(); 

	@Test
	public void testAcquire() throws InterruptedException {
		int count = 100;
		CountDownLatch countDownLatch = new CountDownLatch(count);
		ExecutorService executorService = Executors.newFixedThreadPool(count);
		for (int i=0; i<count; i++) {
			UpdateGoodsOfNumService updateGoodsOfNumService= new UpdateGoodsOfNumServiceImpl();
			UpdateGoodsThread goodsThread = new UpdateGoodsThread(updateGoodsOfNumService, countDownLatch);
			executorService.submit(goodsThread);
		}
		countDownLatch.await();
		LOGGER.info("|--------测试开始......");
		CountDownLatch countDown = new CountDownLatch(1);
		countDown.await();
	}
}
