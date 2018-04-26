package com.weiresearch.service;

import java.util.ArrayList;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * 
 * @author Mr.Wei
 * 
 *
 */
@Component
public class DianpingShopListUrlService {

	@Autowired
	private MongoDatabase database;

	public DianpingShopListUrlService() {
	}

	public List<String> getUrls(Integer cityId) {
		MongoCollection<Document> collection = database.getCollection("city");
		Document document = collection.find(Filters.eq("id", cityId)).first();
		JSONObject cityBasicObj = JSON.parseObject(document.toJson());
		List<JSONObject> subCategoryList = JSON.parseArray(cityBasicObj.getString("sub_category"), JSONObject.class);
		List<JSONObject> businessDistrictList = JSON.parseArray(cityBasicObj.getString("business_district"),
				JSONObject.class);
		List<String> urlList = new ArrayList<>();

		for (JSONObject subCategory : subCategoryList) {
			int categoryId = subCategory.getIntValue("id");
			int parentCategoryId = subCategory.getIntValue("parent_id");
			for (JSONObject businessDistrict : businessDistrictList) {
				StringBuilder url = new StringBuilder();
				url.append("https://mapi.dianping.com/searchshop.json?start=0").append("&categoryid=")
						.append(categoryId).append("&parentCategoryId=").append(parentCategoryId).append("&cityid=")
						.append(cityId).append("&regionid=").append(businessDistrict.getIntValue("id"))
						.append("&limit=20&sortid=0&maptype=0");
				urlList.add(url.toString());
			}
			break;
		}
		return urlList;
	}

	public List<String> getShopUrl(Integer cityId) {
		List<String> urlList = new ArrayList<>();
		MongoCollection<Document> collection = database.getCollection("shop");
		FindIterable<Document> iter = collection.find(Filters.eq("city_id", cityId.toString()));
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(Document t) {
				JSONObject shopObj = JSON.parseObject(t.toJson());
				urlList.add("https://m.dianping.com/shop/" + shopObj.getString("business_id") + "/map");
			}
		});
		return urlList;
	}
}
