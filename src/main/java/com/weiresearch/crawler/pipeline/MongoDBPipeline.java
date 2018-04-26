package com.weiresearch.crawler.pipeline;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 
 * @author Mr.Wei
 * 
 *
 */

@Component
public class MongoDBPipeline implements Pipeline {
	private Logger log = Logger.getLogger(MongoDBPipeline.class.getName());

	@Autowired
	private MongoDatabase database;

	@Override
	public void process(ResultItems resultItems, Task task) {
		try {
			Map<String, Object> map = resultItems.getAll();
			Iterator<String> iterator = map.keySet().iterator();
			String key = iterator.next();
			String name = key.split("#")[0];
			String unique_identifier = key.split("#")[1];
			String type = key.split("#")[2];
			MongoCollection<Document> collection = database.getCollection(name);
			List<JSONObject> list = JSON.parseArray(resultItems.get(key).toString(), JSONObject.class);
			for (JSONObject obj : list) {
				Bson filter = null;
				if (type.equals("int")) {
					filter = Filters.eq(unique_identifier, obj.getIntValue(unique_identifier));
				}
				if (type.equals("string")) {
					filter = Filters.eq(unique_identifier, obj.getString(unique_identifier));
				}
				Document documentOld = collection.find(filter).first();
				if (documentOld != null) {
					Set<String> keySet = documentOld.keySet();
					keySet.forEach(keys -> {
						obj.put(keys, documentOld.get(keys));
					});
					Document document = new Document(obj);
					collection.findOneAndReplace(filter, document);
				} else {
					collection.insertOne(new Document(obj));
				}
			}
		} catch (Exception ex) {
			log.log(Level.SEVERE, "MongoDBPipeline error!", ex);
		}
	}
}
