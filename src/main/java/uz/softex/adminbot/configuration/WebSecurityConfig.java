package uz.softex.adminbot.configuration;

import com.github.mkopylec.recaptcha.security.login.FormLoginConfigurerEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private FormLoginConfigurerEnhancer formLoginConfigurerEnhancer;

    public WebSecurityConfig(FormLoginConfigurerEnhancer formLoginConfigurerEnhancer) {
        this.formLoginConfigurerEnhancer = formLoginConfigurerEnhancer;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // Defines three users with their passwords and roles
                .inMemoryAuthentication()
                .withUser("UserA").password(passwordEncoder().encode("UserA")).roles("USER")
                .and()
                .withUser("UserB").password("UserB").roles("USER")
                .and()
                .withUser("UserC").password("UserC").roles("USER")
                .and()
                .passwordEncoder(passwordEncoder());
        return;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection
                .csrf().disable()
                // Set default configurations from Spring Security
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
        return;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
