package ru.poteriashki.laf.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.poteriashki.laf.core.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Date;

@Component("securityFilter")
public class SecurityFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private UserContext userContext;

    public SecurityFilter() {
        logger.debug("Security filter created");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        filterConfig.getFilterName();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
/*        User user = userContext.getUser();
        userContext.setCheckedAt(new Date());
        if (user == null) {
            logger.debug("User context not initialized.");
            returnError(servletResponse);
            return;
        } else if (user.getUid() == null) {
            logger.debug("Access token not initialized.");
            returnError(servletResponse);
            return;
        } else {
            //todo Check token expire time
        }
        filterChain.doFilter(servletRequest, servletResponse);*/
    }


    private void returnError(ServletResponse servletResponse) throws IOException {
        logger.debug("return error status");
        servletResponse.setContentType("application/json");
//        new ObjectMapper().writeValue(servletResponse.getWriter(), Status.ACCESS_DENIED);
        servletResponse.getWriter().flush();
    }


    @Override
    public void destroy() {

    }
}
