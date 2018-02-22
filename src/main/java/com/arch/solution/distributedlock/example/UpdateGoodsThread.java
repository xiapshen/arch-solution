/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月5日 下午1:24:45
*
* @Package com.arch.solution.distributedlock.service  
* @Title: UpdateGoodsThread.java
* @Description: UpdateGoodsThread.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.arch.solution.distributedlock.example;

import java.util.concurrent.CountDownLatch;

/**
 * ClassName:UpdateGoodsThread
 * @Description: UpdateGoodsThread.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月5日 下午1:24:45
 */
public class UpdateGoodsThread implements Runnable {
	/**
	 * UpdateGoodsOfNumService the updateGoodsOfNumService 
	 */
	private UpdateGoodsOfNumService updateGoodsOfNumService;
	
	/**
	 * CountDownLatch the countDownLatch 
	 */
	private CountDownLatch countDownLatch;

	/**
	 * Constructor com.arch.solution.distributedlock.example.UpdateGoodsThread
	 * @param updateGoodsOfNumService
	 * @param countDownLatch
	 */
	public UpdateGoodsThread(
			UpdateGoodsOfNumService updateGoodsOfNumService,
			CountDownLatch countDownLatch) {
		super();
		this.updateGoodsOfNumService = updateGoodsOfNumService;
		this.countDownLatch = countDownLatch;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		countDownLatch.countDown();
		updateGoodsOfNumService.update();
	}
}
