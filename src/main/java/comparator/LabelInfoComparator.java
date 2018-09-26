package comparator;

import entity.LabelInfo;

import java.util.Comparator;

/**
 * 集合比较器 用于LabelInfo
 * Created by zhangtao on 2017/11/7.
 */
public class LabelInfoComparator implements Comparator {

    /**
     * 先根据y轴升序，在y轴相同的情况下再根据x轴升序
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Object o1, Object o2) {
        LabelInfo labelInfo1 = (LabelInfo)o1;
        int x1 = labelInfo1.getMiddleX();
        int y1= labelInfo1.getMiddleY();
        LabelInfo labelInfo2 = (LabelInfo)o2;
        int x2 = labelInfo2.getMiddleX();
        int y2= labelInfo2.getMiddleY();
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
