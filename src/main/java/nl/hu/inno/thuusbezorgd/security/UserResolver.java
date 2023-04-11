package nl.hu.inno.thuusbezorgd.security;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Dit is natuurlijk -juist geen- security, maar ik merkte dat hier nette voorbeeldcode van aanleveren meer was dan de rest
 * van de applicatie bij elkaar. Dus dat leek me een beetje onhandig
 */
public class UserResolver implements HandlerMethodArgumentResolver {

    private final UserRepository users;

    public UserResolver(UserRepository users) {
        this.users = users;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameter().getType() == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String username = webRequest.getHeader("Authentication-Hack");
        Exception unauthorized = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user found, use Authentication-Hack header");
        if (username == null) {
            throw unauthorized;
        }
        Optional<User> result = users.findById(username);

        return result.orElseThrow(() -> unauthorized);
    }
}
