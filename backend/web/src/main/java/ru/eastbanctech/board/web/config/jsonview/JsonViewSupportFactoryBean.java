package ru.eastbanctech.board.web.config.jsonview;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

/**
 * @author y.bulkin
 */
public class JsonViewSupportFactoryBean implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(JsonViewSupportFactoryBean.class);

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        HandlerMethodReturnValueHandlerComposite returnValueHandlers = adapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers = Lists.newArrayList(returnValueHandlers.getHandlers());
        decorateHandlers(handlers);
        adapter.setReturnValueHandlers(handlers);
    }
    private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor)
            {
                ViewInjectingReturnValueHandler decorator = new ViewInjectingReturnValueHandler(handler);
                int index = handlers.indexOf(handler);
                handlers.set(index, decorator);
                log.info("JsonView decorator support wired up");
                break;
            }
        }
    }

}
