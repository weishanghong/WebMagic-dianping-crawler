package com.weiresearch.crawler.processor.dianping_com;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * @author WeekiXu
 */
public class DianpingPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(10000);

    @Autowired
    JedisPool jedisPool;

    @Override
    public void process(Page page) {
        List<String> pages = page.getHtml().xpath("[@id='shop-all-list']/ul/li//div[@class='tit']/a").links().regex("http://www.dianping.com/shop/\\d+").all();
        page.addTargetRequests(pages);
        // 详情页
        String pageUrl = "http://www.dianping.com/shop/\\d+";
        if (page.getUrl().regex(pageUrl).match()) {
            String name = page.getHtml().xpath("//h1[@class='shop-name']/text()").toString();
            String star = page.getHtml().xpath("//div[@class='brief-info']/span[1]/@title").toString();
            // 匹配的模式
            Pattern pattern = compile("<script> window.shop_config=(.*?)</script>");
            Matcher m = pattern.matcher(page.getHtml().getDocument().body().html());
            JSONObject lnglatObj = null;
            while (m.find()) {
                lnglatObj = JSON.parseObject(m.group(1));
                page.putField("lng", lnglatObj.get("shopGlng"));
                page.putField("lat", lnglatObj.get("shopGlat"));
            }
            page.putField("name", name);
            page.putField("star", star);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        Spider是爬虫的入口类,addurl为入口url
        Spider spider =
                Spider.create(new DianpingPageProcessor()).addUrl("http://www.dianping.com/beijing/ch10/g110");
//        spider.setScheduler(new RedisScheduler());
        spider.addPipeline(new JsonFilePipeline("D:\\tmp\\ab.txt")).thread(1).run();
    }
}
