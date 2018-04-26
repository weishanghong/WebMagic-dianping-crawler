package com.weiresearch.controller;

import com.weiresearch.exception.ServiceException;
import com.weiresearch.service.DianpingSpiderService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * @author Mr.Wei
 */
@RestController
@RequestMapping(path = "/dianping")
public class DianpingController {


	@Autowired
	JedisPool jedisPool;
	@Autowired
	DianpingSpiderService dianpingSpiderService;

	private Logger log = Logger.getLogger(DianpingController.class.getName());

	@GetMapping("/fetch")
	public void fetchDianping(@RequestParam(required = true) Integer cityId) {

		log.log(Level.SEVERE, "dianping spider starting!");

		if (cityId == null) {
			throw new ServiceException(ServiceException.INVALID_PARAMETER);
		}
		dianpingSpiderService.doSpider(cityId);
	}

	// 框架示例
	@GetMapping("/webmagic")
	public void fetchWebmagic() {
		Spider.create(new GithubRepoPageProcessor()).setScheduler(new RedisScheduler(jedisPool))
				.addPipeline(new JsonFilePipeline("D:\\tmp\\tmp")).addUrl("https://github.com/code4craft").thread(1)
				.run();
	}
}
