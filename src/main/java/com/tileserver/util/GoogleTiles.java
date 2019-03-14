package com.tileserver.util;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * @Author YueLifeng
     * @Description //通过瓦片编号和级别获取瓦片的最小外包矩形（即瓦片范围）
     * @Date 下午 2:25 2019/3/14 0014
     * @param tileCode    行列号
     * @param zoom   级别
     * @return com.tileserver.util.Coordinate[]    最小外包矩形坐标
     */
    public static Coordinate[] tilecode2Coordinate(TileCode tileCode, final int zoom) {
        int x = tileCode.getRow();
        int y = tileCode.getColumn();
        Coordinate c1 = new Coordinate();
        c1.setLon(tile2lon(x, zoom));
        c1.setLat(tile2lat(y, zoom));
        Coordinate c2 = new Coordinate();
        c2.setLon(tile2lon(x + 1, zoom));
        c2.setLat(tile2lat(y + 1, zoom));
        return new Coordinate[]{c1, c2};
    }

    /**
     * @Author YueLifeng
     * @Description //通过瓦片编号和级别获取纬度
     * @Date 下午 2:23 2019/3/14 0014
     * @param y   纬度
     * @param zoom    级别
     * @return double   纬度
     */
    private static double tile2lat(int y, int zoom) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, zoom);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    /**
     * @Author YueLifeng
     * @Description //通过瓦片编号和级别获取经度
     * @Date 下午 2:21 2019/3/14 0014
     * @param x    编号x
     * @param zoom    级别
     * @return double    经度
     */
    private static double tile2lon(int x, int zoom) {
        return x / Math.pow(2.0, zoom) * 360.0 - 180;
    }

    /**
     * @Author YueLifeng
     * @Description //通过范围获取对应级别的所有瓦片编号
     * @Date 下午 2:28 2019/3/14 0014
     * @param coordinates   坐标
     * @param level    级别
     * @return java.util.List<com.tileserver.util.TileCode>   编号集合
     */
    public static List<TileCode> calculateCodes(Coordinate[] coordinates, int level) {
        List<TileCode> tileCodeList = new ArrayList<>();
        double xTileSize;
        double yTileSize;
        xTileSize = maxMLonRange / Math.pow(2, level);
        yTileSize = maxMLatRange / Math.pow(2, level);
        double xOrigin = coordinates[0].getLon();
        double yOrigin = coordinates[0].getLat();
        int xCount = (int) Math.ceil(Math.abs(coordinates[0].getLon() - coordinates[1].getLon()) / xTileSize);
        int yCount = (int) Math.ceil(Math.abs(coordinates[0].getLat() - coordinates[1].getLat()) / yTileSize);
        for (int i = 1; i <= xCount; i++) {
            for (int j = 1; j <= yCount; j++) {
                Coordinate coordinate = new Coordinate();
                coordinate.setLon(xOrigin + xTileSize * (i - 1));
                coordinate.setLat(yOrigin + yTileSize * (j - 1));
                tileCodeList.add(coordinate2Tilecode(coordinate, level));
            }
        }
        return tileCodeList.size() > 0 ? tileCodeList : null;
    }
}
