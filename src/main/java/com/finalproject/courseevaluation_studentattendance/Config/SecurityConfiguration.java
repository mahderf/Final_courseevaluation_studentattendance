package com.finalproject.courseevaluation_studentattendance.Config;


import com.finalproject.courseevaluation_studentattendance.Repositories.PersonRepository;
import com.finalproject.courseevaluation_studentattendance.Services.SSPersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{


    @Autowired
    private SSPersonDetailsService personService;
    @Autowired
    private PersonRepository personRepository;
    @Override
    public UserDetailsService userDetailsServiceBean()throws Exception{
        return new SSPersonDetailsService(personRepository);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/img/**","/css/**","/","/register","/js/**","/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/teacher/**").hasRole("TEACHER")
                .and()
                .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").permitAll().permitAll()
                .and()
                .httpBasic();
        http
                .csrf().disable();
        http
                .headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("ADMIN").password("password").roles("ADMIN");
        auth.userDetailsService(userDetailsServiceBean());
    }
}
