package com.lss.aspect;

import com.alibaba.fastjson.JSON;
import com.lss.annotation.OptLog;
import com.lss.entity.OperationLog;
import com.lss.entity.User;
import com.lss.service.OperationLogService;
import com.lss.service.UserService;
import com.lss.utils.IpUtils;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 操作日志切面处理
 *
 * @author lss
 * @create 2022年03月12日 21:42
 */
@Aspect
@Component
public class OptLogAspect {

    @Resource
    UserService userService;
    @Resource
    OperationLogService operationLogService;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.lss.annotation.OptLog)")
    public void optLogPointCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param keys      返回结果
     */
    @AfterReturning(value = "optLogPointCut()", returning = "keys")
    public void saveOptLog(JoinPoint joinPoint, Object keys) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        //获取操作
        OptLog optLog = method.getAnnotation(OptLog.class);
        OperationLog operationLog = new OperationLog();
        // 操作模块
        operationLog.setOptModule(optLog.ApiModule());
        // 操作类型
        operationLog.setOptType(optLog.optType());
        // 操作描述
        operationLog.setOptDesc(optLog.ApiDesc());
        // 获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        // 获取请求的方法名
        String methodName = method.getName();
        methodName = className + "." + methodName;
        // 请求方式
        operationLog.setRequestMethod(Objects.requireNonNull(request).getMethod());
        // 请求方法
        operationLog.setOptMethod(methodName);
        // 请求参数
        operationLog.setRequestParam(Arrays.toString(joinPoint.getArgs()));
        // 返回结果
        operationLog.setResponseData(JSON.toJSONString(keys));
        // 请求用户ID
        Claims claims = JWTUtils.parseToken(request.getHeader("token"));
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        operationLog.setUserId(user.getId());
        // 请求用户
        operationLog.setNickname(user.getNickname());
        // 请求IP
        String ipAddress = IpUtils.getIpAddress(request);
        operationLog.setIpAddress(ipAddress);
        operationLog.setIpSource(IpUtils.getIpSource(ipAddress));
        // 请求URL
        operationLog.setOptUrl(request.getRequestURI());
        operationLog.setCreateTime(new Date());
        operationLogService.save(operationLog);
    }
}
