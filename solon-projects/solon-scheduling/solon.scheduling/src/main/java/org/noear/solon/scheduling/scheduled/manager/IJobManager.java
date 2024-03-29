package org.noear.solon.scheduling.scheduled.manager;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public interface IJobManager extends Lifecycle {
    /**
     * 设置任务拦截器
     * */
    void setJobInterceptor(JobInterceptor jobInterceptor);

    /**
     * 获取任务拦截器
     * */
    JobInterceptor getJobInterceptor();

    /**
     * 任务添加
     * */
    JobHolder jobAdd(String name, Scheduled scheduled, JobHandler handler);
    /**
     * 任务是否存在
     * */
    boolean jobExists(String name);
    /**
     * 任务获取
     * */
    JobHolder jobGet(String name);

    /**
     * 任务获取全部
     */
    Map<String, JobHolder> jobGetAll();

    /**
     * 任务移除
     * */
    void jobRemove(String name);

    /**
     * 任务开始
     */
    void jobStart(String name, Map<String, String> data) throws ScheduledException;

    /**
     * 任务停止
     */
    void jobStop(String name) throws ScheduledException;

    /**
     * 是否已启动
     */
    boolean isStarted();
}
