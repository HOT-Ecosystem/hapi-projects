package com.essaid.hapi.webui;

import ca.uhn.fhir.to.FhirTesterMvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@Import(WebUiConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);

        application.run(args);
    }

    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    @Bean
    public ServletRegistrationBean overlayRegistrationBean() {

        AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
        annotationConfigWebApplicationContext.register(WebUiConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(
                annotationConfigWebApplicationContext);
        dispatcherServlet.setContextClass(AnnotationConfigWebApplicationContext.class);
        dispatcherServlet.setContextConfigLocation(WebUiConfig.class.getName());

        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(dispatcherServlet);
        registrationBean.addUrlMappings("/*");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

}
