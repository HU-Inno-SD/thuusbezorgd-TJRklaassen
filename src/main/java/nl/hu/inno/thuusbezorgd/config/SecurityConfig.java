package nl.hu.inno.thuusbezorgd.config;

import nl.hu.inno.thuusbezorgd.security.CustomOAuthUserService;
import nl.hu.inno.thuusbezorgd.security.DevForceUserFilter;
import nl.hu.inno.thuusbezorgd.security.DevRecreateUserFilter;
import nl.hu.inno.thuusbezorgd.security.GithubUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    private void configureH2Console(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().antMatchers("/h2-console/*").permitAll();
        http.csrf().ignoringAntMatchers("/h2-console/*");
        http.headers().frameOptions().sameOrigin();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> springOAuthService(GithubUserRepository users) {
        return new CustomOAuthUserService(new DefaultOAuth2UserService(), users);
    }


    @Bean
    @Profile("!dev")
    public SecurityFilterChain secureFilterChain(HttpSecurity http) throws Exception {
        return basicFilterChain(http);
    }

     public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        if (environment.acceptsProfiles(Profiles.of("h2"))) {
            configureH2Console(http);
        }

        if (!environment.acceptsProfiles(Profiles.of("csrf"))) {
            http.csrf().ignoringAntMatchers("**");
        }

        http.authorizeHttpRequests().antMatchers("/stock/**").hasRole("ADMIN");
         http.authorizeHttpRequests().antMatchers("/orders/report").hasRole("ADMIN");
        http.authorizeHttpRequests().anyRequest().hasRole("USER");

        http.formLogin();
        http.oauth2Login();
        return http.build();
    }


    @Bean
    @Profile("dev")
    public SecurityFilterChain filterChain(HttpSecurity http, DevForceUserFilter devFilter, DevRecreateUserFilter recreateUserFilter) throws Exception {
        http.addFilterBefore(recreateUserFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(devFilter, UsernamePasswordAuthenticationFilter.class);
        return basicFilterChain(http);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance(); //Deprecated, etc, dus echt echt echt niet in productie gebruiken!
    }
}
