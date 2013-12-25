package ru.poteriashki.laf.web.config.jsonview;

import ru.poteriashki.laf.core.dtos.BaseView;

/**
 * @author y.bulkin
 */
public interface DataView {
    boolean hasView();
    Class<? extends BaseView> getView();
    Object getData();

}
