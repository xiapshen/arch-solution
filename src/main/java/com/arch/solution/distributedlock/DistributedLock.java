/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月5日 下午1:19:42
*
* @Package com.arch.solution.distributedlock
* @Title: DistributedLock.java
* @Description: DistributedLock.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.arch.solution.distributedlock;

import java.util.concurrent.TimeUnit;

/**
 * ClassName:DistributedLock
 * @Description: DistributedLock.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月4日 下午8:33:28
 */
public interface DistributedLock {
    /**
     * Acquire.
     * 获取锁，如果没得到一直等待
     */
     void acquire();
    
    /**
     * Acquire boolean.
     * 获取锁，直到超时
     *
     * @param time the time
     * @param unit the unit
     * @throws Exception the exception
     */
     void acquire(long time, TimeUnit unit);

    /**
     * Release.
     */
    void release();;
}
