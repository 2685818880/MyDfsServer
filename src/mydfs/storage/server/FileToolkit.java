package mydfs.storage.server;

import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 纯Java文件操作工具，支持文件、文件夹的复制、删除、移动操作。
 * 
 * @author leizhimin 2010-6-2 16:12:14
 */
public class FileToolkit {
	public FileToolkit() {
	}

	/** 文件重命名 */
	public static boolean reName(File srcFile, File desFile) {
		try {
			String fileFrom = srcFile.getAbsolutePath();
			String fileTo = desFile.getAbsolutePath();
			FileInputStream in = new java.io.FileInputStream(fileFrom);
			FileOutputStream out = new FileOutputStream(fileTo);
			byte[] buf = new byte[1024];
			int count;
			while ((count = in.read(buf)) > 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
			srcFile.setWritable(true);
			srcFile.delete();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

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
	public static void flushImage(String url,OutputStream outputStream,File file)  {
		byte[] buf=new byte[1024];
		int len=0;
		InputStream inputStream=null;
		BufferedInputStream bufferedInputStream=null;
		BufferedOutputStream bufferedOutputStream=null;
		try {
			bufferedOutputStream=new BufferedOutputStream(outputStream);
			inputStream=new FileInputStream(file);
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
	public static File diggingFile(String storepath) {
		File file=new File(storepath);
		// 如果文件不存在，指定文件返回一张默认图片
		if(!file.exists()){
			URL classPath = MydfsStorageServer.class.getResource("");
			String unfindImg=(classPath.toString()).replace("file:", "");
			unfindImg+="404.jpg";
			System.out.println("current file:"+unfindImg);
			file=new File(unfindImg);
		}
		return file;
	}

	// /98/00/A403-7FF4-4980-93A1-2B20C68CB59A-h100xw100.jpg
	public static void main(String[] args) {
		/*String storepath = "/98/00/A403-7FF4-4980-93A1-2B20C68CB59A.jpg";
		System.out.println(thumbnailPath(storepath, "100", "100"));*/
		URL classPath = MydfsStorageServer.class.getResource("");
		System.out.println(classPath);
	}
}