## Spring Boot 自动装配原理，源码剖析
1. springboot项目启动时，执行SpringApplication.run()方法
```java
@SpringBootApplication
public class HmDianPingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }
}
```
2. 在SpringApplication类的run()的方法中，最终会重载调用public ConfigurableApplicationContext run(String... args)方法，这个方法内会调用一个refreshContext(context)方法来完成自动装配操作  
```java
public ConfigurableApplicationContext run(String... args) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    DefaultBootstrapContext bootstrapContext = createBootstrapContext();
    ConfigurableApplicationContext context = null;
    configureHeadlessProperty();
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting(bootstrapContext, this.mainApplicationClass);
    try {
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
        configureIgnoreBeanInfo(environment);
        Banner printedBanner = printBanner(environment);
        context = createApplicationContext();
        context.setApplicationStartup(this.applicationStartup);
        prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
        refreshContext(context);
        afterRefresh(context, applicationArguments);
        stopWatch.stop();
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
        }
        listeners.started(context);
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, listeners);
        throw new IllegalStateException(ex);
    }

    try {
        listeners.running(context);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, null);
        throw new IllegalStateException(ex);
    }
    return context;
}
```
```mermaid
flowchart TD
    A[启动计时器] --> B[创建引导上下文]
    B --> C[配置无头模式]
    C --> D[获取运行监听器]
    D --> E[监听器启动通知]
    E --> F[准备应用环境]
    F --> G[配置BeanInfo忽略]
    G --> H[打印启动Banner]
    H --> I[创建应用上下文]
    I --> J[准备上下文配置]
    J --> K[刷新应用上下文]
    K --> L[执行后置处理]
    L --> M[停止计时器]
    M --> N[记录启动日志]
    N --> O[通知上下文启动]
    O --> P[调用Runner]
    P --> Q{是否异常}
    Q -->|是| R[处理运行失败]
    Q -->|否| S[通知运行状态]
    S --> T{是否异常}
    T -->|是| U[处理运行失败]
    T -->|否| V[返回上下文]
    R --> W[抛出异常]
    U --> W
```
