package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.OrderBy;
import java.util.List;

@Document(collection = Category.COLLECTION)
@JsonDeserialize
public class Category extends BaseEntity {

    public static final String COLLECTION = "category";

    private String name;

    private List<String> tags;

    @OrderBy
    private Integer priority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
