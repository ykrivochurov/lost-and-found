package ru.poteriashki.laf.web.config.jsonview;

import ru.poteriashki.laf.core.dtos.BaseView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author y.bulkin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseView {
    public Class<? extends BaseView> value();
}
