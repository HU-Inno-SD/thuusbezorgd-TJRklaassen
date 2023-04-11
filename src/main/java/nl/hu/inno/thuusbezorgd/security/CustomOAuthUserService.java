package nl.hu.inno.thuusbezorgd.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

public class CustomOAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final Logger log = LoggerFactory.getLogger(CustomOAuthUserService.class);

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> wrappedService;
    private final GithubUserRepository users;

    public CustomOAuthUserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oauthService, GithubUserRepository users) {
        this.wrappedService = oauthService;
        this.users = users;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("Getting user info");
        OAuth2User result = wrappedService.loadUser(userRequest);
        Number id = result.getAttribute("id");
        Assert.notNull(id, "Github id should not be null!");

        Optional<GithubUser> maybeUser = this.users.findByGithubId(id.longValue());
        log.debug("Saving user info");
        GithubUser user;

        if(maybeUser.isEmpty()){
            user = this.users.save(new GithubUser(id.longValue(), result.getAttribute("login"), result.getAttribute("name")));
        }else{
            user = maybeUser.get();
            //Number, Int, Long weirdness which messes with equals...
            long ghId = user.getGithubId();
            long oAuthId = id.longValue();
            if(ghId != oAuthId){
                throw new RuntimeException("Conflicting ids, %s vs. %s for user %s".formatted(user.getGithubId(), id, user.getName()));
            }
        }

        user.markLogin();

        return user;
    }
}
