package com.tileserver.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tileserver.util.DownloadTiles.writePictureStream;

/**
 * @ProjectionName tileServer
 * @ClassName DownloadTiles
 * @Description 下载瓦片
 * @Author YueLifeng
 * @Date 2019/3/14 0014下午 4:18
 * @Version 1.0
 */
public class DownloadTiles {

    /**
     * @Author YueLifeng
     * @Description //多线程下载瓦片
     * @Date 下午 4:20 2019/3/14 0014
     * @param minZoom    最小级别
     * @param maxZoom    最大级别
     * @param urlString  瓦片地址
     * @param path   瓦片存放路径
     * @param tileCodeList    瓦片编号集合
     * @param threadNum    开启的线程数
     * @return void
     */
    public static void downLoadTile(int minZoom, int maxZoom, String urlString, String path, List<TileCode> tileCodeList, int threadNum) {
        //开启threadNum个线程下载
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int level = minZoom; level <= maxZoom; level++) {
            for (TileCode tileCode : tileCodeList) {
                String url = String.format(urlString, tileCode.getRow(), tileCode.getColumn(), level);
                Runner runner = new Runner(tileCode, url, path, level);
                executorService.execute(runner);
            }
        }
        executorService.shutdown();
        System.out.println("瓦片下载完成！");

    }

    /**
     * @Author YueLifeng
     * @Description //下载单个瓦片
     * @Date 下午 5:14 2019/3/14 0014
     * @param tileCode   单个瓦片编码
     * @param urlString     瓦片地址
     * @param path    存放路径
     * @param level     级别
     * @return void
     */
    public static void writePictureStream(TileCode tileCode, String urlString, String path, int level) {
        int x = tileCode.getRow();
        int y = tileCode.getColumn();
        String tileFullPath = path + "/" + level + "/" + x + "/" + y + ".png";
        File file = new File(tileFullPath);
        //如果瓦片存在于本地，则直接返回，不再下载
        if (file.exists()) return;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5 * 2000);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] bytes = new byte[1024];
            String outputPath = path + "/" + level + "/" + x;
            file = new File(outputPath);
            if(!file.exists()) {
                file.mkdirs();
            }
            OutputStream os = new FileOutputStream(file.getPath() + "/" + y + ".png");
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
            os.close();
            inputStream.close();
        } catch (IOException e) {
            if (e instanceof MalformedURLException) {
                System.out.println("请检查下载网址是否正确或可用！");
            }
            e.printStackTrace();
        }
    }
}

/**
 * @Author YueLifeng
 * @Description //线程对象
 * @Date 下午 4:35 2019/3/14 0014

 * @return
 */
class Runner implements Runnable {
    private TileCode tileCode;
    private String urlString;
    private String path;
    private int level;

    public Runner(TileCode tileCode, String urlString, String path, int level) {
        this.tileCode = tileCode;
        this.urlString = urlString;
        this.path = path;
        this.level = level;
    }

    @Override
    public void run() {
        writePictureStream(tileCode, urlString, path, level);
    }
}