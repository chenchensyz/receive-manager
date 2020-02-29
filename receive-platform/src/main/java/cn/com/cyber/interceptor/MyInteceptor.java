package cn.com.cyber.interceptor;

import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.MessageCodeUtil;
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

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //api后台接口拦截器
        String webExcludePath = messageCodeUtil.getMessage("interceptor.apiExcludePath");
        String[] apiExcludeArr = webExcludePath.split(",");
        registry.addInterceptor(new ApiInterceptor()).addPathPatterns("/api1/**").excludePathPatterns(apiExcludeArr);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        String fileRootPath = environment.getProperty(CodeUtil.FILE_ROOT_PATH);
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

