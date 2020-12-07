package com.mt.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.CORBA.INTERNAL;
import org.springframework.context.annotation.Bean;

/**
 * Created by 郭俊旺 on 2020/11/12 13:43
 *
 * @author 郭俊旺
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    private Integer cronId;
    private String cronKey;
    private String cronExpression;
    private String taskExplain;
    private String status;


}
