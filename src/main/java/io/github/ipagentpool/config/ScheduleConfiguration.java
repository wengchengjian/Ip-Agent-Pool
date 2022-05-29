package io.github.ipagentpool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @Author 翁丞健
 * @Date 2022/5/27 17:08
 * @Version 1.0.0
 */
@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskExecutor());
    }

    @Bean("scheduleTaskExcutor")
    public TaskScheduler taskExecutor(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler  = new ThreadPoolTaskScheduler();
        // 配置线程池大小
        threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors()+1);
        // 设置线程名
        threadPoolTaskScheduler.setThreadNamePrefix("task-scheduling-");

        // 设置等待任务在关机时完成
        //    threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置等待终止时间
        // threadPoolTaskScheduler.setAwaitTerminationSeconds(60);

        return threadPoolTaskScheduler;
    }
}
