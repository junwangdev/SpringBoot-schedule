package com.mt.task;

import com.mt.bean.Schedule;
import com.mt.mapper.ScheduleMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by 郭俊旺 on 2020/11/12 17:01
 *
 * @author 郭俊旺
 */
@Configuration
@EnableScheduling
public class DynamicTask implements SchedulingConfigurer {
    private static Logger LOGGER = LoggerFactory.getLogger(DynamicTask.class);

    //通过 registrar 可以注册 定时任务
    private volatile ScheduledTaskRegistrar registrar;


    //存放执行器
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    //存放当前执行的定时任务
    private final ConcurrentHashMap<Integer, CronTask> cronTasks = new ConcurrentHashMap<>();


    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private ApplicationContext applicationContext;



    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        this.registrar = registrar;


        this.registrar.addTriggerTask(() -> {
            //查询定时任务
            List<Schedule> scheduleList = scheduleMapper.getAll();
            createTask(scheduleList);
        }
        , triggerContext -> new PeriodicTrigger(5L, TimeUnit.SECONDS).nextExecutionTime(triggerContext));
        
    }


    public void createTask(List<Schedule> scheduleList){

        if (!CollectionUtils.isEmpty(scheduleList)) {
            LOGGER.info("检测动态定时任务列表...");
            List<TimingTask> tts = new ArrayList<>();

            scheduleList .forEach(taskConstant -> {
                Object bean = null;
                try {
                    Class<?> taskClass = Class.forName(taskConstant.getCronKey());
                    //创建定时任务 去执行
                    bean = applicationContext.getBean(taskClass);
                } catch (ClassNotFoundException e) {
                    LOGGER.info("定时任务执行器{} 在Spring内找不到!",taskConstant.getCronKey());
                    return;
                }

                //获取bean后转换未 TimingTask 对象
                if(bean instanceof  TimingTask){
                    TimingTask timingTask =  (TimingTask)bean;
                   timingTask.setSchedule(taskConstant);
                    tts.add(timingTask);
                }
            });
            this.refreshTasks(tts.toArray(new TimingTask[tts.size()]));
        }
        LOGGER.info("定时任务加载完成!");

    }




    private void refreshTasks(TimingTask... tasks) {

        Set<Integer> taskIds = scheduledFutures.keySet();

        //查看任务是否已经存在,如果存在了就取消
        for (Integer taskId : taskIds) {
            if (!exists(taskId,tasks)) {
                scheduledFutures.get(taskId).cancel(false);
            }
        }

        for (TimingTask timingTask : tasks) {

            //校验 cron 表达式是否合法
            String expression = timingTask.getSchedule().getCronExpression();
            if (StringUtils.isBlank(expression) || !CronSequenceGenerator.isValidExpression(expression)) {
                LOGGER.error("定时任务"+timingTask.getSchedule().getCronKey()+" cron表达式不合法: " + expression);
                continue;
            }


            //如果配置一致，并且处于启用状态 则不需要重新创建定时任务
            if (scheduledFutures.containsKey(timingTask.getSchedule().getCronId())
                    && cronTasks.get(timingTask.getSchedule().getCronId()).getExpression().equals(expression) && timingTask.getSchedule().getStatus().equals("1")) {
                continue;
            }


            //如果策略执行时间发生了变化(如cron表达式修改,状态修改)，则取消当前策略的任务,重新创建
            if (scheduledFutures.containsKey(timingTask.getSchedule().getCronId())) {
                scheduledFutures.remove(timingTask.getSchedule().getCronId()).cancel(false);
                cronTasks.remove(timingTask.getSchedule().getCronId());
            }

            //如果任务有效,才创建
           if(timingTask.getSchedule().getStatus().equals("1")){
               //创建定时任务执行
               CronTask task = new CronTask(timingTask, expression);
               ScheduledFuture<?> future = registrar.getScheduler().schedule(task.getRunnable(), task.getTrigger());
               cronTasks.put(timingTask.getSchedule().getCronId(), task);

               scheduledFutures.put(timingTask.getSchedule().getCronId(), future);
               LOGGER.info("添加定时任务===>{}   Cron表达式==>{}",timingTask.getSchedule().getCronKey(),timingTask.getSchedule().getCronExpression());
           }
        }
    }

    private boolean exists(Integer taskId,TimingTask... tasks) {
        for (TimingTask task : tasks) {
            if (task.getSchedule().getCronId().equals(taskId)) {
                return true;
            }
        }
        return false;
    }



    @PreDestroy
    public void destroy() {
        this.registrar.destroy();
    }
}