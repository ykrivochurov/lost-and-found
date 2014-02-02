package ru.poteriashki.laf.core.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.poteriashki.laf.core.model.Item;
import ru.poteriashki.laf.core.model.ItemType;
import ru.poteriashki.laf.core.repositories.ICounterDao;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CounterDao implements ICounterDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Float> countTags(ItemType itemType) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(60));

        Query query = new Query(Criteria.where("itemType").is(itemType.toString()).and("creationDate").gte(calendar.getTime()));
        MapReduceResults<ResultPair> results = mongoTemplate.mapReduce(query, Item.COLLECTION,
                "classpath:map.js", "classpath:reduce.js", ResultPair.class);
        Map<String, Float> map = new HashMap<>();
        for (ResultPair resultPair : results) {
            if (resultPair.getId() != null) {
                map.put(resultPair.getId(), resultPair.getValue());
            }
        }
        return map;
    }

    public static final class ResultPair {
        private String id;
        private Float value;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }
    }

}
