package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.to.mvc.AnnotationMethodHandlerAdapterConfigurer;
import ca.uhn.fhir.to.util.WebUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ca.uhn.fhir.to", excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ca.uhn.fhir.to.FhirTesterMvcConfig.class)})
public class FhirTesterMvcConfigLocal implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry theRegistry) {
		WebUtil.webJarAddBoostrap(theRegistry);
		WebUtil.webJarAddJQuery(theRegistry);
		WebUtil.webJarAddFontAwesome(theRegistry);
		WebUtil.webJarAddJSTZ(theRegistry);
		WebUtil.webJarAddEonasdanBootstrapDatetimepicker(theRegistry);
		WebUtil.webJarAddMomentJS(theRegistry);
		WebUtil.webJarAddSelect2(theRegistry);
		WebUtil.webJarAddAwesomeCheckbox(theRegistry);
		WebUtil.webJarAddPopperJs(theRegistry);

		theRegistry.addResourceHandler("/css/**").addResourceLocations("classpath:/static-local/css/","classpath:/static/css/","classpath:/static-overlay/css/");
		theRegistry.addResourceHandler("/fa/**").addResourceLocations("/fa/");
		theRegistry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/");
		theRegistry.addResourceHandler("/img/**").addResourceLocations("classpath:/static-local/img/","classpath:/static/img/","classpath:/static-overlay/img/");
		theRegistry.addResourceHandler("/js/**").addResourceLocations("classpath:/static-local/js/","classpath:/static/js/","classpath:/static-overlay/js/");
	}

	@Bean
	public SpringResourceTemplateResolver templateLocalResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("/WEB-INF/templates-local/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCheckExistence(true);
		return resolver;
	}

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("/WEB-INF/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCheckExistence(true);
		return resolver;
	}


	@Bean
	public SpringResourceTemplateResolver templateOverlayuResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("/WEB-INF/templates-overlay/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public AnnotationMethodHandlerAdapterConfigurer annotationMethodHandlerAdapterConfigurer() {
		return new AnnotationMethodHandlerAdapterConfigurer();
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setCharacterEncoding("UTF-8");
		return viewResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(templateLocalResolver());
		templateEngine.addTemplateResolver(templateResolver());
		templateEngine.addTemplateResolver(templateOverlayuResolver());

		return templateEngine;
	}

}
