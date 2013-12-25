package ru.poteriashki.laf.web.config;

import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: a.zhukov
 * Date: 01.08.13
  */
public class CustomHttpSessionEventPublisher extends HttpSessionEventPublisher {
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        //event.getSession().setMaxInactiveInterval(20);

        super.sessionCreated(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        super.sessionDestroyed(event);
    }
}
