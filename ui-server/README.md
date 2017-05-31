# ui-server
> 前端页面服务，peacock和ui_dev_d3生成用于生产环境的前端文件都copy过来，由node负责serve  
> python只提供接口比较干净，ui-server的node只负责托管页面，不做接口转发，今后可用nginx替代  
> dashboard 依赖执行 python honeybee_server.py 后的8091端口服务  

## build
> # 进入两个前端项目执行build并将静态页移动过来 
> npm run build

## start
> # 监听在 http://localhost:8888  
> npm run start

## deploy
> # 执行 npm run build 后rsync上传到服务器对应目录   
> npm run deploy
