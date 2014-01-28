package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.OrderBy;
import java.util.Set;

@Document(collection = Category.COLLECTION)
@JsonDeserialize
public class Category extends BaseEntity {

    public static final String COLLECTION = "category";

    private String name;

    private Set<String> tags;

    @OrderBy
    private Integer priority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
