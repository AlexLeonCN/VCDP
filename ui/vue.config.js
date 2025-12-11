const path = require('path');

module.exports = {
  outputDir: path.resolve(__dirname, '../src/main/resources/static'), // 打包输出到 Spring Boot static
  publicPath: './', // 相对路径
  devServer: {
    port: 8081, // 本地调试端口，可避免和 Spring Boot 冲突
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Spring Boot 后端接口
        changeOrigin: true
      }
    }
  }
};