package com.we.weblog.config;

import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.web.handler.SSOHandlerInterceptor;
import com.baomidou.kisso.web.interceptor.SSOSpringInterceptor;
import com.vue.adminlte4j.model.UIModel;
import com.we.weblog.web.intercaptor.InstallInterceptor;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  <pre>
 *     拦截器，资源路径配置
 * </pre>
 *总结：大家在使用2.0版本的springboot的时候 使用WebMvcConfigurationSupport类配置拦截器时一定要重写addResourceHandlers来实现静态资源的映射,不要使用application.properties中添加配置来实现映射，不然资源会映射不成功导致打开页面资源一直加载不到。会出现下面这种奇怪的问题
 * @author tangwei
 * @date 2018/10/18 17:13
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.we.weblog.web.controller")
@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true, encoding = "UTF-8")
public class WebMainConig implements WebMvcConfigurer {

    @Resource
    private InstallInterceptor installInterceptor;

    private String LOGIN_URL = "/login.html" ;

    /**ser
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
////        registry.addInterceptor(installInterceptor)
////                .addPathPatterns("/admin/**")
////                .excludePathPatterns("/index")
////                .excludePathPatterns("/static/**")
////                .excludePathPatterns("/install");
//
        //单点登录拦截器
        registry.addInterceptor(ssoInterceptor()).addPathPatterns("/admin/**");
   }

    /**
     * 配置静态资源路径
     *
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //不加的话无法加载静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/fonts")
                .addResourceLocations("classpath:/static/fonts/");

        registry.addResourceHandler("/loading.png")
                .addResourceLocations("classpath:/static/images/loading.png");

        registry.addResourceHandler("/font.jpg")
                .addResourceLocations("classpath:/templates/source/images/font.jpg");
        registry.addResourceHandler("/friend_links.jpg")
                .addResourceLocations("classpath:/templates/source/images/friend_links.jpg");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/templates/source/");
        //添加jar包中静态资源的方法
        registry.addResourceHandler("/lib/**")
                .addResourceLocations("classpath:/META-INF/resources/lib/");

    }


    /**
     * 配置单点登录拦截
     * @return
     */
    @Bean(autowire = Autowire.BY_NAME )
    public SSOSpringInterceptor ssoInterceptor() {

        SSOConfig.getInstance().setLoginUrl(LOGIN_URL);
        SSOConfig.getInstance().setLogoutUrl("/logout.html");

        SSOSpringInterceptor springSSOInterceptor =  new SSOSpringInterceptor();
        springSSOInterceptor.setHandlerInterceptor(new SSOHandlerInterceptor() {
            @Override public boolean preTokenIsNullAjax(HttpServletRequest request, HttpServletResponse response) {
                try {
                    UIModel uiModel = UIModel.success().isLogin(false).setLoginUrl(LOGIN_URL);
                    response.getWriter().write(uiModel.toJsonString());

                } catch (IOException e) {
                    // to do nothing
                }
                return false;
            }
            @Override public boolean preTokenIsNull(HttpServletRequest request
                    ,HttpServletResponse response) {
                return true;
            }
        });
        return springSSOInterceptor;
    }

}
