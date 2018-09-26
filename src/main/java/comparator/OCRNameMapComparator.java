package comparator;

import java.util.Comparator;
import java.util.HashMap;

/**
 * 集合比较器 用于OCRNameMap
 * Created by zhangtao on 2017/10/13.
 */
public class OCRNameMapComparator implements Comparator{

    /**
     * 根据sameCharNum降序排列
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Object o1, Object o2) {
        HashMap<String,Object> map1 = (HashMap<String,Object>)o1;
        int sameCharNum1 = map1.get("sameCharNum")!=null ? Integer.parseInt(map1.get("sameCharNum").toString()) : 0;
        HashMap<String,Object> map2 = (HashMap<String,Object>)o2;
        int sameCharNum2 = map2.get("sameCharNum")!=null ? Integer.parseInt(map2.get("sameCharNum").toString()) : 0;
        return sameCharNum1 > sameCharNum2 ? -1 :sameCharNum1 < sameCharNum2 ? 1 :0;
    }
}
