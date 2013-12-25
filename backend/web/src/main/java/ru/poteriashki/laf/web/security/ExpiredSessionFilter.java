package ru.poteriashki.laf.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import ru.poteriashki.laf.web.controllers.dtos.ExceptionDTO;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User: a.zhukov
 */
@Component
public class ExpiredSessionFilter  extends GenericFilterBean {

    static final String FILTER_APPLIED = "__spring_security_expired_session_filter_applied";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        Assert.isInstanceOf(HttpServletRequest.class, servletRequest, "Can only process HttpServletRequest");
        Assert.isInstanceOf(HttpServletResponse.class, servletResponse, "Can only process HttpServletResponse");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession httpSession = request.getSession(false);

        if (request.getRequestURI().startsWith("/api") &&
            !request.getRequestURI().startsWith("/api/login") && !request.getRequestURI().startsWith("/api/logout") &&
            httpSession == null && request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ExceptionDTO ex = new ExceptionDTO();
            ex.setMessage("Session is expired");
            ObjectMapper om = new ObjectMapper();
            om.writeValue(response.getOutputStream(), ex);
            return;
        }
        chain.doFilter(request, response);
    }
}
