package util;

/**
 * Created by zhangtao on 2017/7/18.
 */
public class ColorUtil {


    /**
     * 将ARGB颜色转为RGB颜色
     * @param ARGB
     * @return
     */
    public static int[] toRGB(int ARGB){
        int[] rgb = new int [3];
        rgb[0] = (ARGB & 0xff0000) >> 16;
        rgb[1] = (ARGB & 0xff00) >> 8;
        rgb[2] = (ARGB & 0xff);
        return  rgb;
    }

    /**
     * 将RGB颜色转为十六进制
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static String toHex(int r, int g, int b) {
        String hexStr = new StringBuilder("#")
                .append(toBrowserHexValue(r))
                .append(toBrowserHexValue(g))
                .append(toBrowserHexValue(b))
                .toString().toUpperCase();
        return hexStr;
    }

    /**
     * R、G、B 单个颜色转十六进制
     * @param number
     * @return
     */
    private static StringBuilder toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(
                Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder;
    }
}
