# <center>LessDFS<center>

## 什么是LessDFS

* LessDFS是一个基于JDK8、Netty 4.0开发的跨平台轻量级小型文件系统
* 提供文件存储、文件访问（文件上传、文件下载）等
* 适合以小文件为载体的在线服务，如相册网站等等
* 因为基于Java开发，所以同时支持windows、linux平台

##  支持功能

* 基于TCP的文件上传、下载、删除
* 通过HTTP访问和下载文件
* 流光溢彩：提供图片处理功能，包括等比缩放、宽度缩放，高度不变、高度缩放，宽度不变、指定宽度，高度等比缩放、指定高度，宽度等比缩放、指定宽度和高度

## 如何使用

先克隆完整代码到本地机器

```shell
$ git clone git@github.com:liaochente/lessDFS.git
```

修改配置文件（文件在src/main/resources/less.conf），主要配置有服务端口、通讯密码、存储目录

```shell
#服务端口
less.server.port=8888
#通讯密码
less.server.password=123456
#虚拟存储目录
less.server.storage_path=/lessdfs_data
```

配置修改完毕后，就通过maven工具打包，获得可运行的fulljar
```shell
mvn clean  assembly:assembly -Dmaven.test.skip=true
```

最后，通过命令运行jar即可启动服务
```shell
java -jar  xxx-xxx.jar
```

## 客户端

* [Java客户端](https://github.com/liaochente/lessDFS-java-client)

## 备注

虽然已实现基本的文件服务，但目前仅是个人开发和测试，暂未投入实际使用；

后续考虑逐渐完善功能和测试，增加负载均衡、文件同步、小文件合并存储、文件服务治理等