package main.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import main.bean.user.MyUserDetailsService;
import main.security.filter.StatelessAuthenticationFilter;
import main.security.filter.StatelessLoginFilter;
import main.security.token.TokenAuthenticationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	
	@Resource(name="authService")
    private MyUserDetailsService myUserDetailsService;
 
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
    
    /**
	 * This section defines the user accounts which can be used for
	 * authentication as well as the roles each user has.
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

        BCryptPasswordEncoder encoder = passwordEncoder();
        auth
			.userDetailsService(myUserDetailsService)
			.passwordEncoder(encoder);
//		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER").and().withUser("admin")
//				.password("admin").roles("USER", "ADMIN");
		
	}

	private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

	}

	/**
	 * This section defines the security policy for the app. - BASIC
	 * authentication is supported (enough for this REST-based demo) -
	 * /employees is secured using URL security shown below - CSRF headers are
	 * disabled since we are only testing the REST interface, not a web one.
	 *
	 * NOTE: GET is not shown which defaults to permitted.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//TODO change findAll to admin
				http
				.csrf().disable()
				.exceptionHandling().and()
				.anonymous().and()
				.headers().cacheControl();
				http
				.servletApi().and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/spring/register").permitAll()
				.antMatchers(HttpMethod.POST, "/login**").permitAll()
				.antMatchers(HttpMethod.POST, "/logout**").permitAll()
				.antMatchers(HttpMethod.PUT, "/spring/updateSave").hasRole("USER")
				.antMatchers(HttpMethod.PUT, "/spring/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.POST, "/spring/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.PATCH, "/spring/**").hasRole("ADMIN")
				//all other request need to be authenticated
				.anyRequest().hasRole("USER").and()				
				.addFilterBefore(new StatelessLoginFilter("/api/login", tokenAuthenticationService, myUserDetailsService, authenticationManager()), UsernamePasswordAuthenticationFilter.class)

				// custom Token based authentication based on the header previously given to the client
				.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class);
}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}


	@Override
	protected MyUserDetailsService userDetailsService() {
		return myUserDetailsService;
	}
}
