package com.sakura.reggieApi.common.aspect;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.mapper.LogMapper;
import com.sakura.reggieApi.common.pojo.Log;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

/**
 * @author sakura
 * @className LogAspect
 * @createTime 2023/2/8
 */
@Component
@Aspect
@Slf4j
public class LogAspect {


    private final LogMapper logMapper;

    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;
    @Resource
    private TokenUtils tokenUtils;

    @Autowired
    public LogAspect(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    // 配置切入点
    @Pointcut("@annotation(com.sakura.reggieApi.common.anno.LogAnnotation)")
    public void pointcutAll(){}


    @Around("pointcutAll()")
    @Transactional
    public Object controllerAround(ProceedingJoinPoint joinPoint) {
        Log logMsg = new Log();

        // 获取方法的名字
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.toString();
        log.info("正在执行方法: " + methodName.substring(0, methodName.indexOf("(")));
        logMsg.setMethod(methodName);

        Method method = methodSignature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

        String logName = logAnnotation.value();
        logMsg.setLogName(logName);

        String username = tokenUtils.getMemberUsernameByJwtToken(request);
        logMsg.setUsername(username);

        // 拼接参数
        Object[] args = joinPoint.getArgs();
        Parameter[] methodParameters = method.getParameters();
        // 参数
        String param = spellMethodParam(args, methodParameters);
        logMsg.setParams(param);

        // 方法的返回值
        Object methodValue = null;

        try {
            // 执行方法
            methodValue = joinPoint.proceed();

            // 如果执行成功 日志级别为 info
            logMsg.setLogType("INFO");
        } catch (Throwable e) {
            // 异常为 ERROR
            logMsg.setLogType("ERROR");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logMsg.setExceptionDetail(sw.toString());

            methodValue = JsonResponseResult.error(e.getMessage());
        } finally {
            logMsg.setCreateTime(new Date());
            logMapper.insert(logMsg);
        }

        return methodValue;
    }



    /**
     * 拼接字符串
     * @param argsMethod 传递的参数
     * @param methodParams 方法中的参数名
     * @return 拼接好的字符串
     */
    private String spellMethodParam(Object[] argsMethod, Parameter[] methodParams) {
        StringBuffer sbf = new StringBuffer();
        sbf.append("{ ");
        for (int i = 0; i < methodParams.length; i++) {
            Parameter methodParam = methodParams[i];
            sbf.append(methodParam.getName()).append(" : ");
            Object o = argsMethod[i];

            if (i == methodParams.length - 1) {
                if (o == null) {
                    sbf.append("null");
                } else {
                    sbf.append(o.toString());
                }
                sbf.append(" ");
            } else {
                if (o == null) {
                    sbf.append("null");
                } else {
                    sbf.append(o.toString());
                }
                sbf.append(" ");
            }
        }

        sbf.append("}");

        return sbf.toString();
    }



}
