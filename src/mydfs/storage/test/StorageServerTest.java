package mydfs.storage.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
//import org.junit.Test;
import mydfs.storage.server.MydfsStorageServer;
import mydfs.storage.server.MydfsTrackerServer;


public class StorageServerTest {
	
/*	//使用main方法，可以将服务一直开启 
	public static void main(String[] args) throws Exception {
			MydfsStorageServer storageServer=new MydfsStorageServer(9999,"E:/data/mydfs/store",4,"127.0.0.1");
			storageServer.startServer();
	}
	
	// 如果使用单元测试会将开启后的服务关闭
	@Test
	public  void startUp() throws Exception{
		MydfsStorageServer storageServer=new MydfsStorageServer(9999,"D:/data/mydfs/store",4,"127.0.0.1");
		storageServer.startServer();
	}
	
	@Test
	public  void upload() throws IOException{ 
		MydfsTrackerServer client=new MydfsTrackerServer("localhost", 9999);
		InputStream inputStream = new FileInputStream("C:/Users/wuqiwei/Desktop/林梦凡图片/40.jpg");
		String storepath = client.upload(inputStream,"git");
		System.out.println(storepath);
	}
	@Test
	public void remove(){
		MydfsTrackerServer client=new MydfsTrackerServer("127.0.0.1", 9999);
		String url="http://www.jobs.com/group/M00/78/79/53D9-34E2-4110-BDBA-8BF808E2C4BD.swf";
		boolean success = client.removeData(url);
		System.out.println("删除情况："+success);
	}
	@Test
	public  void receiveDataTest() throws Exception {
		MydfsTrackerServer client=new MydfsTrackerServer("127.0.0.1", 9999);
		String url="http://localhost:8080/group/M00/B3/89/C6B1-D7D0-4A38-927D-647403C3F23B.gif?w=200&h=100";
		InputStream inputStream = client.receiveData(url);
		int data=0;
		while((data=inputStream.read())!=-1){
			System.out.print(data);
		}
	}*/
	public static void main(String[] args) throws Exception {
			MydfsTrackerServer client=new MydfsTrackerServer("localhost", 9999);
			InputStream inputStream = new FileInputStream("C:/Users/wuqiwei/Desktop/林梦凡图片/40.jpg");
			String storepath = client.upload(inputStream,"jpg");
			System.out.println(storepath);
	}
}
