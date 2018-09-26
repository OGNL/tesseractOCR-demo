package test;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by zhangtao on 2017/11/16.
 */
public class OCRTest {



    public static void main(String [] args){
        ITesseract instance = new Tesseract();  // JNA Interface Mapping

        try {
            BufferedImage image = ImageIO.read(new File("img/label/日乓一渲否失职.png"));
            instance.setDatapath(System.getenv("TESSDATA_PREFIX"));//设置OCR的本地路径
//            System.out.println(System.getenv("TESSDATA_PREFIX"));
            instance.setLanguage("chi_sim"); //加载语言包
            System.out.println(instance.doOCR(image));
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
