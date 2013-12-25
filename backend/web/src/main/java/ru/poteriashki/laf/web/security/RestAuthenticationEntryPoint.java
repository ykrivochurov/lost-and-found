package ru.poteriashki.laf.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import ru.poteriashki.laf.web.controllers.dtos.ExceptionDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
public class RestAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    //private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    public RestAuthenticationEntryPoint(String loginUrl) {
        super(loginUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ExceptionDTO ex = new ExceptionDTO();
        ex.setMessage("Unauthorized");
        ObjectMapper om = new ObjectMapper();
        om.writeValue(response.getOutputStream(), ex);
    }
}
