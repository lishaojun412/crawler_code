package com.huawei.crawler;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.List;

public class GithubDemo implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)").setCharset("UTF-8");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {


        // 部分三：从页面发现后续的url地址来抓取
        if (page.getUrl().get().endsWith(".xml")) {
            if (page.getUrl().get().equals("https://www.airitilibrary.com/sitemap_AL/AL_7.xml")) {
                // parse xml
                List<String> loc = page.getHtml().getDocument().getElementsMatchingOwnText("https://www.airitilibrary.com/sitemap_AL/I2020").eachText();
                page.addTargetRequests(loc);
                System.out.println(loc);
            } else if (page.getUrl().get().startsWith("")) {
                List<String> loc = page.getHtml().getDocument().getElementsByTag("loc").eachText();
                page.addTargetRequests(loc);
                System.out.println(loc);
            } else {
                page.setSkip(true);
            }
        } else {
            // 部分二：定义如何抽取页面信息，并保存下来
            Elements textArea2 = page.getHtml().getDocument().getElementsByClass("TextArea2");
            if (!textArea2.hasText()) {
                page.setSkip(true);
            } else {
                for (Element element : textArea2) {
                    page.putField("lang2", element.ownText());
                }
            }
            Elements textArea1 = page.getHtml().getDocument().getElementsByClass("TextArea1");
            if (!textArea1.hasText()) {
                page.setSkip(true);
            } else {
                for (Element element : textArea1) {
                    page.putField("lang1", element.ownText());
                }
            }
            System.out.println();

//            page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//            page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//            if (page.getResultItems().get("name") == null) {
//                //skip this page
//                page.setSkip(true);
//            }
//            page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
                new Proxy("127.0.0.1", 7890)));
        Thread thread = new Thread(() -> Spider.create(new GithubDemo())
                //从"https://github.com/code4craft"开始抓
                .setDownloader(httpClientDownloader)
                .addUrl("https://www.airitilibrary.com/sitemap_AL/AL_7.xml")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run());

//        Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Spider.create(new GithubDemo())
//                        //从"https://github.com/code4craft"开始抓
//                        .addUrl("https://github.com/code4craft")
//                        //开启5个线程抓取
//                        .thread(5)
//                        //启动爬虫
//                        .run();
//            }
//        });

        thread.start();
//        thread2.start();


    }

}
