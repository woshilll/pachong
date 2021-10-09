package com.yang;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.HexUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.http.HTMLFilter;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Stream;

/**
 * desc
 *
 * @author stmj
 * @version 1.0.0
 * @date 2021/9/17 15:55
 */
public class ParseHtml {
    public static final String SERVER1 = "https://v3.ddrk.me";
    public static final String SERVER2 = "https://v2.ddrk.me";
    public static final String SERVER_TV = "https://v.ddys.tv";
    public static void main(String[] args) throws Exception {
        ziMu();
    }
    public static void download() throws Exception {
        HttpResponse execute = HttpUtil.createGet("https://ddrk.me/free-guy/").execute();
//        HttpResponse execute = HttpUtil.createGet("https://ddrk.me/the-suicide-squad-2021/").execute();
        String body = execute.body();
//        String date = execute.header("date");
//        Date resDate = new Date(date);
        Document document = Jsoup.parse(body);
        Element element = document.body();
        Element aClass = element.getElementsByClass("wp-playlist wp-video-playlist wp-playlist-light wpse-playlist").get(0);
        Element script = aClass.getElementsByTag("script").get(0);
        String json = script.html();
        JSONObject jsonObject = JSONUtil.parseObj(json);
        Object tracks = jsonObject.get("tracks");
        JSONArray jsonArray = JSONUtil.parseArray(tracks);
//        List<Integer> skip = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> skip = Arrays.asList(-1, -3);
        int i = 0;
        for (Object o : jsonArray) {
            i++;
            if (skip.contains(i)) {
                continue;
            }
            Object videoId = JSONUtil.parseObj(o).get("src0");
            System.out.println(videoId);
            String encryptStr = Encrypt.toStr((String) videoId, new Date());
            String downloadUrl =  tryServer(encryptStr);
            int lastIndexOf = ((String) videoId).lastIndexOf('_');
            String fileName = ((String) videoId).substring(lastIndexOf + 1);
            if (StringUtil.isBlank(downloadUrl)) {
                System.err.println(fileName + " --- 下载失败");
                return;
            }
            download(downloadUrl, fileName);
            Thread.sleep(60000);
        }
    }

    private static String tryServer(String encryptStr) throws InterruptedException {

        String request = ":9543/video?id=" + encryptStr + "&type=mix";
        System.out.println("尝试服务1 " + SERVER1 + request);
        String resJson = HttpUtil.createGet(SERVER1 + request)
                .execute().body();
        System.out.println("接收到服务器1的消息: " + resJson);
        Object downloadUrl = JSONUtil.parseObj(resJson).get("url");
        if (StringUtil.isBlank((String) downloadUrl)) {
            // server2
            Thread.sleep(1000);
            System.out.println("尝试服务2 " + SERVER2 + request);
            resJson = HttpUtil.createGet(SERVER2 + request)
                    .execute().body();
            System.out.println("接收到服务器2的消息: " + resJson);
            downloadUrl = JSONUtil.parseObj(resJson).get("url");
            if (StringUtil.isBlank((String) downloadUrl)) {
                // server2
                Thread.sleep(1000);
                System.out.println("尝试服务3 " + SERVER_TV + request);
                resJson = HttpUtil.createGet(SERVER_TV + request)
                        .execute().body();
                System.out.println("接收到服务器3的消息: " + resJson);
                downloadUrl = JSONUtil.parseObj(resJson).get("url");
            }
        }
        return (String) downloadUrl;
    }

    public static void download(String url, String fileName) {
        HttpUtil.downloadFile(url, new File("./rick/" + fileName), new StreamProgress() {
            @Override
            public void start() {
                System.out.println("开始下载 --- " + fileName);
            }

            @Override
            public void progress(long l) {
                if (l % 1000 == 0) {
                    System.out.println("已下载：" + FileUtil.readableFileSize(l));
                }
            }

            @Override
            public void finish() {
                System.out.println("下载完成");
            }
        });
    }
    private static void ziMu() throws Exception{
        FileInputStream inputStream = new FileInputStream(new File("F:\\liyang\\workspace\\pachong\\src\\main\\java\\com\\yang\\Free.Guy.2021.ddr"));
        byte[] bytes = new byte[1024];
        int len = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((len = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
        }
        bytes = outputStream.toByteArray();
        byte[] head = Arrays.copyOfRange(bytes, 0, 16);
        StringBuilder sb = new StringBuilder();
        for (byte b : head) {
            String str = "00" + String.valueOf(b);
            sb.append(str.substring(str.length() - 2));
        }
        byte[] hex = HexUtil.decodeHex(sb.toString());
        byte[] decrypt = Encrypt.decrypt(hex, hex, Arrays.copyOfRange(bytes, 16, bytes.length));
        String encodeToString = Base64.getEncoder().encodeToString(decrypt);
        int length = encodeToString.length();
        byte[] byte2 = new byte[length];
        for (int i = 0; i < length; i++) {
            byte2[i] = (byte) encodeToString.charAt(i);
        }
        FileOutputStream stream = new FileOutputStream("./a.png");
        stream.write(decrypt);
        stream.close();
//        let eAB = this.response;
//
//        let wordArray = CryptoJS.lib.WordArray.create(eAB.slice(16));
//        let hexStr = Array.prototype.map.call(new Uint8Array(eAB.slice(0, 16)), x => ('00' + x.toString(16)).slice(-2)).join('');
//        let wordArray2 = CryptoJS.enc.Hex.parse(hexStr);
//
//        let jsdec = CryptoJS.AES.decrypt({ciphertext:wordArray},wordArray2,{
//                iv: wordArray2,
//                mode: CryptoJS.mode.CBC
//						    });
//
//        let binary_string = window.atob(jsdec.toString(CryptoJS.enc.Base64));
//        let len = binary_string.length;
//        let bytes = new Uint8Array(len);
//        for (let i = 0; i < len; i++) {
//            bytes[i] = binary_string.charCodeAt(i);
//        }
//
//        let blob = new Blob([pako.ungzip(bytes.buffer,{to:'string'})], {type : 'image/png'});
//        let img = document.createElement("img");
//        img.src = window.URL.createObjectURL(blob);
//        let subTrack = {
//                kind: 'subtitles',
//                src: img.src,
//                srclang: 'zh-cn',
//                label: '中文',
//                mode: 'showing',
//        default: true
//							};
//        myPlayer.addRemoteTextTrack(subTrack,true);
    }
}
