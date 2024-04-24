# 依赖检查插件

> 用来检查gradle依赖中是否有`快照`依赖, 如果有通过bootjar构建报错
>> 插件依赖 java插件 如果不引用java插件则报错

## 使用方式

buildscript 中引入私服. 引入插件包名

```groovy
buildscript {
    repositories {

        maven {
            allowInsecureProtocol = true
            url "私服地址"

        }
    }

    dependencies {
        classpath "com.itjcloud.common:check-dependencies-plugin:1.0.0"
    }
}
```

应用插件
apply plugin: 'check-dependencies'

插件配置

//关闭bootjar依赖检查
checkDependency.isCheckSnapshot = false