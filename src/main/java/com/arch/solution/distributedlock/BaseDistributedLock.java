/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月5日 下午1:19:42
*
* @Package com.arch.solution.distributedlock
* @Title: BaseDistributedLock.java
* @Description: BaseDistributedLock.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.arch.solution.distributedlock;

import java.util.concurrent.TimeUnit;

/**
 * ClassName:BaseDistributedLock
 * @Description: BaseDistributedLock.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月4日 下午8:33:15
 */
public abstract class BaseDistributedLock implements DistributedLock {
	/** 
	 * 未获取到锁，一直等待
	 * @see com.arch.solution.distributedlock.DistributedLock#acquire()
	 */
	public void acquire() {
		lock(-1, null);
	}

	/** 
	 * 获取锁时，在超过超时时间未获取到锁，直接报超时异常
	 * @see com.arch.solution.distributedlock.DistributedLock#acquire(long, java.util.concurrent.TimeUnit)
	 */
	public void acquire(long time, TimeUnit unit) {
		lock(time, unit);
	}

	/**
	 * 释放锁
	 * @see com.arch.solution.distributedlock.DistributedLock#release()
	 */
	public void release() {
		releaseLock();
	}

	/**
	* @MethodName: lock
	* @Description: the lock
	* @param time
	* @param unit
	*/
	protected abstract void lock(long time, TimeUnit unit);
	
	/**
	* @MethodName: releaseLock
	* @Description: the releaseLock
	*/
	protected abstract void releaseLock();
}
