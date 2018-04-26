package com.weiresearch.crawler.processor;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

import static java.util.regex.Pattern.compile;

/**
 * @author WeekiXu
 */
public class DoubanTop250Processor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(2000);

    @Autowired
    JedisPool jedisPool;

    @Override
    public void process(Page page) {
        List<String> pages = page.getHtml().xpath("//ol[@class='grid_view']/li/div[@class='item']//div[@class='hd']/a").links().regex("https://movie.douban.com/subject/\\d+/").all();
        page.addTargetRequests(pages);
        // 下一页
        String nextPage = page.getHtml().xpath("//link[@rel='next']/@href").toString();
        if (nextPage != null && !nextPage.trim().isEmpty()) {
            nextPage = "https://movie.douban.com/top250" + nextPage;
            page.addTargetRequest(nextPage);
        }

        // 详情页
        String pageUrl = "https://movie.douban.com/subject/\\d+/";
        if (page.getUrl().regex(pageUrl).match()) {
            fetchInfo(page);
        }
    }

    private void fetchInfo(Page page) {
        String name = page.getHtml().xpath("//span[@property='v:itemreviewed']/text()").toString();
        String year = page.getHtml().xpath("//span[@class='year']/text()").toString();
        String img = page.getHtml().xpath("//div[@id='mainpic']//img/@src").toString();
        page.putField("name", name);
        page.putField("year", year);
        page.putField("img", img);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        Spider是爬虫的入口类,addurl为入口url
        Spider spider =
                Spider.create(new DoubanTop250Processor()).addUrl("https://movie.douban.com/top250");
//        spider.setScheduler(new RedisScheduler());
        spider.addPipeline(new JsonFilePipeline("D:\\tmp\\ab.txt")).thread(1).run();
    }
}
