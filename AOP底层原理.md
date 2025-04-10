## 概述
AOP 面向切面编程，spring AOP在Spring框架中的地位举足轻重，主要用于实现事务、缓存、安全等功能  
主要分以下三个方面：  
1、Spring Aop多种代理机制相关核心介绍  
2、Spring Boot中Aop注解方式的源码分析  
3、Spring Boot 1.X和2.X版本AOP默认配置的变动

### Spring Aop多种代理机制相关核心介绍
> 先介绍一些Spring Aop中的核心类，大致分为三类：
>>1、advisorCreator：继承spring Ioc的扩展接口beanPostProcessor，主要用来扫描获取advisor。   
> 2、advisor：顾问的意思，封装了spring aop中的切点和通知    
> 3、advice：通知，也就是aop中增强的方法  
