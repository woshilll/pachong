package com.yang.video.cha_li;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yang.utils.Decrypt;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * desc
 *
 * @author woshilll
 * @version 1.0.0
 * @date 2021/9/24 10:17
 */
public class ChaLi {
    private static final Pattern PATTERN = Pattern.compile("\\[\\{.*?}]");
    public static void main(String[] args) {
        parse();
    }
    public static void parse() {
        // 主机地址
        String host = "https://www.abcmeiju.com/";
        // 分类地址
        String muLu = "voddetail/7523/";
        String html = HttpUtil.createGet(host + muLu).execute().body();
        Document document = Jsoup.parse(html);
        Element ul = document.body().getElementsByClass("play_list fade_in sidebar_list").get(0);
        Elements plays = ul.getElementsByTag("li");
        List<Integer> skip = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        int i = 0;
        for (Element play : plays) {
            i++;
            if (skip.contains(i)) {
                continue;
            }
            Element a = play.getElementsByTag("a").get(0);
            String href = a.attr("href");
            String fileName = a.text();
            getPlay(host, href, fileName);
        }
    }

    private static void getPlay(String host, String href, String fileName) {
        String html = HttpUtil.createGet(host + href).execute().body();
        Element element = Jsoup.parse(html).body().getElementById("yoyet_player");
        Element script = element.getElementsByTag("div").get(0).getElementsByTag("script").get(0);
        String str = script.html();
        String json = str.substring(str.indexOf("{"));
        Object url = JSONUtil.parseObj(json).get("url");
        parseUrl((String) url, fileName);
    }

    private static void parseUrl(String url, String fileName) {
        String html = HttpUtil.createGet(url).execute().body();
        Elements scripts = Jsoup.parse(html).body().getElementsByTag("script");
        Element script = scripts.get(scripts.size() - 1);
        String m3u8Source = getM3u8Source(script.html());
        if (m3u8Source == null) {
            return;
        }
        try {
            URI uri = URLUtil.getHost(new URL(url));
            String host = uri.getHost();
            Object o = JSONUtil.parseArray(m3u8Source).get(0);
            m3u8Source = (String) JSONUtil.parseObj(o).get("url");
            parseM3u8(host + m3u8Source, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseM3u8(String host, String fileName) {
        System.out.println(Thread.currentThread().getName() + "下载 -----  " + fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("./3/" + fileName + ".ts");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String body = HttpUtil.createGet(host).execute().body();
        String[] lines = body.split("\n");
        int length = lines.length;
        String key = "";
        int i = 1;
        for (String line : lines) {
            System.out.println(Thread.currentThread().getName() + "共计-" + length + "-行，正在执行第-" + (i++) + "-行");
            if (line.contains("URI") && "".equals(key)) {
                // 说明是key
                String keyUrl = line.substring(line.indexOf("http"), line.lastIndexOf("\""));
                key = HttpUtil.createGet(keyUrl).execute().body();
            } else {
                if (line.contains("http")) {
                    byte[] bytes = HttpUtil.createGet(line).execute().bodyBytes();
                    try {
                        byte[] decrypt = Decrypt.decrypt(key, key, bytes);
                        assert outputStream != null;
                        outputStream.write(decrypt);
                    } catch (Exception e) {
                        System.err.println("下载失败key " + key + " url --- " + line);
                    }
                }
            }
        }
        try {
            outputStream.close();
            // 合并
            Runtime.getRuntime().exec("ffmpeg -i F:\\liyang\\workspace\\pachong\\3\\"+fileName+".ts -c copy -map 0:v -map 0:a -bsf:a aac_adtstoasc F:\\liyang\\workspace\\pachong\\3\\"+fileName+".mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getM3u8Source(String source) {
        Matcher matcher = PATTERN.matcher(source);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}
