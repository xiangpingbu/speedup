# honeybee
> 出于性能和可维护性考虑，直接使用python渲染静态页，去掉不必要的node服务

## todo
- 将其他功能页面（tool.html等）也从node中移过来，完全移除node  
- 可以用nginx渲染静态页，python只提供接口  

## dev
> path to /honeybee
> python honeybee_server.py
> new terminal
> path to /ui_dev_d3  
> npm run dev
> open http://localhost:8080

## prod
> path to /ui_dev_d3
> npm run build
> path to /honeybee
> python honeybee_server.py 
> open http://localhost:8091/dash/index.html  
