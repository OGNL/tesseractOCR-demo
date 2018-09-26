package util;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * robot工具类
 * Created by zhangtao on 2017/7/18.
 */
public class RobotUtil {

    private static Logger log = Logger.getLogger(RobotUtil.class);
    public static Robot robot;
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取全屏幕像素点颜色（RGB值）
     * @return
     */
    public static int [][][] getScreenPixelRGB(){
            //获取屏幕大小
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension dimension = toolkit.getScreenSize();
            int [][][] pixelArray = new int [dimension.width][dimension.height][3];
            //一个矩形面板
            log.info("开始进行全屏截图");
            Rectangle rec = new Rectangle(0, 0, dimension.width, dimension.height);
            BufferedImage bufferedImage = robot.createScreenCapture(rec);
            for(int i=0; i<bufferedImage.getWidth(); i++){
                for(int j=0; j<bufferedImage.getHeight(); j++){
                    int [] rgb = ColorUtil.toRGB(bufferedImage.getRGB(i,j));
                    pixelArray[i][j][0] = rgb[0];
                    pixelArray[i][j][1] = rgb[1];
                    pixelArray[i][j][2] = rgb[2];
                }
            }
            return pixelArray;
    }

    /**
     * 获取全屏幕像素点颜色（ARGB值）
     * @return
     */
    public static int [][][] getScreenPixelARGB(){
        //获取屏幕大小
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        int [][][] pixelArray = new int [dimension.width][dimension.height][1];
        //一个矩形面板
        log.info("开始进行全屏截图");
        Rectangle rec = new Rectangle(0, 0, dimension.width, dimension.height);
        BufferedImage bufferedImage = robot.createScreenCapture(rec);
        for(int i=0; i<bufferedImage.getWidth(); i++){
            for(int j=0; j<bufferedImage.getHeight(); j++){
                pixelArray[i][j][0] = bufferedImage.getRGB(i,j);
            }
        }
        return pixelArray;
    }

    /**
     * 获取屏幕指定区域像素点颜色（ARGB值）
     * @return
     */
    public static int [][][] getScreenPixelARGB(int minX, int maxX, int minY, int maxY){
        //获取屏幕大小
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        if(maxX > dimension.width){
            maxX = dimension.width;
        }
        if(maxY > dimension.height){
            maxY = dimension.height;
        }
        int [][][] pixelArray = new int [maxX - minX][maxY - minY][1];
        //一个矩形面板
        log.info("开始进行指定区域截图，minX："+minX+" maxX："+maxX+" minY："+minY+" maxY："+maxY);
        Rectangle rec = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        BufferedImage bufferedImage = robot.createScreenCapture(rec);
        for(int i=0; i<bufferedImage.getWidth(); i++){
            for(int j=0; j<bufferedImage.getHeight(); j++){
                pixelArray[i][j][0] = bufferedImage.getRGB(i,j);
            }
        }
        return pixelArray;
    }

    /**
     * 根据HexRGB值去除数组中不必要的像素点
     * @param HexRGB 十六进制颜色，如 #FFFFFF
     * @param pixelArray 全屏像素点数组
     * @return
     */
    public static int [][][] getPixelByHexRGB(String HexRGB,int [][][] pixelArray){
        int red = Integer.parseInt(HexRGB.substring(1,3),16);
        int green = Integer.parseInt(HexRGB.substring(3,5),16);
        int blue = Integer.parseInt(HexRGB.substring(5,7),16);
        int [][][] changedPixelArray = new int [pixelArray.length][pixelArray[0].length][3];
            for(int i=0; i<pixelArray.length; i++){
                for(int j=0; j<pixelArray[i].length; j++){
                    if(!(pixelArray[i][j][0] == red && pixelArray[i][j][1] == green && pixelArray[i][j][2] == blue)){
                        //将不必要的像素点的RGB值都变为-1
                        changedPixelArray[i][j][0] = -1;
                        changedPixelArray[i][j][1] = -1;
                        changedPixelArray[i][j][2] = -1;
                    }else{
                        changedPixelArray[i][j][0] = pixelArray[i][j][0];
                        changedPixelArray[i][j][1] = pixelArray[i][j][1];
                        changedPixelArray[i][j][2] = pixelArray[i][j][2];

                    }
                }
            }
        return changedPixelArray;
    }

    /**
     * 鼠标左击
     * @param x x轴坐标移动像素
     * @param y y轴坐标移动像素
     * @param count 点击次数
     */
    public static void clickLMouse( int x, int y, int count) {
        robot.mouseMove(x, y);
        for(int i=0; i<count; i++) {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.delay(10);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
    }

    /**
     * Tab键
     * @param count 执行次数
     */
    public static void doTab( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_TAB);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_TAB);
        }
    }

    /**
     * Down键
     * @param count 执行次数
     */
    public static void doDown( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_DOWN);
        }
    }

    /**
     * Space键
     * @param count 执行次数
     */
    public static void doSpace( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_SPACE);
        }
    }

    /**
     * Delete键
     * @param count 执行次数
     */
    public static void doDelete(int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_DELETE);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_DELETE);
        }
    }

    /**
     * DeleteAll键
     */
    public static void doDeleteAll(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.delay(10);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);

        robot.keyPress(KeyEvent.VK_DELETE);
        robot.delay(10);
        robot.keyRelease(KeyEvent.VK_DELETE);
    }

    /**
     * Enter键
     * @param count 执行次数
     */
    public static void doEnter( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
    }

    /**
     * Esc键
     * @param count 执行次数
     */
    public static void doEsc( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
        }
    }

    /**
     * Up键
     * @param count 执行次数
     */
    public static void doUp( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_UP);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_UP);
        }
    }

    /**
     * Right键
     * @param count 执行次数
     */
    public static void doRight( int count){
        for(int i=0; i<count; i++){
            robot.keyPress(KeyEvent.VK_RIGHT);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_RIGHT);
        }
    }

    /**
     * 复制
     * @param delayTime 复制前等待毫秒数
     */
    public static void doCopy(int delayTime) {
        try {
            Thread.sleep(delayTime);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_C);
            robot.delay(10);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_C);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //粘贴
    public static void doPaste(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.delay(10);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
    }

    //向系统剪切板加入文本内容
    public static void insertToClipboard(String content){
        //获取系统剪切板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //构建String数据类型
        StringSelection selection = new StringSelection(content);
        //添加文本到系统剪切板
        clipboard.setContents(selection, null);

    }

    //获取系统剪切板的文本内容[如果系统剪切板复制的内容是文本]
    public static String getSystemClipboard(){
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (null != t && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String)t.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
