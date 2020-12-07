//package com.mt;
//
//import com.mt.bean.Schedule;
//import com.mt.mapper.ScheduleMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.config.ScheduledTask;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.scheduling.config.Task;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.util.Assert;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by 郭俊旺 on 2020/11/12 15:41
// *
// * @author 郭俊旺
// */
//@Configuration
//@Slf4j
//public class ScheduleConfig implements SchedulingConfigurer {
//
//
//    @Autowired
//    private ApplicationContext context;
//
//    @Autowired
//    private ScheduleMapper scheduleMapper;
//
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){
//        List<Schedule> scheduleList = scheduleMapper.getAll();
//
//        try {
//
//            for (Schedule schedule : scheduleList) {
//
//                Class clazz = null;
//                Object task = null;
//
//                try {
//                    clazz = Class.forName(schedule.getCronKey());
//                    task  = context.getBean(clazz);
//                } catch (ClassNotFoundException  e) {
//                    log.error("未找到该执行器:",schedule.getCronKey());
//                    continue;
//                } catch (BeansException e) {
//                    log.error("spring容器内不存在该Bean:",schedule.getCronKey());
//                    continue;
//                }
//                //判断是否继承至  ScheduledOfTask
//                if(! (task instanceof ScheduledOfTask ) ){
//                    log.error("定时任务类必须实现 Runnable 接口");
//                }
//
//
//                taskRegistrar.addTriggerTask( ( (ScheduledOfTask)task ),
//                    triggerContext -> {
//                        return new CronTrigger(schedule.getCronExpression()).nextExecutionTime(triggerContext);
//                    }
//                );
//
//                Set<ScheduledTask> scheduledTasks = taskRegistrar.getScheduledTasks();
//            }
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        // 定时任务执行线程池核心线程数
//        taskScheduler.setPoolSize(4);
//        taskScheduler.setRemoveOnCancelPolicy(true);
//        taskScheduler.setThreadNamePrefix("TaskSchedulerThreadPool-");
//        return taskScheduler;
//    }
//
//
//}
