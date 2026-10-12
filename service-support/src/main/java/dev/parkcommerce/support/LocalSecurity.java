package dev.parkcommerce.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

/** Local-only opaque bearer credentials. This is not a production identity provider. */
@Configuration(proxyBeanMethods=false)
@EnableMethodSecurity
public class LocalSecurity {
    @Bean Clock clock() { return Clock.tickMillis(java.time.ZoneOffset.UTC); }

    @Bean @Profile("!local")
    SecurityFilterChain closedByDefault(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> a.anyRequest().denyAll()).build();
    }

    @Bean @Profile("local")
    SecurityFilterChain local(HttpSecurity http, Environment env) throws Exception {
        List<Credential> credentials = List.of(
            credential(env,"DEMO_EMPLOYEE_TOKEN","employee","EMPLOYEE"),
            credential(env,"DEMO_GUEST_TOKEN","guest","GUEST"),
            credential(env,"DEMO_OTHER_GUEST_TOKEN","other-guest","GUEST"),
            credential(env,"DEMO_SERVICE_TOKEN","service","INTERNAL"));
        if(credentials.stream().map(Credential::token).distinct().count()!=credentials.size())
            throw new IllegalStateException("Demo credentials must be distinct");
        var filter = new BearerFilter(credentials);
        // No cookies, sessions, or HTTP Basic. Browsers never attach bearer credentials implicitly.
        http.csrf(c -> c.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(c -> c.disable()).formLogin(c -> c.disable()).httpBasic(c -> c.disable())
            .authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health", "/actuator/health/**", "/error").permitAll()
                .requestMatchers("/actuator/prometheus").hasRole("INTERNAL")
                .requestMatchers("/api/admin/**").hasRole("EMPLOYEE")
                .requestMatchers("/api/internal/**").hasRole("INTERNAL")
                .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/products", "/api/products/**", "/api/events/*/availability").permitAll()
                .requestMatchers("/graphql").permitAll() // Private fields additionally require method authorization.
                .requestMatchers("/api/**").hasAnyRole("GUEST","EMPLOYEE")
                .anyRequest().denyAll())
            .exceptionHandling(e -> e.authenticationEntryPoint((req,res,err) -> problem(res,401,"UNAUTHENTICATED"))
                .accessDeniedHandler((req,res,err) -> problem(res,403,"FORBIDDEN")))
            .addFilterBefore(filter, AnonymousAuthenticationFilter.class);
        return http.build();
    }
    private record Credential(String token,String subject,String role) {}
    private Credential credential(Environment env,String property,String subject,String role) {
        String value=env.getRequiredProperty(property);
        if(!value.matches("[A-Za-z0-9_-]{32,128}")) throw new IllegalStateException(property+" must be a random 32-128 character token");
        return new Credential(value,subject,role);
    }
    private static void problem(HttpServletResponse response,int status,String code) throws IOException {
        response.setStatus(status);response.setContentType("application/problem+json");
        response.getWriter().write("{\"status\":"+status+",\"title\":\""+code+"\",\"code\":\""+code+"\"}");
    }
    private static final class BearerFilter extends OncePerRequestFilter {
        private final List<Credential> credentials;
        BearerFilter(List<Credential> credentials){this.credentials=credentials;}
        @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
            String header=req.getHeader("Authorization");
            if(header!=null && header.startsWith("Bearer ")){
                String token=header.substring(7);
                for(Credential credential:credentials){
                    if(MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),credential.token().getBytes(StandardCharsets.UTF_8))){
                        var context=SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(credential.subject(),null,
                            AuthorityUtils.createAuthorityList("ROLE_"+credential.role())));
                        SecurityContextHolder.setContext(context);break;
                    }
                }
            }
            chain.doFilter(req,res);
        }
    }
}
