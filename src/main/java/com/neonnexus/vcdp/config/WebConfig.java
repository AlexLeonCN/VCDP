package com.neonnexus.vcdp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web 配置类 - 支持前后端一体部署
 * 确保 Vue Router 的 history 模式能正常工作
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(false)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        // API 与 H2 控制台交给对应处理器，缺失的静态文件返回 404
                        if (resourcePath.startsWith("api/")
                                || resourcePath.startsWith("h2-console")
                                || hasFileExtension(resourcePath)) {
                            return null;
                        }

                        // 无扩展名的前端路由回退到 index.html
                        return new ClassPathResource("/static/index.html");
                    }

                    private boolean hasFileExtension(String resourcePath) {
                        int lastSlash = resourcePath.lastIndexOf('/');
                        String filename = lastSlash >= 0 ? resourcePath.substring(lastSlash + 1) : resourcePath;
                        return filename.contains(".");
                    }
                });
    }
}
