package com.sakura.reggieApi.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Objects;

/**
 * 日志实体类
 * @author sakura
 * @className Log
 * @createTime 2023/2/8
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(id, log.id) && Objects.equals(createTime, log.createTime) && Objects.equals(logName, log.logName) && Objects.equals(exceptionDetail, log.exceptionDetail) && Objects.equals(logType, log.logType) && Objects.equals(method, log.method) && Objects.equals(params, log.params) && Objects.equals(username, log.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, logName, exceptionDetail, logType, method, params, username);
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", logName='" + logName + '\'' +
                ", exceptionDetail='" + exceptionDetail + '\'' +
                ", logType='" + logType + '\'' +
                ", method='" + method + '\'' +
                ", params='" + params + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getExceptionDetail() {
        return exceptionDetail;
    }

    public void setExceptionDetail(String exceptionDetail) {
        this.exceptionDetail = exceptionDetail;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
