package com.weiresearch.controller;

import com.weiresearch.crawler.processor.DoubanTop250Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * @author WeekiXu
 */
@RestController
@RequestMapping("douban")
public class DoubanController {

    @Autowired
    JedisPool jedisPool;

    @GetMapping("/fetchTop250")
    public void fetchTop250(@RequestParam(required = false) String reset) {
        RedisScheduler scheduler = new RedisScheduler(jedisPool);
//        Spider是爬虫的入口类,addurl为入口url
        Spider spider = Spider.create(new DoubanTop250Processor())
                .setScheduler(new RedisScheduler(jedisPool))
                .addPipeline(new JsonFilePipeline("D:\\tmp\\tmp"))
                .addUrl("https://movie.douban.com/top250");
        if (reset != null && "true".equals(reset)) {
            scheduler.resetDuplicateCheck(spider);
        }
        spider.thread(1).run();
    }

}
