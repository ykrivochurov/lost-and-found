package ru.poteriashki.laf.core.repositories;

import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFSDBFile;
import ru.poteriashki.laf.core.service.ServiceException;

import java.io.InputStream;

public interface IGridFSDao {

    static final String BUCKET = "resource";

    GridFSDBFile loadFile(final String fileId) throws ServiceException;

    String saveFile(InputStream is, String originalFileName, String contentType) throws ServiceException;

    void deleteFile(String id) throws ServiceException;

    DBCursor getList();

    void deleteFileByName(String name);

}
