package com.tileserver.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectionName tileServer
 * @ClassName TDTTiles
 * @Description 天地图瓦片编码经纬度相互转换
 * @Author YueLifeng
 * @Date 2019/3/14 0014下午 3:24
 * @Version 1.0
 */
public class TDTTiles {
    //瓦片大小
    public final static int tileSize = 256;

    /**
     * @Author YueLifeng
     * @Description //经纬度转瓦片编码
     * @Date 下午 3:29 2019/3/14 0014
     * @param coordinate   经纬度
     * @param level   级别
     * @return com.tileserver.util.TileCode   瓦片编码
     */
    public static TileCode coordinate2Tilecode(Coordinate coordinate, int level) {
        TileCode tileCode = new TileCode();
        double resolution = 360 / (Math.pow(2, level) * tileSize);
        int row = (int) Math.floor(Math.abs(-180 - coordinate.getLon()) / (resolution * tileSize));
        int column = (int) Math.floor(Math.abs(90 - coordinate.getLat()) / (resolution * tileSize));
        tileCode.setRow(row);
        tileCode.setColumn(column);
        return tileCode;
    }

    /**
     * @Author YueLifeng
     * @Description //瓦片编号转坐标
     * @Date 下午 3:37 2019/3/14 0014
     * @param tileCode    瓦片编号
     * @param level    级别
     * @return com.tileserver.util.Coordinate[]    坐标
     */
    public static Coordinate[] tilecode2Coordinate(TileCode tileCode, int level) {
        Coordinate leftTop = new Coordinate();
        Coordinate rightBottom = new Coordinate();
        leftTop.setLon(tilecode2lon(tileCode.getRow(), level));
        leftTop.setLat(tilecode2lat(tileCode.getColumn(), level));
        rightBottom.setLon(tilecode2lon(tileCode.getRow(), level));
        rightBottom.setLat(tilecode2lat(tileCode.getColumn(), level));
        return new Coordinate[]{leftTop, rightBottom};
    }

    /**
     * @Author YueLifeng
     * @Description //列号转纬度
     * @Date 下午 3:42 2019/3/14 0014
     * @param column   瓦片列号
     * @param level   级别
     * @return double   纬度
     */
    private static double tilecode2lat(int column, int level) {
        return 90 - column / Math.pow(2, level - 1) * 180;
    }

    /**
     * @Author YueLifeng
     * @Description //行号转经度
     * @Date 下午 3:39 2019/3/14 0014
     * @param row   瓦片行号
     * @param level    级别
     * @return double    经度
     */
    private static double tilecode2lon(int row, int level) {
        return row / Math.pow(2, level) * 360 - 180;
    }

    /**
     * @Author YueLifeng
     * @Description //坐标集合生成瓦片编号集合
     * @Date 下午 3:45 2019/3/14 0014
     * @param coordinates
     * @param level
     * @return java.util.List<com.tileserver.util.TileCode>
     */
    public static List<TileCode> mapTilecodes(Coordinate[] coordinates, int level) {
        double tileSize = 360 / Math.pow(2, level);
        List<TileCode> tileCodeList = new ArrayList<>();
        Coordinate coordinate = new Coordinate();
        double xOrigin = coordinates[0].getLon();
        double yOrigin = coordinates[0].getLat();
        int xCount = (int) Math.ceil(Math.abs(coordinates[0].getLon() - coordinates[1].getLon()) / tileSize);
        int yCount = (int) Math.ceil(Math.abs(coordinates[0].getLat() - coordinates[1].getLat()) / tileSize);
        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                coordinate.setLon( xOrigin + tileSize * i);
                coordinate.setLat( yOrigin + tileSize * i);
                TileCode tileCode = coordinate2Tilecode(coordinate, level);
                tileCodeList.add(tileCode);
            }
        }
        return tileCodeList.size() > 0 ? tileCodeList : null;
    }
}
