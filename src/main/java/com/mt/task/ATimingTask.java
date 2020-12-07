package com.mt.task;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 郭俊旺 on 2020/11/13 10:38
 *  自定义定时任务，必须实现 TimingTask接口
 * @author 郭俊旺
 */
@Component
public class ATimingTask extends TimingTask {


    @Override
    public void task() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("TaskA=====>"+sdf.format(new Date()));
    }

}
