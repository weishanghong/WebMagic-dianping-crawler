package com.weiresearch.crawler.processor.dianping_com;

import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

/**
 * 
 * @author Mr.Wei
 * 
 *
 */

public class DianpingShopLngLatProcessor implements PageProcessor {

	private Site site = Site.me().setSleepTime(5000).setRetryTimes(10).setCharset("utf-8").setTimeOut(30000);

	private String type = "shop#business_id#int";

	@Override
	public void process(Page page) {
		Html html = page.getHtml();
		Pattern pattern = compile("<script>window.PAGE_INITIAL_STATE=(.*?)</script>");
		String value = html.getDocument().head().html().replaceAll("\\s*|\t|\r|\n", "");
		Matcher m = pattern.matcher(value);
		JSONArray lnglatArray = new JSONArray();
		if (m.find()) {
			if (null != m.group(1) && !m.group(1).isEmpty()) {
				JSONObject lnglatObj = JSON.parseObject(m.group(1).substring(0, m.group(1).length() - 1));
				JSONObject shopObj = lnglatObj.getJSONObject("_context").getJSONObject("pageInitData");
				JSONObject shopResObj = new JSONObject();
				shopResObj.put("business_id", shopObj.getIntValue("shopId"));
				String lat = shopObj.getString("shopLat");
				String lng = shopObj.getString("shopLng");
				if (lng != null && !lng.isEmpty()) {
					shopResObj.put("lng", lng.substring(0, lng.indexOf(".") + 7));
				} else {
					shopResObj.put("lng", "");
				}
				if (lat != null && !lat.isEmpty()) {
					shopResObj.put("lat", lat.substring(0, lat.indexOf(".") + 7));
				} else {
					shopResObj.put("lat", "");
				}
				shopResObj.put("address", shopObj.getString("address"));
				lnglatArray.add(shopResObj);
				page.putField(type, lnglatArray);
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

}
