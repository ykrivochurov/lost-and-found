package ru.eastbanctech.board.core.dao;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 12:10
 */
public class CustomNamingStrategy extends ImprovedNamingStrategy {

    public static final String TABLE_PREFIX = "tbl_";

    public static final String COLUMN_PREFIX = "c_";

    @Override
    public String tableName(String tableName) {
        return TABLE_PREFIX + super.tableName(tableName);
    }

    @Override
    public String columnName(String columnName) {

        return COLUMN_PREFIX + super.columnName(columnName);
    }
}
