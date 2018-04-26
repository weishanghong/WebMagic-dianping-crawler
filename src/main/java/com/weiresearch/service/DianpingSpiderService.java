package com.weiresearch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.weiresearch.crawler.pipeline.MongoDBPipeline;
import com.weiresearch.crawler.processor.dianping_com.DianpingCityRegionProcessor;
import com.weiresearch.crawler.processor.dianping_com.DianpingShopLngLatProcessor;
import com.weiresearch.crawler.processor.dianping_com.DianpingShopProcessor;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

@Component
public class DianpingSpiderService {
	
	@Autowired
	MongoDBPipeline mongoDBPipeline;
	@Autowired
	DianpingShopListUrlService dianpingShopListUrlService;
	
	@Async
	public void doSpider(Integer cityId) {
		
		// 爬取城市对应行政区及一级、二级业态
		Spider.create(new DianpingCityRegionProcessor(cityId))
			  .addUrl("https://mapi.dianping.com/searchshop.json?cityid=" + cityId)
			  .addPipeline(mongoDBPipeline)
		      .run();

		// 封装所有区域及业态url
		List<String> urlList = dianpingShopListUrlService.getUrls(cityId);

		// 爬取店铺列表
		Spider.create(new DianpingShopProcessor())
		      .addUrl(urlList.toArray(new String[urlList.size()]))
		      .setScheduler(new FileCacheQueueScheduler("/opt/jars/crawler-webmagic/file-cache-queue/shop-queue"))
			  .addPipeline(mongoDBPipeline)
			  .thread(3)
		      .run();

		// 查询现有shop的business_id封装获取经纬度的url
		List<String> shopUrlList = dianpingShopListUrlService.getShopUrl(cityId);
		
		//获取店铺经纬度信息
		Spider.create(new DianpingShopLngLatProcessor())
			  .addUrl(shopUrlList.toArray(new String[shopUrlList.size()]))
			  .setScheduler(new FileCacheQueueScheduler("/opt/jars/crawler-webmagic/file-cache-queue/lnglat-queue"))//默认使用HashSetDuplicateRemover去重
			  .addPipeline(mongoDBPipeline)
			  .thread(3)
			  .run();
	}
}
