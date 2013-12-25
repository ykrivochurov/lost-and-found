package ru.poteriashki.laf.web.config.jsonview;

import ru.poteriashki.laf.core.dtos.BaseView;

/**
 * @author y.bulkin
 */
public class PojoView implements DataView {

    private final Object data;
    private final Class<? extends BaseView> view;

    public PojoView(Object data, Class<? extends BaseView> view) {

        this.data = data;
        this.view = view;
    }

    @Override
    public boolean hasView() {
        return true;
    }

    public Object getData() {

        return data;
    }

    public Class<? extends BaseView> getView() {

        return view;
    }
}
