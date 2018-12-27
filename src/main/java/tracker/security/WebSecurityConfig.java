package tracker.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //TODO implement security for rest calls: https://www.baeldung.com/securing-a-restful-web-service-with-spring-security

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin();

        // do i really want to disable csrf? did it because 403 on post
        http.csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth ) throws Exception {
        auth.ldapAuthentication().userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource().url("ldap://localhost:8389/dc=springframework,dc=org")
                .and()
                .passwordCompare().passwordEncoder(new LdapShaPasswordEncoder())
                .passwordAttribute("userPassword");
    }
}
