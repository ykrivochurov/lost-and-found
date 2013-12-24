package ru.eastbanctech.board.web.config.jsonview;

import ru.eastbanctech.board.core.dtos.BaseView;

/**
 * @author y.bulkin
 */
public interface DataView {
    boolean hasView();
    Class<? extends BaseView> getView();
    Object getData();

}
