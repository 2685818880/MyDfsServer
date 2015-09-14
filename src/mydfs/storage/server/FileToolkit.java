package mydfs.storage.server;

import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 纯Java文件操作工具，支持文件、文件夹的复制、删除、移动操作。
 * 
 * @author leizhimin 2010-6-2 16:12:14
 */
public class FileToolkit {

	// 获取url中参数的宽
	public static String getWidth(String url) {
		String width = "";
		Pattern wregex = Pattern.compile("(?<=(\\?w=))[0-9]+");
		Matcher matcher = wregex.matcher(url);
		if (matcher.find()) {
			width = matcher.group();
		}
		return width;
	}

	// 获取url中参数的高
	public static String getHeigth(String url) {
		String heigth = "";
		Pattern regex = Pattern.compile("(?<=(&h=))[0-9]+");
		Matcher matcher = regex.matcher(url);
		if (matcher.find()) {
			heigth = matcher.group();
		}
		return heigth;
	}

	// 根据url和参数的宽和高判断缩略图的存储地址
	// url=/98/00/A403-7FF4-4980-93A1-2B20C68CB59A.jpg?w=100&h=100
	public static String thumbnailPath(String storepath, String width, String heigth) {
		String thumbnailPath=storepath.replaceAll("\\?w=[0-9]+&h=[0-9]+", "");
		String extension = getExtensionName(thumbnailPath);
		String thumbnailFlag="-w"+width+"xh"+heigth;
		thumbnailPath=thumbnailPath.replace("."+extension, thumbnailFlag);
		thumbnailPath+="."+extension;
		return thumbnailPath;
	}

	// 获取文件扩展名
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
	public static void flushImage(String url,OutputStream outputStream,InputStream inputStream)  {
		byte[] buf=new byte[1024];
		int len=0;
		BufferedInputStream bufferedInputStream=null;
		BufferedOutputStream bufferedOutputStream=null;
		try {
			bufferedOutputStream=new BufferedOutputStream(outputStream);
			bufferedInputStream=new BufferedInputStream(inputStream);
			while ((len=bufferedInputStream.read(buf))!=-1) {
				bufferedOutputStream.write(buf, 0, len);
				bufferedOutputStream.flush();
			}
			System.out.println(url+" output success");
			bufferedOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bufferedInputStream!=null){
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bufferedOutputStream!=null){
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream!=null){
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public InputStream diggingFile(String storepath) throws Exception {
		File file=new File(storepath);
		// 如果文件不存在，指定文件返回一张默认图片
		if(!file.exists()){
			 String packageName=this.getClass().getPackage().getName().replace(".", "/");
			 // getResourceAsStream方法比getResource好,getResourceAsStream把压缩在jar包中的文件也当放到classpath中
			 String image="404.jpg";
			 if(storepath.contains("the-file-is-not-support-thumbs.png")){
				 image="unSupport.png";
			 }
			 InputStream inputStream=this.getClass().getResourceAsStream("/"+packageName+"/"+image);
			 return inputStream;
		}
		return new FileInputStream(file);
		
	}
	// 判断该是否该文件是否是图片文件
	public static boolean isCanThumbnail(String url){
		String extension = getExtensionName(url);
		boolean can=false;
		if(extension.equalsIgnoreCase("jpg"))
			can=true;
		else if(extension.equalsIgnoreCase("bmp")){
			can=true;
		}else if (extension.equalsIgnoreCase("png")) {
			can=true;
		}else if (extension.equalsIgnoreCase("jpeg")) {
			can=true;
		}
		return can;
	}
	public static String removeHost(String url){
		return url.substring(url.lastIndexOf("/")-6);
	}
	
	//设置属性
	public static void setProperty(String filepath,String key ,String value,String comment){
		try {
			File statisticsFile=new File(filepath);
			FileInputStream inputStream=new FileInputStream(statisticsFile);
			Properties properties=new Properties();
			properties.load(inputStream);
			properties.setProperty(key,value);
			properties.store(new FileOutputStream(filepath),comment);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//获取属性值
	public static String getProperty(String filepath,String key){
		String value="";
		try {
			File statisticsFile=new File(filepath);
			FileInputStream inputStream;
			inputStream = new FileInputStream(statisticsFile);
			Properties properties=new Properties();
			properties.load(inputStream);
			value=(String)properties.get(key);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}