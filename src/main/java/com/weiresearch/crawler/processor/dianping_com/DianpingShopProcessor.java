package com.weiresearch.crawler.processor.dianping_com;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

/**
 * 
 * @author Mr.Wei
 * 
 *
 */
public class DianpingShopProcessor implements PageProcessor {

	private Site site = Site.me().setSleepTime(5000).setRetryTimes(10).setCharset("utf-8").setTimeOut(30000);
	
	private String type = "shop#business_url#string";

	@Override
	public void process(Page page) {
		try {
			URL url = new URL(page.getUrl().toString());
			Map<String, String> paramMap = parseUrlParams(url.getQuery());
			Json contentJson = page.getJson();
			boolean isEnd = Boolean.parseBoolean(contentJson.jsonPath("$.isEnd").get());
			int nextStartIndex = Integer.parseInt(contentJson.jsonPath("$.nextStartIndex").get());
			if (isEnd == false) {
				StringBuilder nextUrl = new StringBuilder();
				nextUrl.append(url.toString().split("\\?")[0]).append("?");
				paramMap.entrySet().stream().forEach(entry -> nextUrl.append(entry.getKey()).append("=")
						.append("start".equals(entry.getKey()) ? nextStartIndex : entry.getValue()).append("&"));
				page.addTargetRequest(nextUrl.toString());
			}
			JSONArray shopResArr = new JSONArray();
			List<String> shopList = contentJson.jsonPath("$.list").all();
			for (String shopStr : shopList) {
				JSONObject shopObj = JSON.parseObject(shopStr);
				JSONObject shopResObj = new JSONObject();
				shopResObj.put("business_id", shopObj.getIntValue("id"));
				shopResObj.put("business_url", "https://m.dianping.com/shop/" + shopObj.get("id"));
				shopResObj.put("city_id", paramMap.get("cityid"));
				shopResObj.put("region_id", shopObj.getIntValue("regionid"));
				shopResObj.put("region_name", shopObj.getString("regionName"));
				shopResObj.put("category_id", shopObj.getIntValue("categoryId"));
				shopResObj.put("category_name", shopObj.getString("categoryName"));
				shopResObj.put("name", shopObj.getString("name"));
				shopResObj.put("branch_name", shopObj.getString("branchName"));
				shopResObj.put("avg_rating", shopObj.getString("showPower"));
				shopResObj.put("avg_price", shopObj.getString("priceText").replace("￥", "").replace("/人", ""));
				shopResObj.put("review_count", shopObj.getIntValue("reviewCount"));
				shopResObj.put("updatetime", new Date());
				shopResArr.add(shopResObj);
			}
			page.putField(type, shopResArr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	Map<String, String> parseUrlParams(String query) {
		Map<String, String> paramMap = new HashMap<>();
		String[] paramArr = query.split("&");
		for (String paramStr : paramArr) {
			String[] strings = paramStr.split("=");
			paramMap.put(strings[0], strings[1]);
		}
		return paramMap;
	}
}
