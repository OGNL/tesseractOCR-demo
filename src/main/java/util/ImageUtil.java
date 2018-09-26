package util;

import entity.RectanglePixel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by zhangtao on 2017/8/10.
 */
public class ImageUtil {


    private static Logger log = Logger.getLogger(ImageUtil.class);
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //注意程序运行的时候需要在VM option添加该行 指明opencv的dll文件所在路径
        //-Djava.library.path=$PROJECT_DIR$\opencv\x64
    }

    /**
     * 查找图片在当前界面所有的出现位置
     * @param imageUrl 图片路径
     * @param pixelArray 当前界面ARGB数组
     * @return
     */
    public static List<RectanglePixel> getScreenshotLocation(String imageUrl,int [][][] pixelArray){
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imageUrl));
            //用数组存储图片的所有ARGB值
            int [] screenshotArray = new int [bufferedImage.getWidth()*bufferedImage.getHeight()];
            int screenshotArrayNum = 0;
            for(int i=0; i<bufferedImage.getWidth(); i++){
                for(int j=0; j<bufferedImage.getHeight(); j++){
                    screenshotArray[screenshotArrayNum++] = bufferedImage.getRGB(i,j);
                }
            }
            List<RectanglePixel> rectanglePixelList = new ArrayList<>();
            //查找当前界面中所有与图片左上角的像素点颜色相同的坐标点
            for(int i=0; i<pixelArray.length; i++){
                for(int j=0; j<pixelArray[i].length; j++){
                     if(pixelArray[i][j][0] == screenshotArray[0]){
                         int minX = i;
                         int minY = j;
                         int maxX = minX+bufferedImage.getWidth();
                         int maxY = minY+bufferedImage.getHeight();
                         if( maxX >= pixelArray.length || maxY >= pixelArray[i].length){
                                break;
                         }
                         //以左上角的相同点为起始点截取当前界面的一部分，大小与图片相同
                         RectanglePixel rectanglePixel = isScreenshotExist(pixelArray,screenshotArray,minX,minY,maxX,maxY);
                         if(rectanglePixel != null){
                             rectanglePixelList.add(rectanglePixel);
                         }
                     }
                }
            }
            return rectanglePixelList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断图片在当前界面是否存在
     * @param pixelArray 当前界面ARGB数组
     * @param screenshotArray 图片ARGB数组
     * @param minX 图片左侧在当前界面的坐标值
     * @param minY 图片上侧在当前界面的坐标值
     * @param maxX 图片右侧在当前界面的坐标值
     * @param maxY 图片下侧在当前界面的坐标值
     * @return
     */
    private static RectanglePixel isScreenshotExist(int [][][] pixelArray, int [] screenshotArray, int minX, int minY, int maxX, int maxY ){
        int screenshotArrayNum = 0;
        boolean isScreenshotExist = true;
        for(int i=minX; i<maxX && isScreenshotExist; i++){
                for(int j=minY; j<maxY; j++){
                    if(pixelArray[i][j][0] != screenshotArray[screenshotArrayNum++]){
                        isScreenshotExist = false;
                        break;
                    }
                }
        }
        if(isScreenshotExist){
            int middleX = minX + (maxX - minX)/2;
            int middleY = minY + (maxY - minY)/2;
            RectanglePixel rectanglePixel = new RectanglePixel();
            rectanglePixel.setMiddleX(middleX);
            rectanglePixel.setMiddleY(middleY);
            rectanglePixel.setMinX(minX);
            rectanglePixel.setMinY(minY);
            rectanglePixel.setMaxX(maxX);
            rectanglePixel.setMaxY(maxY);
            return rectanglePixel;
        }
        return null;
    }

    /**
     * 去除无用的图片坐标点（针对可能出现的图片重叠）
     * @param imageUrl 图片路径
     * @param rectanglePixelList 图片坐标点集合
     * @return
     */
    public static List<RectanglePixel> dealUselessRectanglePixel(String imageUrl,List<RectanglePixel> rectanglePixelList){
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imageUrl));
            List<RectanglePixel> changedList = new ArrayList<>();
            for(int i=0; i<rectanglePixelList.size(); i++){
               RectanglePixel nowRectanglePixel = rectanglePixelList.get(i);
                int nowX = nowRectanglePixel.getMiddleX();
                int nowY = nowRectanglePixel.getMiddleY();
                boolean flag = true;
                for(int j=0; j<changedList.size(); j++){
                    RectanglePixel prevRectanglePixel = changedList.get(j);
                    int prevX = prevRectanglePixel.getMiddleX();
                    int prevY = prevRectanglePixel.getMiddleY();
                    if(!(nowX - prevX > bufferedImage.getWidth() || Math.abs(nowY - prevY) > bufferedImage.getHeight())){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    changedList.add(nowRectanglePixel);
                }
            }
            return changedList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据左上、右下两个坐标点截取图片
     * @param upperLeftMap 左上map
     * @param lowerRightMap 右下map
     * @param moveX x轴偏移量
     * @param moveY y轴偏移量
     * @return
     */
    public static BufferedImage convertScreenToImage(HashMap<String,Object> upperLeftMap, HashMap<String,Object> lowerRightMap,int moveX, int moveY){
            int upperLeftX = upperLeftMap.get("middleX")!=null ? Integer.parseInt(upperLeftMap.get("middleX").toString()) : 0;
            int upperLeftY = upperLeftMap.get("middleY")!=null ? Integer.parseInt(upperLeftMap.get("middleY").toString()) : 0;
            int lowerRightX = lowerRightMap.get("middleX")!=null ? Integer.parseInt(lowerRightMap.get("middleX").toString()) : 0;
            int lowerRightY = lowerRightMap.get("middleY")!=null ? Integer.parseInt(lowerRightMap.get("middleY").toString()) : 0;
//            log.info("开始截取选中的标签区域，minX:" + (upperLeftX + moveX) +" minY："+ (upperLeftY + moveY) + " maxX："
//                    + (lowerRightX + moveX) + " maxY:" + (lowerRightY + moveY));
            BufferedImage bufferedImage = RobotUtil.robot.createScreenCapture(new Rectangle(upperLeftX + moveX,upperLeftY + moveY,
                    lowerRightX - upperLeftX, lowerRightY - upperLeftY));
            return bufferedImage;
    }

    /**
     * 图片二值化
     */
    public static BufferedImage imageBinary(Mat srcMat) {
            //图像放大
            Imgproc.pyrUp(srcMat,srcMat);
            //图像灰化
            Mat grayMat = new Mat();
            Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGB2GRAY);
            //图像二值化
//            Mat binaryMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1);
//            Imgproc.threshold(grayMat, binaryMat, 100,255,Imgproc.THRESH_BINARY);
            //图像腐蚀
//            Mat destMat = new Mat();
//            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
//            Imgproc.erode(grayMat,destMat,element);
            BufferedImage bufferedImage = Mat2BufferedImage(grayMat);
            return bufferedImage;
    }

    /**
     * Mat类型 转 BufferedImage类型
     * @param mat
     * @return
     */
    private static BufferedImage Mat2BufferedImage(Mat mat){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( mat.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels()*mat.cols()*mat.rows();
        byte [] b = new byte[bufferSize];
        mat.get(0,0,b); // get all the pixels
        BufferedImage bufferedImage = new BufferedImage(mat.cols(),mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return bufferedImage;
    }

    /**
     *  BufferedImage类型 转 Mat类型
     * @param image
     * @return
     */
    public static Mat BufferedImage2Mat(BufferedImage image) {
        byte [] pixels = getMatrixBGR(image);
        // Create a Matrix the same size of image
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        // Fill Matrix with image values
        mat.put(0, 0, pixels);
        return mat;
    }



    /**
     * 图片OCR
     * @param image
     */
    public static String imageOCR(BufferedImage image){
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        try {
            instance.setDatapath(System.getenv("TESSDATA_PREFIX"));//设置OCR的本地路径
            instance.setLanguage("chi_sim"); //加载语言包
            return instance.doOCR(image);
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * @param image
     * @param bandOffset 用于判断通道顺序
     * @return
     */
    private static boolean equalBandOffsetWith3Byte(BufferedImage image,int[] bandOffset){
        if(image.getType()==BufferedImage.TYPE_3BYTE_BGR){
            if(image.getData().getSampleModel() instanceof ComponentSampleModel){
                ComponentSampleModel sampleModel = (ComponentSampleModel)image.getData().getSampleModel();
                if(Arrays.equals(sampleModel.getBandOffsets(), bandOffset)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断图像是否为BGR格式
     * @return
     */
    private static boolean isBGR3Byte(BufferedImage image){
        return equalBandOffsetWith3Byte(image,new int[]{0, 1, 2});
    }

    /**
     * 对图像解码返回BGR格式矩阵数据
     * @param image
     * @return
     */
    private static byte[] getMatrixBGR(BufferedImage image){
        if(null==image)
            throw new NullPointerException();
        byte[] matrixBGR;
        if(isBGR3Byte(image)){
            matrixBGR= (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        }else{
            // ARGB格式图像数据
            int intrgb[]=image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            matrixBGR=new byte[image.getWidth() * image.getHeight()*3];
            // ARGB转BGR格式
            for(int i=0,j=0;i<intrgb.length;++i,j+=3){
                matrixBGR[j]=(byte) (intrgb[i]&0xff);
                matrixBGR[j+1]=(byte) ((intrgb[i]>>8)&0xff);
                matrixBGR[j+2]=(byte) ((intrgb[i]>>16)&0xff);
            }
        }
        return matrixBGR;
    }



}
