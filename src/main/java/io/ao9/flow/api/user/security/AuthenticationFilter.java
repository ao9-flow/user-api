package io.ao9.flow.api.user.security;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.ao9.flow.api.user.service.UserService;
import io.ao9.flow.api.user.shared.UserDto;
import io.ao9.flow.api.user.ui.model.LoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private Environment env;
    private UserService userService;

    // no @Autowired is needed because it's not a @Component class
    // nor a @Bean method.
    // the parameters are set in WebSecurity when constructing
    public AuthenticationFilter(Environment env, UserService userService, AuthenticationManager authenticationManager) {
        this.env = env;
        this.userService = userService;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestModel loginRequestModel = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequestModel.getEmail(), 
                    loginRequestModel.getPassword(),
                    new ArrayList<>()
                )
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain chain,
                        Authentication authResult
                        ) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDto outputUserDto = userService.findUserByEmail(username);

        String token = Jwts.builder()
                .setSubject(outputUserDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration-time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret") )
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", outputUserDto.getUserId());
    }
}