package com.sportclub.sportclub.security;
import com.sportclub.sportclub.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;
@Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }
//    @Bean
  /*  public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("1234")).roles("USER", "ADMIN").build()

        );
    }
*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

   /*  httpSecurity.authorizeHttpRequests().requestMatchers("/coach/*").hasAuthority("READ_USER").anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout();
        httpSecurity.authorizeHttpRequests().requestMatchers("/admin/*").hasRole("ADMIN").anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout();*/

        httpSecurity.formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll();

        httpSecurity.authorizeHttpRequests().requestMatchers("/*","/abonnementList/**","/membersList/**","/coachList/**","/addMember","/webjars/**","/css/**","/js/**","/uploads/**","/images/**","/favicon.ico","/node_modules/**","/calendar","/search","/coachList/images").permitAll();
        httpSecurity.authorizeHttpRequests().requestMatchers("/membersList").hasAuthority("COACH");

    httpSecurity.logout().logoutSuccessUrl("/login");
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();
        httpSecurity.exceptionHandling().accessDeniedPage("/notAuthorized");
        httpSecurity.userDetailsService(userDetailsServiceImp);
//        httpSecurity.rememberMe();
        return httpSecurity.build();
    }
}
