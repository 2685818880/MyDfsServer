package mydfs.storage.server;

import java.io.File;
import mydfs.storage.utills.PropertiesUtil;

public class Folder {
	private static String[] varchar={
				"0","1","2","3","4",
				"5","6","7","8","9",
				"A","B","C","D","E","F"};
	public static String basepath;
	
	/*因为linux有权限，担心在程序运行的过程中创建不了文件夹，所以就在启动的时候创建了。*/
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
	private static void createStatisticFile(String basepath){
		String filename="logs/statistics";
		String fileCount=PropertiesUtil.getValue("fileCount",filename);
		if (String.valueOf(fileCount).equals("null")) {
			PropertiesUtil.setValue("fileCount","0", filename,"statistics file count");
		}
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
