package ru.poteriashki.laf.core.model;

import java.io.Serializable;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:14
 */
public interface IEntity extends Serializable {

    String getId();

    void setId(String id);

}