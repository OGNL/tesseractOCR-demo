package test;

import service.ImageService;

import java.sql.SQLException;

/**
 * Created by zhangtao on 2017/8/30.
 */
public class ImageTest {

    private static ImageService imageService = new ImageService();
    public static void main(String [] args){
        try {
            Thread.sleep(2000);
            imageService.getModuleLabelInfo("UP",0,true,true,false,true,false,0,null,null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }





}
