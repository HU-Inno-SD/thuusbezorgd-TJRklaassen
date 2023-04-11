package nl.hu.inno.thuusbezorgd.security;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Profile("dev")
public class DevForceUserFilter extends OncePerRequestFilter {

    private final UserRepository users;

    public DevForceUserFilter(UserRepository users){
        this.users = users;
    }

    private final Logger log = LoggerFactory.getLogger(DevForceUserFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authentication-Hack");
        if (authHeader != null) {
            log.debug("Overriding Auth for " + authHeader);
            BaseUser user = this.users.findById(authHeader).orElseThrow();

            Authentication authResult = new UsernamePasswordAuthenticationToken(authHeader, authHeader, user.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }
}
