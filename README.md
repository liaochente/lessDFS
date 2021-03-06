<div align=center><img width=200 height=200 src="https://crop-1257315785.cos.ap-shanghai.myqcloud.com/carp/pywxicon/lessDFS-logo.png"/></div>

## 什么是LessDFS

* LessDFS是一个基于JDK8、Netty 4.0开发的跨平台轻量级小型文件系统
* 提供文件存储、文件访问（文件上传、文件下载）等
* 适合以小文件为载体的在线服务，如相册网站等等

##  支持功能

* 可以通过客户端SDK（基于TCP协议），进行文件上传、下载、删除
* 可以通过HTTP协议访问和下载文件
* 可以通过流光溢彩功能对服务器上的图片文件进行处理，提供图片处理功能，包括等比缩放、宽度缩放，高度不变、高度缩放，宽度不变、指定宽度，高度等比缩放、指定高度，宽度等比缩放、指定宽度和高度

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

欢迎交流
QQ交流群：1037987281
后续考虑逐渐完善功能和测试，增加负载均衡、文件同步、小文件合并存储、文件服务治理等
