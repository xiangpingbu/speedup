
### python web
因为建模和分析过程需要依赖python的第三方类库,因此再搭建一个由python实现的web项目.通过http接口对外提供数据.

#### 项目结构
flask作为web框架,部署在gunicorn服务器上,结合gevent提高服务端的并发能力.



