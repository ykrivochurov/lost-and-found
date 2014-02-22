package ru.poteriashki.laf.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = TempResource.COLLECTION)
@JsonDeserialize
public class TempResource extends BaseEntity {

    public static final String COLLECTION = "tempResource";

    private String fileId;

    private Date creationDate;

    public TempResource() {
    }

    public TempResource(String fileId, Date creationDate) {
        this.fileId = fileId;
        this.creationDate = creationDate;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
