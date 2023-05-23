package de.yatta.ideintruders.backend;

import java.util.Arrays;

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

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
   {
      String defaultUrl = "http://localhost:62348/ide-intruders/index.html";
      String defaultLogoutUrl = "http://localhost:62348/vendor-login/";

      httpSecurity.authorizeHttpRequests()
            .requestMatchers("/public", "/queryLicense").permitAll()
            .anyRequest().authenticated()
            .and()
            .cors().and()
            .oauth2Login().defaultSuccessUrl(defaultUrl).and()
            .logout(logout -> logout.logoutSuccessUrl(defaultLogoutUrl));

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
