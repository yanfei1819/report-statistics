package com.yudianbank.tms.job.callback;

/**
 * 处理后的回调函数接口
 *
 * @author Song Lea
 */
@FunctionalInterface
public interface ThreadCallback {

    // 处理函数,子类实现
    void handler();
}