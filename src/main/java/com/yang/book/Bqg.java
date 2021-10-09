package com.yang.book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileOutputStream;
import java.net.URL;


/**
 * desc
 *
 * @author woshilll
 * @version 1.0.0
 * @date 2021/10/8 12:25
 */
public class Bqg {
    public static void main(String[] args) throws Exception {
        download("https://www.bbiquge.net", "/book_36872/", "斗罗大陆五");
    }

    /**
     * 适用于大多数笔趣阁
     *
     * @param host 域名
     * @param api 请求地址
     * @param fileName 下载文件名
     * @throws Exception 异常
     */
    private static void download(String host, String api, String fileName) throws Exception{
        // 获取目录
        Document parse = Jsoup.parse(new URL(host + api), 10000);
        Element body = parse.body();
        // 针对id是list的小说网站
        Element div = body.getElementById("list");
        Element dl = div.getElementsByTag("dl").get(0);
        // 找到所有的目录标签
        Elements elements = dl.getAllElements();
        int index = skipNewLessons(elements);
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (!"dd".equalsIgnoreCase(element.tagName())) {
                continue;
            }
            Elements as = element.getElementsByTag("a");
            if (as.size() == 0) {
                continue;
            }
            Element a = as.get(0);
            String href = a.attr("href");
            String title = a.text();
            try {
                System.out.println(title);
                String content = parseContent(host + (href.startsWith("/") ? href : api + href));
                sb.append(title).append("\n").append(content).append("\n");
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(title + " --- 下载失败");
            }
        }
        FileOutputStream outputStream = new FileOutputStream("./" + fileName + ".txt");
        outputStream.write(sb.toString().getBytes());
        outputStream.close();
    }

    private static int skipNewLessons(Elements elements) {
        boolean flag = false;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (flag) {
                if ("dt".equalsIgnoreCase(element.tagName())) {
                    return i + 1;
                }
            } else {
                if ("dt".equalsIgnoreCase(element.tagName())) {
                    flag = true;
                }
            }
        }
        return 0;
    }

    private static String parseContent(String url) throws Exception {
        Element content = Jsoup.parse(new URL(url), 10000).body().getElementById("content");
        return content.html().replaceAll(" ", "").replaceAll("&nbsp;", " ").replaceAll("<br>", "").replaceAll("\n\n", "\n");
    }
}
