package service;

import comparator.LabelInfoComparator;
import comparator.OCRNameMapComparator;
import comparator.RectanglePixelComparator;
import dao.LabelInfoDao;
import entity.LabelInfo;
import entity.RectanglePixel;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import util.ImageUtil;
import util.RobotUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 通过图片获取案卡的标签信息
 * Created by zhangtao on 2017/11/10.
 */
public class ImageService {

    private static final String WRITED_LEFT_TEXTBOX_URL = "img/screenshot/已填左框.png";
    private static final String NOT_WRITED_LEFT_TEXTBOX_URL = "img/screenshot/未填左框.png";
    private static final String FROBID_WRITING_LEFT_TEXTBOX_URL = "img/screenshot/禁止填左框.png";
    private static final String TEXTFIELD_LEFT_TEXTBOX_URL = "img/screenshot/文本域左框.png";
    private static final String WRITED_RIGHT_TEXTBOX_URL = "img/screenshot/已填右框.png";
    private static final String NOT_WRITED_RIGHT_TEXTBOX_URL = "img/screenshot/未填右框.png";
    private static final String FROBID_WRITING_RIGHT_TEXTBOX_URL = "img/screenshot/禁止填右框.png";
    private static final String LEFT_BORDER_URL = "img/screenshot/左边框.png";
    private static final String RIGHT_BORDER_URL = "img/screenshot/右边框.png";

    private static final String CHECKED_LEFT_TAB_URL = "img/screenshot/选中tab左框.png";
    private static final String UNCHECKED_RIGHT_TAB_URL = "img/screenshot/未选中tab左框.png";

    private static final String WIN7_SCROLLBAR_URL = "img/screenshot/win7滚动条.png";
    private static final String WIN10_SCROLLBAR_URL = "img/screenshot/win10滚动条.png";

    private static final String ADD_BUTTON_URL = "img/screenshot/加号.png";
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    //左侧标签与表格区域左边框的间距
    private static final int LEFT_INTERVAL = 12;
    //滚轮向下滚动次数
    private static final int DOWN_SCROLLCOUNT = 4;

    private static LabelInfoDao labelInfoDao = new LabelInfoDao();
    private static Logger log = Logger.getLogger(ImageService.class);

    /**
     * 获取单个案卡内所有表格的标签信息
     * @param moduleType 模块类型
     * @param tabNum 标签所在tab的编号
     * @param isCheckUpTable 是否校验上模块表格的完整性
     * @param isDownTableExist 是否存在下模块
     * @param isCheckDownTable 是否校验下模块表格的完整性
     * @param isCheckTab 是否校验模块是否存在tab
     * @param isFull 表格是否完整
     * @param scrollCount 滚轮滚动次数
     * @param labelInfoList 表格内的标签信息集合
     * @param uncheckedRightTabList 未选中的tab集合
     * @throws InterruptedException
     * @throws SQLException
     */
    public void getModuleLabelInfo(String moduleType, int tabNum, boolean isCheckUpTable,boolean isDownTableExist,boolean isCheckDownTable,boolean isCheckTab, boolean isFull,int scrollCount,List<LabelInfo> labelInfoList,List<RectanglePixel> uncheckedRightTabList)throws InterruptedException,SQLException{
        //通过屏幕截图定位到案卡界面的表格区域
        int [][][] pixelArray = RobotUtil.getScreenPixelARGB();
        List<RectanglePixel> coordinateList = ImageUtil.getScreenshotLocation(LEFT_BORDER_URL,pixelArray);
        int leftX = coordinateList.get(0).getMiddleX();
        coordinateList = ImageUtil.getScreenshotLocation(RIGHT_BORDER_URL,pixelArray);
        int rightX = coordinateList.get(0).getMiddleX();
        //截取案卡表格区域
        int maxY = Toolkit.getDefaultToolkit().getScreenSize().height;
        pixelArray = RobotUtil.getScreenPixelARGB(leftX,rightX,0,maxY);

        int addY = maxY;
        if(moduleType.equals(UP) && isCheckUpTable){
            //校验是否存在下模块，上模块表格是否完整
            List<RectanglePixel> scrollBarList = new ArrayList<>();
            coordinateList = ImageUtil.getScreenshotLocation(WIN7_SCROLLBAR_URL,pixelArray);
            scrollBarList.addAll(coordinateList);
            coordinateList = ImageUtil.getScreenshotLocation(WIN10_SCROLLBAR_URL,pixelArray);
            scrollBarList.addAll(coordinateList);
            if(scrollBarList.size() > 0){
                isDownTableExist = true;
                isFull = false;
                coordinateList = ImageUtil.getScreenshotLocation(ADD_BUTTON_URL,pixelArray);
                if(coordinateList.size() == 1){
                    if(coordinateList.get(0).getMaxY() > 200){
                        addY = coordinateList.get(0).getMaxY();
                        isFull = true;
                    }else{
                        isFull = false;
                    }
                }else if(coordinateList.size() == 2){
                    addY = coordinateList.get(1).getMaxY();
                    isFull = true;
                }else if(coordinateList.size() == 0){
                    isFull = false;
                }
            }else{
                isDownTableExist = false;
                isFull = true;
            }
            labelInfoList = getLabelInfos(moduleType,pixelArray,leftX,addY,0);
            log.info("校验上模块，表格是否完整：" + isFull + " 表格下边界值：" + addY + " 是否存在下模块："+isDownTableExist);
        }

        if(moduleType.equals(DOWN) && isCheckDownTable){
            //校验下模块表格是否完整
            coordinateList = ImageUtil.getScreenshotLocation(ADD_BUTTON_URL,pixelArray);
            isFull = false;
            if(coordinateList.size() > 0){
                addY = coordinateList.get(0).getMaxY();
            }else{
                addY = 0;
            }
            labelInfoList = getLabelInfos(moduleType,pixelArray,leftX,addY,0);
            log.info("校验下模块，表格是否完整：" + isFull + " 表格上边界值：" + addY);
        }

       if(isCheckTab){
            //校验模块的tab个数
           uncheckedRightTabList = new ArrayList<>();
           List<RectanglePixel> allUncheckedRightTabList = ImageUtil.getScreenshotLocation(UNCHECKED_RIGHT_TAB_URL,pixelArray);
               for(int i=0; i<allUncheckedRightTabList.size(); i++){
                   RectanglePixel uncheckedRightTab = allUncheckedRightTabList.get(i);
                   boolean isUncheckedTabAtUp = false;
                   if(moduleType.equals(UP)){
                       isUncheckedTabAtUp = uncheckedRightTab.getMiddleY() < addY;
                   }else if(moduleType.equals(DOWN)){
                       isUncheckedTabAtUp = uncheckedRightTab.getMiddleY() >= addY;
                   }
                   if(isUncheckedTabAtUp){
                       uncheckedRightTabList.add(uncheckedRightTab);
                   }
               }
               Collections.sort(uncheckedRightTabList, new RectanglePixelComparator());
           log.info("校验模块的tab个数，模块类型：" + moduleType + " tab个数：" +(uncheckedRightTabList.size() == 0 ? 0 :(uncheckedRightTabList.size() + 1)));
       }

        if(isFull){
            tabNum += 1;
            for(int i=0; i<labelInfoList.size(); i++){
                labelInfoList.get(i).setModuleType(moduleType.equals(UP) ? "上模块":"下模块");
                labelInfoList.get(i).setTabNum(tabNum);
                labelInfoList.get(i).setLabelNum(i+1);
                labelInfoDao.insertLabelInfo(labelInfoList.get(i));
            }
            if(uncheckedRightTabList != null && uncheckedRightTabList.size() > 0){
                RobotUtil.robot.mouseMove(rightX-60,200);
                if(moduleType.equals(UP)){
                    log.info("开始进行上模块tab切换");
                    if(scrollCount > 0){
                        RobotUtil.robot.mouseWheel(-scrollCount);
                        Thread.sleep(500);
                    }
                    RobotUtil.clickLMouse(uncheckedRightTabList.get(0).getMiddleX() + leftX + 30,uncheckedRightTabList.get(0).getMiddleY(),1);
                    uncheckedRightTabList.remove(0);
                    Thread.sleep(300);
                    getModuleLabelInfo(moduleType,tabNum,true,isDownTableExist,false,false,false,0,null,uncheckedRightTabList);
                }else if(moduleType.equals(DOWN)){
                    log.info("开始进行下模块tab切换");
                    RobotUtil.robot.mouseWheel(-10);
                    Thread.sleep(300);
                    RobotUtil.robot.mouseMove(rightX-60,200);
                    RobotUtil.robot.mouseWheel(DOWN_SCROLLCOUNT);
                    Thread.sleep(500);
                    RobotUtil.clickLMouse(uncheckedRightTabList.get(0).getMiddleX() + leftX + 30,uncheckedRightTabList.get(0).getMiddleY(),1);
                    uncheckedRightTabList.remove(0);
                    Thread.sleep(300);
                    getModuleLabelInfo(moduleType,tabNum,false,true,true,false,false,DOWN_SCROLLCOUNT,null,uncheckedRightTabList);
                }

            }else{
                if(isDownTableExist){
                    if(moduleType.equals(UP)){
                        log.info("开始进行从上模块到下模块的切换");
                        if(scrollCount == 0){
                            RobotUtil.robot.mouseMove(rightX-60,200);
                            RobotUtil.robot.mouseWheel(DOWN_SCROLLCOUNT);
                            Thread.sleep(500);
                        }
                        getModuleLabelInfo(DOWN,0,false,isDownTableExist,true,true,false,DOWN_SCROLLCOUNT,null,uncheckedRightTabList);
                    }else if(moduleType.equals(DOWN)){
                        log.info("下模块信息获取完成，流程结束");
                    }
                }else{
                    log.info("上模块信息获取完成，不存在下模块，流程结束");
                }


            }

        }else{
            log.info("滚动条下移，保证模块：" + moduleType + " 内表格信息获取完整");
            RobotUtil.robot.mouseMove(rightX-60,200);
            RobotUtil.robot.mouseWheel(DOWN_SCROLLCOUNT);
            Thread.sleep(800);
            pixelArray = RobotUtil.getScreenPixelARGB(leftX,rightX,0,maxY);
            coordinateList = ImageUtil.getScreenshotLocation(ADD_BUTTON_URL,pixelArray);
            if(coordinateList.size() > 0){
                addY = coordinateList.get(0).getMaxY();
            }
            List<LabelInfo> otherLabelInfoList =  getLabelInfos(moduleType,pixelArray,leftX,addY,DOWN_SCROLLCOUNT);
            labelInfoList = mergeLabelInfoList(labelInfoList,otherLabelInfoList);
            getModuleLabelInfo(moduleType,tabNum,false,isDownTableExist,false,false,true,DOWN_SCROLLCOUNT,labelInfoList,uncheckedRightTabList);

        }

    }


    /**
     * 将属于同一表格内的标签集合进行合并
     * @param labelInfoList 初次截取的标签集合
     * @param otherLabelInfoList 二次截取的标签集合
     * @return
     */
    public static List<LabelInfo> mergeLabelInfoList(List<LabelInfo> labelInfoList, List<LabelInfo> otherLabelInfoList){
        boolean isScroll = false;
        for(int i=0; i<otherLabelInfoList.size(); i++){
            boolean isExist = false;
            LabelInfo otherLabelInfo = otherLabelInfoList.get(i);
            for(int j=0; j<labelInfoList.size(); j++){
                if(otherLabelInfo.getOcrLabelName().equals(labelInfoList.get(j).getOcrLabelName())){
                    isExist = true;
                    break;
                }
            }
            if(!isExist){
                if(!isScroll){
                    otherLabelInfo.setScrollCount(DOWN_SCROLLCOUNT);
                    labelInfoList.add(otherLabelInfo);
                    isScroll = true;
                }else{
                    otherLabelInfo.setScrollCount(0);
                    labelInfoList.add(otherLabelInfo);
                }

            }

        }
        return labelInfoList;
    }

    /**
     * 获取当前界面表格内的所有标签信息
     * @param moduleType 模块类型
     * @param pixelArray 当前界面像素数组
     * @param leftX x轴偏移量
     * @param addY 表格的下边界值（上模块）或上边界值（下模块）
     * @param scrollCount 滚动次数
     * @return
     */
    public List<LabelInfo> getLabelInfos(String moduleType,int [][][] pixelArray,int leftX, int addY,int scrollCount){
        try {

            List<RectanglePixel> leftRectanglePixelList = new ArrayList<>();
            //已填左边框位置集合
            List<RectanglePixel> coordinateList = ImageUtil.getScreenshotLocation(WRITED_LEFT_TEXTBOX_URL,pixelArray);
            leftRectanglePixelList.addAll(coordinateList);
            //未填左边框位置集合
            coordinateList = ImageUtil.getScreenshotLocation(NOT_WRITED_LEFT_TEXTBOX_URL,pixelArray);
            leftRectanglePixelList.addAll(coordinateList);
            //禁止填写左边框位置集合
            coordinateList = ImageUtil.getScreenshotLocation(FROBID_WRITING_LEFT_TEXTBOX_URL,pixelArray);
            leftRectanglePixelList.addAll(coordinateList);
            //文本域左边框位置集合
            coordinateList = ImageUtil.getScreenshotLocation(TEXTFIELD_LEFT_TEXTBOX_URL,pixelArray);
            leftRectanglePixelList.addAll(ImageUtil.dealUselessRectanglePixel(TEXTFIELD_LEFT_TEXTBOX_URL,coordinateList));

            Collections.sort(leftRectanglePixelList, new RectanglePixelComparator());

            List<LabelInfo> labelInfoList = new ArrayList<>();
            //获取左列标签信息
            labelInfoList.addAll(getLeftLabelInfo(moduleType,leftRectanglePixelList,leftX,addY,scrollCount));
            leftRectanglePixelList = getRectanglePixelsFromAggregatePixels(pixelAggregation(leftRectanglePixelList),1);

            List<RectanglePixel> rightRectanglePixelList = new ArrayList<>();
            //已填右边框位置集合
            coordinateList = ImageUtil.getScreenshotLocation(WRITED_RIGHT_TEXTBOX_URL,pixelArray);
            rightRectanglePixelList.addAll(coordinateList);
            //未填右边框位置集合
            coordinateList = ImageUtil.getScreenshotLocation(NOT_WRITED_RIGHT_TEXTBOX_URL,pixelArray);
            rightRectanglePixelList.addAll(coordinateList);
            //禁止填写右边框位置集合
            coordinateList = ImageUtil.getScreenshotLocation(FROBID_WRITING_RIGHT_TEXTBOX_URL,pixelArray);
            rightRectanglePixelList.addAll(coordinateList);

            Collections.sort(rightRectanglePixelList, new RectanglePixelComparator());
            rightRectanglePixelList = getRectanglePixelsFromAggregatePixels(pixelAggregation(rightRectanglePixelList),0);
            //获取右列标签信息
            labelInfoList.addAll(getRightLabelInfo(moduleType,leftRectanglePixelList,rightRectanglePixelList,leftX,addY,scrollCount));
            Collections.sort(labelInfoList, new LabelInfoComparator());
            return labelInfoList;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取左侧标签信息集合
     * @param moduleType 模块类型
     * @param leftRectanglePixelList 左边框像素矩形集合
     * @param leftX x轴偏移量
     * @param addY 表格的下边界值（上模块）或上边界值（下模块）
     * @param scrollCount 滚动次数
     * @return
     */
    private List<LabelInfo> getLeftLabelInfo(String moduleType,List<RectanglePixel> leftRectanglePixelList, int leftX, int addY, int scrollCount) throws IOException {
        List<LabelInfo> labelInfoList = new ArrayList<>();
        int prevY = 0;
        for(int i=0; i<leftRectanglePixelList.size(); i++){
            RectanglePixel rectanglePixel = leftRectanglePixelList.get(i);
            boolean flag = false;
            if(moduleType.equals(UP)){
                flag = rectanglePixel.getMiddleY() >= addY;
            }else if (moduleType.equals(DOWN)){
                flag = rectanglePixel.getMiddleY() < addY;
            }
            if(rectanglePixel.getMiddleY() == prevY || flag){
                continue;
            }else{
                prevY = rectanglePixel.getMiddleY();
                //标签左上角坐标map
                HashMap<String,Object> upperLeftMap = new HashMap<>();
                upperLeftMap.put("middleX",LEFT_INTERVAL);
                upperLeftMap.put("middleY", rectanglePixel.getMinY());
                //标签右下角坐标map
                HashMap<String,Object> lowerRightMap = new HashMap<>();
                lowerRightMap.put("middleX", rectanglePixel.getMiddleX());
                lowerRightMap.put("middleY", rectanglePixel.getMaxY());
                //截取左侧标签
                BufferedImage bufferedImage = ImageUtil.convertScreenToImage(upperLeftMap,lowerRightMap,leftX,0);
                Mat mat = ImageUtil.BufferedImage2Mat(bufferedImage);
                bufferedImage = ImageUtil.imageBinary(mat);
                String ocrName = reviseOcrName(ImageUtil.imageOCR(bufferedImage));
                boolean isCreate = ImageIO.write(bufferedImage,"png",new File("img/label/"+ocrName+".png"));
                if(isCreate){
//                    log.info("图片创建成功！ ocrName: "+ocrName);
                }
                LabelInfo labelInfo = new LabelInfo();
                labelInfo.setMiddleX(rectanglePixel.getMiddleX()+leftX+50);
                labelInfo.setMiddleY(rectanglePixel.getMiddleY());
                labelInfo.setScrollCount(scrollCount);
                labelInfo.setOcrLabelName(ocrName);
                labelInfoList.add(labelInfo);
            }
        }
        return labelInfoList;
    }


    /**
     * 获取右侧标签信息集合
     * @param moduleType 模块类型
     * @param leftRectanglePixelList 左边框像素矩形集合
     * @param rightRectanglePixelList 左边框像素矩形集合
     * @param leftX x轴偏移量
     * @param addY 表格的下边界值（上模块）或上边界值（下模块）
     * @param scrollCount 滚动次数
     * @return
     */
    private List<LabelInfo> getRightLabelInfo(String moduleType,List<RectanglePixel> leftRectanglePixelList,List<RectanglePixel> rightRectanglePixelList,int leftX, int addY,int scrollCount)throws IOException{
        List<LabelInfo> labelInfoList = new ArrayList<>();
        for(int i=0; i<leftRectanglePixelList.size(); i++){
            RectanglePixel leftRectanglePixel = leftRectanglePixelList.get(i);
            for(int j=0; j<rightRectanglePixelList.size(); j++){
                RectanglePixel rightRectanglePixel = rightRectanglePixelList.get(j);
                boolean flag = false;
                if(moduleType.equals(UP)){
                    flag = leftRectanglePixel.getMiddleY() < addY;
                }else if (moduleType.equals(DOWN)){
                    flag =leftRectanglePixel.getMiddleY() >= addY;
                }
                if(leftRectanglePixel.getMiddleY() == rightRectanglePixel.getMiddleY() && flag){
                    //标签左上角坐标map
                    HashMap<String,Object> upperLeftMap = new HashMap<>();
                    upperLeftMap.put("middleX",rightRectanglePixel.getMaxX());
                    upperLeftMap.put("middleY", rightRectanglePixel.getMinY());
                    //标签右下角坐标map
                    HashMap<String,Object> lowerRightMap = new HashMap<>();
                    lowerRightMap.put("middleX", leftRectanglePixel.getMiddleX());
                    lowerRightMap.put("middleY", leftRectanglePixel.getMaxY());
                    //截取右侧标签
                    BufferedImage bufferedImage = ImageUtil.convertScreenToImage(upperLeftMap,lowerRightMap,leftX,0);
                    Mat mat = ImageUtil.BufferedImage2Mat(bufferedImage);
                    bufferedImage = ImageUtil.imageBinary(mat);
                    String ocrName = reviseOcrName(ImageUtil.imageOCR(bufferedImage));
                    boolean isCreate = ImageIO.write(bufferedImage,"png",new File("img/label/"+ocrName+".png"));
                    if(isCreate){
//                        log.info("图片创建成功！ ocrName: "+ocrName);
                    }
                    LabelInfo labelInfo = new LabelInfo();
                    labelInfo.setMiddleX(leftRectanglePixel.getMiddleX()+leftX+50);
                    labelInfo.setMiddleY(leftRectanglePixel.getMiddleY());
                    labelInfo.setScrollCount(scrollCount);
                    labelInfo.setOcrLabelName(ocrName);
                    labelInfoList.add(labelInfo);
                }
            }
        }
        return labelInfoList;
    }

    /**
     * 根据middleY 对 像素矩形进行聚合
     * @param rectanglePixelList
     * @return
     */
    private List<List<RectanglePixel>> pixelAggregation(List<RectanglePixel> rectanglePixelList){
        List<List<RectanglePixel>> list = new ArrayList<>();
        for(int i=0; i<rectanglePixelList.size(); i++){
            List<RectanglePixel>  pixelList = null;
            if( i == 0){
                pixelList = new ArrayList<>();
                pixelList.add(rectanglePixelList.get(0));
                list.add(pixelList);
            }else{
                boolean isEquals = false;
                for(int j=0; j<list.size() && !isEquals; j++){
                    for(int k=0; k<list.get(j).size(); k++){
                        if(list.get(j).get(k).getMiddleY() == rectanglePixelList.get(i).getMiddleY()){
                            list.get(j).add(rectanglePixelList.get(i));
                            isEquals = true;
                            break;
                        }
                    }
                }

                if(!isEquals){
                    pixelList = new ArrayList<>();
                    pixelList.add(rectanglePixelList.get(i));
                    list.add(pixelList);
                }
            }

        }
        return list;
    }

    /**
     * 取出每行的第 listIndex 个像素矩形
     * @param list 聚合后的像素矩形集合
     * @param listIndex
     * @return
     */
    private List<RectanglePixel> getRectanglePixelsFromAggregatePixels(List<List<RectanglePixel>> list, int listIndex){
        List<RectanglePixel> rectanglePixelList = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            if(list.get(i).size() >= listIndex + 1){
                rectanglePixelList.add(list.get(i).get(listIndex));
            }
        }
        return rectanglePixelList;
    }


    //去除中文空格、标点等特殊符号
    private String reviseOcrName(String ocrName){
        ocrName = ocrName.replaceAll("[\\s\\p{Zs}]+","").replace("‘","").replace("^","").replace("0","")
                .replace("o","").replace("x","").replace("|","").replace("。","").replace("\\","")
                .replace("’","").replace("*","").replace("'","").replace("/","");
        return ocrName;
    }

    public static String getOCRNameByLabelName(List<String> ocrNameList,String labelName){
        List<String> possibleOCRNameList = new ArrayList<>();
        for(int i=0; i<ocrNameList.size(); i++){
            String possibleOCRName = ocrNameList.get(i);
            if(possibleOCRName.length() == labelName.length()){
                possibleOCRNameList.add(possibleOCRName);
            }
        }

        List<HashMap<String,Object>> ocrNameMapList = new ArrayList<>();
        for(int i=0; i<possibleOCRNameList.size(); i++){
            char [] ocrNameArray = possibleOCRNameList.get(i).toCharArray();
            char [] labelNameArray = labelName.toCharArray();
            int sameCharNum = 0;
            for(int j=0; j<ocrNameArray.length; j++){
                if(ocrNameArray[j] == labelNameArray[j]){
                    sameCharNum++;
                }
            }
            HashMap<String,Object> ocrNameMap = new HashMap<>();
            ocrNameMap.put("ocrName",possibleOCRNameList.get(i));
            ocrNameMap.put("sameCharNum",sameCharNum);
            ocrNameMapList.add(ocrNameMap);
        }
        Collections.sort(ocrNameMapList,new OCRNameMapComparator());
        return ocrNameMapList.get(0).get("ocrName").toString();
    }





}
