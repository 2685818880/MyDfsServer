package mydfs.storage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**begin wuqiwei 2015-09-16 读取属性文件的工具类 */
public class PropertiesUtil{
	// 设置属性
	public static void setValue(String key, String value,String filename,String comment) {
		try {
			File file = new File(filename);
			if(file.exists()){file.createNewFile();}
			FileInputStream inputStream = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(inputStream);
			properties.setProperty(key, value);
			properties.store(new FileOutputStream(filename), comment);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取属性值
	public static String getValue(String key,String filename) {
		String value = null;
		try {
			File file = new File(filename);
			if(!file.exists()){file.createNewFile();}
			FileInputStream in=new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(in);
			value = (String) properties.get(key);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
}
/**begin wuqiwei 2015-09-16 读取属性文件的工具类 */