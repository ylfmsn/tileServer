package com.tileserver.util;

import com.sun.org.apache.bcel.internal.classfile.Code;

/**
 * @ProjectionName tileServer
 * @ClassName GoogleTiles
 * @Description Google Map 瓦片经纬度间转换
 * @Author YueLifeng
 * @Date 2019/3/14 0014上午 10:41
 * @Version 1.0
 */
public class GoogleTiles {

    //Web墨卡托经纬度范围
    private static final double maxMLonRange = 180 * 2;
    private static final double maxMLatRange = 85.05113 * 2;

    /**
     * @Author YueLifeng
     * @Description //经纬度转墨卡托
     * @Date 上午 10:57 2019/3/14 0014
     * @param coordinate    经纬度坐标
     * @return com.tileserver.util.Coordinate  墨卡托坐标
     */
    public static Coordinate lonlat2WebMercator(Coordinate coordinate) {
        Coordinate mCoordinate = new Coordinate();
        double lon = coordinate.getLon();
        double lat = coordinate.getLat();
        double x = lon * 20037508.3427892 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.3427892 / 180;
        mCoordinate.setLon(x);
        mCoordinate.setLat(y);
        return mCoordinate;
    }

    /**
     * @Author YueLifeng
     * @Description //墨卡托转经纬度
     * @Date 下午 1:59 2019/3/14 0014
     * @param mCoordinate 墨卡托坐标
     * @return com.tileserver.util.Coordinate 经纬度坐标
     */
    public static Coordinate webMercator2LonLat(Coordinate mCoordinate) {
        Coordinate coordinate = new Coordinate();
        double x = mCoordinate.getLon();
        double y = mCoordinate.getLat();
        double lon = x / 20037508.34 * 180;
        double lat = y / 20037508.34 * 180;
        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        coordinate.setLon(lon);
        coordinate.setLat(lat);
        return coordinate;
    }

    /**
     * @Author YueLifeng
     * @Description //通过经纬度获取对应级别的瓦片
     * @Date 下午 2:08 2019/3/14 0014
     * @param coordinate  经纬度坐标
     * @param zoom   级别
     * @return com.tileserver.util.TileCode   瓦片编号
     */
    public static TileCode coordinate2Tilecode(Coordinate coordinate, final int zoom) {
        TileCode tileCode = new TileCode();
        double lon = coordinate.getLon();
        double lat = coordinate.getLat();
        int xTile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int yTile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xTile < 0) {
            xTile = 0;
        }
        if (xTile >= (1 << zoom)) {
            xTile = ((1 << zoom) - 1);
        }
        if (yTile < 0) {
            yTile = 0;
        }
        if (yTile >= (1 << zoom)) {
            yTile = ((1 << zoom) - 1);
        }
        tileCode.setRow(xTile);
        tileCode.setColumn(yTile);
        return tileCode;
    }










}
