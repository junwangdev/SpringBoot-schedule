package com.mt.mapper;

import com.mt.bean.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

/**
 * Created by 郭俊旺 on 2020/11/12 13:52
 *
 * @author 郭俊旺
 */

public interface ScheduleMapper {
    /**
     * 获取有效得定时任务
     * */
    @Select(" select cron_id,cron_key,cron_expression,task_explain,status from spring_scheduled_cron ")
    public List<Schedule> getAll();
}
