package de.yatta.ideintruders.backend;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
   @Value("${server.servlet.session.cookie.name}")
   private String cookieName;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
   {
      String defaultUrl = "http://localhost:62348/ide-intruders/index_no_iam.html";
      String defaultLoginUrl = "http://localhost:62348/vendor-login/";

      httpSecurity
            .authorizeHttpRequests(requestMatcher -> requestMatcher
                  .requestMatchers("/public", "/queryLicense", "/login").permitAll()
                  .anyRequest().authenticated())
            .cors().and()
            .csrf().disable()
            .oauth2Login(config -> config
                  .defaultSuccessUrl(defaultUrl)
                  .loginPage(defaultLoginUrl)
                  .isCustomLoginPage())
            .logout(logout -> logout
                  .logoutSuccessUrl(defaultLoginUrl)
                  .permitAll()
                  .deleteCookies(cookieName));

      return httpSecurity.build();
   }

   @Bean
   CorsConfigurationSource corsConfigurationSource()
   {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOriginPatterns(Arrays.asList("*"));
      configuration.setAllowedMethods(Arrays.asList("*"));
      configuration.setAllowedHeaders(Arrays.asList("*"));
      configuration.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
   }
}
