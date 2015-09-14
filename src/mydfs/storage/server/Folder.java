package mydfs.storage.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Folder {
	private static String[] varchar={
				"0","1","2","3","4",
				"5","6","7","8","9",
				"A","B","C","D","E","F"};
	public static String basepath;
	
	public static void initFolder(String basepath){
		Folder.basepath=basepath;
		//创建失败
		File file=new File(basepath);
		if(!file.exists()&&!file.mkdirs())
		  throw new RuntimeException("folder create error.do you hava authority create folder or your directory write error ?");
		//创建子文件夹
		try{
			createSubFolder(file);
			File[] subFolders = file.listFiles();
			for (int i = 0; i < subFolders.length; i++) {
				createSubFolder(subFolders[i]);
			}
			//创建统计文件,用来统计文件个数等
			createStatisticFile(basepath);
			System.out.println("Success:all folder create success");
		}catch(Exception ex){
			System.out.println("Error:folder create error.error message:"+ex.getMessage());
		}
	}

	/**Begin wuqiwei 2015-9-14  创建统计文件*/
	private static void createStatisticFile(String basepath)
			throws IOException, FileNotFoundException {
		File statisticsFile=new File(basepath+"/statistics");
		statisticsFile.createNewFile();
		Properties properties=new Properties();
		FileInputStream inputStream = new FileInputStream(statisticsFile);
		properties.load(inputStream);
		String fileCount = properties.getProperty("fileCount");
		OutputStream fos = new FileOutputStream(statisticsFile);
		if (String.valueOf(fileCount).equals("null")) {
			properties.setProperty("fileCount", "0");
			properties.store(fos, "statistics file count");
		}
		inputStream.close();
	}
	/**End wuqiwei 2015-9-14 创建统计文件*/
	
	//创建子文件夹
	private static void createSubFolder(File file) {
		String subfolder;
		for (int i = 0; i < varchar.length; i++) {
			for(int j=0;j<varchar.length;j++){
				subfolder=file.getAbsolutePath()+"/"+varchar[i]+varchar[j];
				//创建子文件夹
				File dfsFile=new File(subfolder);
				if(!dfsFile.exists()&&dfsFile.mkdirs())
					System.out.println("create mydfs subfolder:"+dfsFile.getAbsolutePath());
			}
		}
	}
	public static String getStoragePath(String md5){
    	String subfolder1 = md5.substring(0, 2).toUpperCase();
    	String subfolder2 = md5.substring(2, 4).toUpperCase();
    	String fileName=md5.substring(4);
    	return basepath+"/"+subfolder1+"/"+subfolder2+"/"+fileName;
	}
}
