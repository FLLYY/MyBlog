package com.rawchen.aspct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Mainaspect {

    @Pointcut("execution(* com.rawchen.domain.*.*(..))")
    private void anyMethod() {}

    @Before("anyMethod()")
    public void before(JoinPoint joinPoint) {
        System.out.println("Before executing method: " + joinPoint.getSignature().getName());
    }

    @After("anyMethod()")
    public void after(JoinPoint joinPoint) {
        System.out.println("After executing method: " + joinPoint.getSignature().getName());
    }

    @AfterReturning("anyMethod()")
    public void afterReturning(JoinPoint joinPoint) {
        System.out.println("After returning from method: " + joinPoint.getSignature().getName());
    }

    @AfterThrowing("anyMethod()")
    public void afterThrowing(JoinPoint joinPoint) {
        System.out.println("After throwing exception from method: " + joinPoint.getSignature().getName());
    }
}
