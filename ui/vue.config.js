const path = require('path');

module.exports = {
  outputDir: path.resolve(__dirname, '../src/main/resources/static'), // 打包输出到 Spring Boot static
  // 必须使用绝对路径。相对路径在刷新 /projects/:id 这类深层路由时，
  // 浏览器会按 /projects/js/app.js 去加载资源，导致页面空白。
  publicPath: '/',
  devServer: {
    port: 8081, // 本地调试端口，可避免和 Spring Boot 冲突
    proxy: {
      '/api': {
        target: 'http://localhost:9992', // Spring Boot 后端接口
        changeOrigin: true
      }
    }
  }
};