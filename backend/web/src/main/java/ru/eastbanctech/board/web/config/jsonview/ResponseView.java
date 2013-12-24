package ru.eastbanctech.board.web.config.jsonview;

import ru.eastbanctech.board.core.dtos.BaseView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author y.bulkin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseView {
    public Class<? extends BaseView> value();
}
