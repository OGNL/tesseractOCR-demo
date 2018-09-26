package util;


import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyUtil {



	public static String getValueByKey(String key){
		Properties prop =  new  Properties();
	    try  {
			prop.load(new InputStreamReader(PropertyUtil.class.getResourceAsStream("/database.properties"), "UTF-8"));
			return prop.getProperty(key).trim();

	    }catch(Exception e){
	    	System.out.println("Load config.properties has an error!");
	    	return "";
	    }

	}

	

}
