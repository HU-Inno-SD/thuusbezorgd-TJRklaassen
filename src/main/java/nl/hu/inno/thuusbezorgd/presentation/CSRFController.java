package nl.hu.inno.thuusbezorgd.presentation;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CSRFController {

    public record CSRFResponse(String token, String headerName, String paramName){}


    @GetMapping("/csrf")
    public CSRFResponse getToken(CsrfToken token){
        return new CSRFResponse(token.getToken(), token.getHeaderName(), token.getParameterName());
    }
}
