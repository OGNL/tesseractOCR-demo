package entity;

/**
 * 像素矩形
 * Created by zhangtao on 2017/10/30.
 */
public class RectanglePixel {


    private int minX;//矩形最小x坐标
    private int minY;//矩形最小y坐标
    private int middleX;//矩形中心x坐标
    private int middleY;//矩形中心y坐标
    private int maxX;//矩形最大x坐标
    private int maxY;//矩形最大y坐标

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMiddleX() {
        return middleX;
    }

    public void setMiddleX(int middleX) {
        this.middleX = middleX;
    }

    public int getMiddleY() {
        return middleY;
    }

    public void setMiddleY(int middleY) {
        this.middleY = middleY;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }
}
