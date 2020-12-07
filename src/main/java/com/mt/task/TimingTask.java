package com.mt.task;

import com.mt.bean.Schedule;

/**
 * Created by 郭俊旺 on 2020/11/13 10:37
 *
 * @author 郭俊旺
 */
//自定义 定时任务接口
public abstract class TimingTask implements Runnable {
   private Schedule schedule;


    public Schedule getSchedule() {
        return schedule;
    }


    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public  void run() {
        this.task();
    }

    //执行的任务
    public abstract void  task();


    @Override
    public String toString() {
        return schedule.getCronId()+"   "+schedule.getCronKey();
    }

}
