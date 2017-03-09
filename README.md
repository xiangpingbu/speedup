# Maas
##java web
maas的java模块由maven构建,因此在项目工程上可以将其分为几个模块
###web模块
web模块由下面几部分组成
####jersey
jersey是一个web框架.在它的帮助下,可以使用注解和java方法描述一次Http请求接收和响应的过程.jersey自己维护一个容器,利用扫描包中的注解信息,将URL Mapping加入容器管理.

####swagger
将Rest接口图形界面化,方便调试接口.配置和初始化过程和jersey相似.

####spring
spring也有一个容器,用于维护项目内部的对象,可以方便整合其他工具,例如Redis,mysql等.

###其他模块
maven工程的模块可以相互依赖,随着业务的扩大,项目的体积也会随之膨胀.模块化有利于梳理项目的结构

###图示
![maas](http://oagjvfn8h.bkt.clouddn.com/maas.png)

	黑色箭头代表依次请求过程
	白色箭头指向代表被依赖的关系


##python web
因为建模和分析过程需要依赖python的第三方类库,因此再搭建一个由python实现的web项目.通过http接口对外提供数据.

###项目结构
flask作为web框架,部署在gunicorn服务器上,结合gevent提高服务端的并发能力.

##模型服务
###模型计算流程

![maas-model -1-](http://oagjvfn8h.bkt.clouddn.com/maas-model%20-1-.png)




