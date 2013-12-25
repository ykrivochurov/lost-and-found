package ru.poteriashki.laf.core.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ru.poteriashki.laf.core.repositories.IGridFSDao;
import ru.poteriashki.laf.core.repositories.impl.GridFSDao;

@Configuration
@EnableMongoRepositories(basePackages = {"ru.poteriashki.laf.core.repositories"})
public class PersistenceMongoConfig extends AbstractMongoConfiguration {

    @Value("${db.host}")
    public String host;

    @Value("${db.port}")
    public Integer port;

    @Value("${db.name}")
    public String dbName;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(host, port);
    }

    @Override
    protected String getMappingBasePackage() {
        return "ru.poteriashki.laf.core.model";
    }

    @Bean(initMethod = "init")
    @DependsOn("mongoTemplate")
    public IGridFSDao gridFSRepository() {
        return new GridFSDao();
    }
}