package comparator;

import entity.RectanglePixel;

import java.util.Comparator;

/**
 * 集合比较器 用于RectanglePixel
 * Created by zhangtao on 2017/8/22.
 */
public class RectanglePixelComparator implements Comparator {

    /**
     * 先根据y轴升序，在y轴相同的情况下再根据x轴升序
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Object o1, Object o2) {
        RectanglePixel rectanglePixel1 = (RectanglePixel)o1;
        int x1 = rectanglePixel1.getMiddleX();
        int y1= rectanglePixel1.getMiddleY();
        RectanglePixel rectanglePixel2 = (RectanglePixel)o2;
        int x2 = rectanglePixel2.getMiddleX();
        int y2= rectanglePixel2.getMiddleY();
        if(y1 > y2){
            return 1;
        }else if(y1 == y2){
            if(x1 > x2){
                return 1;
            }else if( x1 == x2){
                return 0;
            }else{
                return -1;
            }
        }else{
            return -1;
        }

    }
}
