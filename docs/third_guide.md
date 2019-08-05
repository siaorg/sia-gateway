# 第三方插件开发指南

### 功能介绍

API网关对ZuulFilter进行了抽象封装，业务系统根据template提供的AbstractThirdPartyFilter抽象类，实现定制化的业务逻辑按照标准规范开发及打包步骤，上传到API网关平台后通过路由绑定组件功能使业务逻辑生效。



###  POM文件命名规范及依赖

【规范1】artifactId命名需要与Filter类名一致，并且需要带version信息。

【规范2】需要引入 `sia-gateway-template` 依赖


```xml

<groupId>com.sia</groupId>
<artifactId>TestFilter</artifactId>
<version>1.0</version>

    ........

<dependency>
    <groupId>com.sia</groupId>
    <artifactId>sia-gateway-template</artifactId>
    <version>1.0</version>
</dependency>
```

【规范3】第三方组件工程的依赖包需要放在sia-gateway-template的 POM.xml 加载相关依赖。
第三方组件工程打包仅包含源码文件，所有第三方JAR包由sia-gateway-template加载依赖


###  Demo工程代码实例

sia-gateway-testFilter 源码工程包含业务第三方组件开发实例。