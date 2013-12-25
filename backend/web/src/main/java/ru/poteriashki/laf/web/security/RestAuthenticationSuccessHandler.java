package ru.poteriashki.laf.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;
import ru.poteriashki.laf.web.controllers.dtos.LoginStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: y.bulkin
 */
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            updateResponse(response);
            clearAuthenticationAttributes(request);
            return;
        }

        String targetUrlParam = getTargetUrlParameter();

        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParam != null && StringUtils.hasText(
                request.getParameter(targetUrlParam)))) {
            requestCache.removeRequest(request, response);
            updateResponse(response);
            clearAuthenticationAttributes(request);
            return;
        }

        updateResponse(response);
        clearAuthenticationAttributes(request);

    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        LoginStatus status = new LoginStatus("success", true, auth.getName());
        ObjectMapper om = new ObjectMapper();
        om.writeValue(response.getOutputStream(), status);
    }
}
