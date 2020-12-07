//package com.mt;
//
//import com.mt.bean.Schedule;
//import com.mt.mapper.ScheduleMapper;
//import com.mt.utils.SpringUtils;
//
///**
// * Created by 郭俊旺 on 2020/11/12 16:01
// *
// * @author 郭俊旺
// */
//public interface ScheduledOfTask extends Runnable{
//    /**
//     * 定时任务方法
//     */
//    void execute();
//    /**
//     * 实现控制定时任务启用或禁用的功能
//     */
//    @Override
//    default void run() {
//        //在这里查询数据库的原因是 每次执行定时任务时判断当前是否被禁止执行了
//        // 如果是禁止执行就不走数据库
//        ScheduleMapper scheduleMapper = SpringUtils.getBean(ScheduleMapper.class);
//
//        Schedule scheduledCron = scheduleMapper.findByCronKey(this.getClass().getName());
//        //判断 当前得执行器是否是可执行状态
//        if ("1".equals(scheduledCron.getStatus())) {
//            execute();
//        }
//
//    }
//}
