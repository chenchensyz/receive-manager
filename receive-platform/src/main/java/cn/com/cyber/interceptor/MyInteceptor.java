package cn.com.cyber.interceptor;

import cn.com.cyber.util.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyInteceptor implements WebMvcConfigurer {


    @Autowired
    private Environment environment;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthCheckInterceptor()).addPathPatterns("/apisd");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        String fileRootPath = environment.getProperty(CodeUtil.FILE_ROOT_PATH);
        System.out.println(fileRootPath);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/file/**").addResourceLocations("file:" + fileRootPath);
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowCredentials(true).allowedHeaders("Origin, X-Requested-With, Content-Type, Accept")
//                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS");
//    }

}

