package nl.hu.inno.thuusbezorgd.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * In development mode, the session gets saved to address database. But the JPA parts of the database may be reset.
 * So it's possible (nay likely) the session will contain users from last run that don't exist anymore in JPA.
 * <p>
 * That's ...weird and not something we'd want to support in production, but logging back in every time is annoying too.
 * This just recreates the user that was active last time
 */
@Component
@Profile("dev")
public class DevRecreateUserFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(DevForceUserFilter.class);
    private final GithubUserRepository users;
    private final DbUserRepository dbUsers;

    public DevRecreateUserFilter(GithubUserRepository users, DbUserRepository dbUsers){

        this.users = users;
        this.dbUsers = dbUsers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Principal p = request.getUserPrincipal();

        if(p instanceof OAuth2AuthenticationToken){
            OAuth2User innerPrincipal = ((OAuth2AuthenticationToken) p).getPrincipal();
            log.debug("Recreating Githubuser " + innerPrincipal.getName());
            if(innerPrincipal instanceof GithubUser){
                users.save((GithubUser) innerPrincipal);
            }
        }
        if(p instanceof TestingAuthenticationToken){
            log.debug("Recreating DbUser " + p.getName());
            dbUsers.save(new DbUser(p.getName(), "lala"));
        }

        filterChain.doFilter(request, response);
    }
}
