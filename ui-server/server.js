const Koa = require('koa')
const app = new Koa()
const fs = require('fs')
const send = require('koa-send')
const serve = require('koa-static-server')
const router = require('koa-router')()

router
    .get('/', async(ctx, next) => {
        await send(ctx, './index.html')
    })
    .get('/dash/', async(ctx, next) => {
        await send(ctx, './dash/index.html')
    })
    .get('/tool/', async(ctx, next) => {
        await send(ctx, './tool/index.html')
    })

app
    .use(router.routes())
    .use(serve({rootDir: 'dash', rootPath: '/dash'}))
    .use(serve({rootDir: 'tool', rootPath: '/tool'}))
    .use(router.allowedMethods())
// response
// app.use(async(ctx) => {
//     ctx.body = 'Hello World'
// })

app.listen(8888, () => console.log('server started 8888'))

module.exports = app
