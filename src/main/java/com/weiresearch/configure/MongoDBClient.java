package com.weiresearch.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author Mr.Wei
 * 
 *
 */

@Component
@ConfigurationProperties("spring.data.mongodb")
public class MongoDBClient {

	private String uri;

	public MongoDBClient() {
	}

	// 获取mongoDatabase
	@Bean
	public MongoDatabase getClient() {
		String dataBaseName = uri.substring(uri.lastIndexOf("/") + 1);
		MongoDatabase database = new MongoClient(new MongoClientURI(uri)).getDatabase(dataBaseName);
		return database;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
