package com.sakura.reggieApi.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * 日志实体类
 * @author sakura
 * @className Log
 * @createTime 2023/2/8
 */
@Data
@ToString
@EqualsAndHashCode
@TableName("log")
public class Log {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String logName;
    private String exceptionDetail;
    private String logType;
    private String method;
    private String params;
    private String username;

}
