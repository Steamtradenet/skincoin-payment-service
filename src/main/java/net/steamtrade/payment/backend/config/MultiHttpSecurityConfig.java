package net.steamtrade.payment.backend.config;

import net.steamtrade.payment.web.security.filters.ApiFilter;
import net.steamtrade.payment.web.security.filters.JwtFilter;
import net.steamtrade.payment.web.security.RestAuthenticationEntryPoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;


/**
 * Created by sasha on 5/4/17.
 */
@EnableWebSecurity
public class MultiHttpSecurityConfig {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
        @Autowired
        private ApiFilter apiFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**")
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
                    .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(apiFilter, UsernamePasswordAuthenticationFilter.class)
                    .headers()
                        .frameOptions().sameOrigin().and()
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .antMatchers("/api/ethereum/getInfo").permitAll()
                    .anyRequest().authenticated();
        }
   }

    @Configuration
    public static class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
        @Autowired
        private JwtFilter jwtFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
                    .addFilterBefore(jwtFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/",
                            "/login", "/build/**", "/fonts/**", "/images/**",
                            "/css/**", "/favicon.ico").permitAll()
                    .anyRequest().authenticated();
        }
    }

//    @Bean
//    public ServletRegistrationBean dispatcherServlet(){
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new DispatcherServlet(getContext()));
//        registrationBean.setName("rest-dispatcher");
//        registrationBean.addUrlMappings("/");
////        registrationBean.setLoadOnStartup(1);
//        return registrationBean;
//    }
//
//    @Bean
//    public FilterRegistrationBean characterEncodingFilter() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
//        characterEncodingFilter.setForceEncoding(true);
//        characterEncodingFilter.setEncoding("UTF-8");
//        registrationBean.setFilter(characterEncodingFilter);
//        registrationBean.setOrder(2);
//        return registrationBean;
//    }
//
    public static Filter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
