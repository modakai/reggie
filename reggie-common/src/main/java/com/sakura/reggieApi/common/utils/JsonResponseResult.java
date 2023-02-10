package com.sakura.reggieApi.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于 返回 JSON 格式的响应信息
 * @author sakura
 * @className JsonResponseResult
 * @createTime 2023/2/8
 */
@JsonIgnoreProperties(value = "handler")
public class JsonResponseResult<T> implements Serializable {

    private static final Long serialVersionUID = 23L;
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String code;
    private Boolean success;
    private String msg;
    private T data;
    private String token;
    private String date;

    public JsonResponseResult() {
    }

    /**
     * @param token 用户登入成功后返回的令牌
     * @return 登入成功后 返回 用户的信息
     * @throws JsonProcessingException JSON 解析异常
     */
    public static String successToken(String token, String username) {
        JsonResponseResult<String> result = new JsonResponseResult<>();

        result.code = "200";
        result.success = true;
        result.msg = "登入成功";
        result.token = token;
        result.data = username;
        result.date = DATE_FORMAT.format(new Date());

        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @return 默认的响应成功信息
     * @param <T> 类型
     */
    public static <T> String defaultSuccess(String msg)  {
        JsonResponseResult<T> result = new JsonResponseResult<>();

        result.code = "200";
        result.success = true;
        result.msg = msg;
        result.date = DATE_FORMAT.format(new Date());

        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param data 数据
     * @return 返回 带数据的响应成功信息
     * @param <T> 类型
     */
    public static <T> String success(T data) {
        JsonResponseResult<T> result = new JsonResponseResult<>();

        result.code = "200";
        result.success = true;
        result.msg = "响应成功";
        result.data = data;
        result.date = DATE_FORMAT.format(new Date());

        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param msg 信息
     * @return 返回 响应失败的信息
     * @throws JsonProcessingException
     */
    public static <T> String error(String msg) {
        JsonResponseResult<String> result = new JsonResponseResult<>();

        result.code = "-1";
        result.success = false;
        result.msg = msg;
        result.date = DATE_FORMAT.format(new Date());

        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "JsonResponseResult{" +
                "code='" + code + '\'' +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", token='" + token + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
