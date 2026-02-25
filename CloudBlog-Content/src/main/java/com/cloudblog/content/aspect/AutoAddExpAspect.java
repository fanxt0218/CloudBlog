package com.cloudblog.content.aspect;

import com.cloudblog.common.annotation.AutoAddExp;
import com.cloudblog.common.enums.ExpSource;
import com.cloudblog.common.utils.UserContext;
import com.cloudblog.content.service.LevelService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AutoAddExpAspect {

    @Autowired
    private LevelService levelService;

    @Pointcut("@annotation(com.cloudblog.common.annotation.AutoAddExp)")
    public void autoAddExpPointCut() {}

    @Before("autoAddExpPointCut()")
    public void AddExp(JoinPoint joinPoint) {
        log.info("添加经验值");
        // 获取参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoAddExp annotation = signature.getMethod().getAnnotation(AutoAddExp.class);

        ExpSource source = annotation.value();
        int exp = annotation.exp();
        if (exp < 0) {
            exp = source.getExp();
        }
        // 从token中获取用户ID
        Long userId = UserContext.getUser();
        log.info("用户ID: {},增加经验{}", userId, exp);
        if (userId != null && userId > 0 && exp > 0) {
            levelService.addExp(userId, exp);
        }
    }

}
