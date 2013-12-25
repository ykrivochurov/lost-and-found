package ru.poteriashki.laf.core.repositories.impl;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.poteriashki.laf.core.repositories.IGridFSDao;
import ru.poteriashki.laf.core.service.ErrorType;
import ru.poteriashki.laf.core.service.ServiceException;

import java.io.InputStream;

public class GridFSDao implements IGridFSDao {

    private static Logger logger = LoggerFactory.getLogger(GridFSDao.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    private GridFS gridFS;

    private void init() {
        gridFS = mongoTemplate.execute(new DbCallback<GridFS>() {
            @Override
            public GridFS doInDB(DB db) throws MongoException, DataAccessException {
                return new GridFS(db, BUCKET);
            }
        });
    }

    @Override
    public DBCursor getList() {
        return gridFS.getFileList();
    }


    @Override
    public GridFSDBFile loadFile(final String fileId) throws ServiceException {
        try {
            return gridFS.findOne(new ObjectId(fileId));
        } catch (Exception e) {
            logger.error("Can not retrieve InputStream for fileId :", fileId);
            throw new ServiceException(ErrorType.DATABASE_ERROR, "Can not retrieve InputStream for fileId " + fileId, e);
        }
    }

    @Override
    public String saveFile(InputStream is, String originalFileName, String contentType) throws ServiceException {
        try {
            GridFSInputFile file = gridFS.createFile(is);
            file.setFilename(originalFileName);
            file.setContentType(contentType);
            file.save();
            file.validate();
            return file.getId().toString();
        } catch (Exception e) {
            logger.error("Can not store InputStream for file");
            throw new ServiceException(ErrorType.DATABASE_ERROR, "Can not retrieve InputStream for file", e);
        }
    }


    @Override
    public void deleteFile(String id) throws ServiceException {
        try {
            gridFS.remove(new ObjectId(id));
        } catch (Exception e) {
            logger.error("An error has occurred while deleting file from FS: " + id, e);
            throw new ServiceException(ErrorType.DATABASE_ERROR, "An error has occurred while deleting file from FS: " + id, e);
        }
    }

    @Override
    public void deleteFileByName(String name) {
        gridFS.remove(name);
    }

    public GridFS getGridFS() {
        return gridFS;
    }
}

