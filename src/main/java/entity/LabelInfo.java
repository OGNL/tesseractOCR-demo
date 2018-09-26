package entity;

/**
 * 标签信息
 * Created by zhangtao on 2017/11/6.
 */
public class LabelInfo {

    private int id;
    private String ocrLabelName;//OCR识别后的标签名
    private String lineName;//条线
    private String step;//步骤
    private String labelType;//标签类型
    private String moduleType;//标签所处模块类型
    private int middleX;//输入框中心点的X轴坐标
    private int middleY;//输入框中心点的Y轴坐标
    private int scrollCount;//滚动次数
    private int labelNum;//标签在表格内的编号
    private int tabNum;//标签所处tab的编号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOcrLabelName() {
        return ocrLabelName;
    }

    public void setOcrLabelName(String ocrLabelName) {
        this.ocrLabelName = ocrLabelName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
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

    public int getScrollCount() {
        return scrollCount;
    }

    public void setScrollCount(int scrollCount) {
        this.scrollCount = scrollCount;
    }

    public int getLabelNum() {
        return labelNum;
    }

    public void setLabelNum(int labelNum) {
        this.labelNum = labelNum;
    }


    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public int getTabNum() {
        return tabNum;
    }

    public void setTabNum(int tabNum) {
        this.tabNum = tabNum;
    }
}
