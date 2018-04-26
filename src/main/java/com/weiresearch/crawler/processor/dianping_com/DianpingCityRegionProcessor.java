package com.weiresearch.crawler.processor.dianping_com;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author Mr.Wei
 * 
 */

public class DianpingCityRegionProcessor implements PageProcessor {

	private Logger log = Logger.getLogger(DianpingCityRegionProcessor.class.getName());

	private Site site = Site.me().setSleepTime(5000).setRetryTimes(30).setCharset("utf-8").setTimeOut(30000);
	private Integer cityId;
	private String type = "city#id#int";// 指定type,格式为"collection#唯一标识#标识的字段类型"，用于pipeline中解析对应collection更新入库

	@SuppressWarnings("unchecked")
	private Map<Integer, String> cityMap = new HashedMap() {
		private static final long serialVersionUID = 1L;
		{
			put(2, "北京");put(8, "成都");put(9, "重庆");put(4, "广州");put(3, "杭州");put(5, "南京");put(1, "上海");
			put(7, "深圳");put(6, "苏州");put(10, "天津");put(16, "武汉");put(17, "西安");put(22, "济南");put(25, "唐山");
			put(21, "青岛");put(19, "大连");put(11, "宁波");put(15, "厦门");put(18, "沈阳");put(70, "长春");
			put(14, "福州");put(160, "郑州");put(208, "佛山");put(13, "无锡");put(145, "淄博");put(344, "长沙");
			put(148, "烟台");put(35, "太原");put(110, "合肥");put(134, "南昌");put(101, "温州");
		}
	};

	public DianpingCityRegionProcessor(Integer cityId) {
		this.cityId = cityId;
	}

	@Override
	public void process(Page page) {
		JSONObject pageObj = JSON.parseObject(page.getRawText());
		JSONObject districtObj = analysisNavs(pageObj.getString("regionNavs"));
		JSONObject categoryObj = analysisNavs(pageObj.getString("categoryNavs"));
		JSONObject resObj = new JSONObject();
		JSONArray resArray = new JSONArray();
		resObj.put("id", cityId);
		resObj.put("name", cityMap.get(cityId));
		resObj.put("district", districtObj.get("first_level"));
		resObj.put("business_district", districtObj.get("second_level"));
		resObj.put("category", categoryObj.get("first_level"));
		resObj.put("sub_category", categoryObj.get("second_level"));
		resArray.add(resObj);
		page.putField(type, resArray);
	}

	@Override
	public Site getSite() {
		return site;
	}

	JSONObject analysisNavs(String arrInfo) {
		JSONObject resObj = new JSONObject();
		JSONArray firstLevelArr = new JSONArray();
		JSONArray secondLevelArr = new JSONArray();
		List<JSONObject> regionList = JSON.parseArray(arrInfo, JSONObject.class);
		regionList.stream()
				.filter(region -> region.getIntValue("id") == -10000 || region.getIntValue("parentId") == -10000
						|| region.getIntValue("id") == region.getIntValue("parentId"))
				.collect(Collectors.toList());
		for (JSONObject regionObj : regionList) {
			try {
				int id = regionObj.getIntValue("id");
				int parentId = regionObj.getIntValue("parentId");
				String name = regionObj.getString("name");
				JSONObject newObj = new JSONObject();
				newObj.put("id", id);
				newObj.put("name", name);
				if (parentId == 0) {
					firstLevelArr.add(newObj);
					continue;
				}
				newObj.put("parent_id", parentId);
				secondLevelArr.add(newObj);
			} catch (Exception ex) {
				log.log(Level.SEVERE, "analyseNavs error!", ex);
			}
		}
		resObj.put("first_level", firstLevelArr);
		resObj.put("second_level", secondLevelArr);
		return resObj;
	}
}
