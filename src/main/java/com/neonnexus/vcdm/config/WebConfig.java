package com.neonnexus.vcdm.config;

import com.neonnexus.vcdm.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web 配置类 - 支持前后端一体部署
 * 确保 Vue Router 的 history 模式能正常工作
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // 拦截所有 API 请求
                .excludePathPatterns(
                        "/api/login",      // 排除登录接口
                        "/api/register",   // 排除注册接口
                        "/api/logout"     // 排除登出接口（仅用于记录请求日志）
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // API 路径不处理，交给 Controller
        // 静态资源（js、css、图片等）从 /static 目录提供
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(false)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // 如果请求的资源存在，直接返回
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // 如果请求的是 API 路径，不处理（交给 Controller）
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        
                        // 其他所有请求都返回 index.html（支持 Vue Router 的 history 模式）
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}

