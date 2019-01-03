package tracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import tracker.web.LoggingAccessDeniedHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;

    //TODO implement security for rest calls: https://www.baeldung.com/securing-a-restful-web-service-with-spring-security

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin();
//
//        // do i really want to disable csrf? did it because 403 on post
//        http.csrf().disable();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/js/**", "/css/**", "/img/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .csrf();
    }

//    @Override
//    public void configure(AuthenticationManagerBuilder auth ) throws Exception {
//        auth.ldapAuthentication().userDnPatterns("uid={0},ou=people")
//                .groupSearchBase("ou=groups")
//                .contextSource().url("ldap://localhost:8389/dc=springframework,dc=org")
//                .and()
//                .passwordCompare().passwordEncoder(new LdapShaPasswordEncoder())
//                .passwordAttribute("userPassword");
//    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username, password, 1 from application_users where username=?")
                .authoritiesByUsernameQuery("select username, role from application_users au " +
                        "inner join application_users_user_roles map on map.user_entity_id = au.id " +
                        "inner join user_roles ur on ur.id = map.user_roles_id " +
                        "where au.username=?").passwordEncoder(passwordEncoder());
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
