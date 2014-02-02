package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = City.COLLECTION)
@JsonDeserialize
public class City extends BaseEntity {

    public static final String COLLECTION = "city";

    private String name;
    private Double[] center; // lng, lat
    private Double[] leftBottom;
    private Double[] rightTop;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double[] getCenter() {
        return center;
    }

    public void setCenter(Double[] center) {
        this.center = center;
    }

    public Double[] getLeftBottom() {
        return leftBottom;
    }

    public void setLeftBottom(Double[] leftBottom) {
        this.leftBottom = leftBottom;
    }

    public Double[] getRightTop() {
        return rightTop;
    }

    public void setRightTop(Double[] rightTop) {
        this.rightTop = rightTop;
    }
}