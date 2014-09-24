package mydfs.storage.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import mydfs.storage.server.MydfsStorageServer;
import mydfs.storage.server.MydfsTrackerServer;


public class StorageServerTest {
	
	//使用main方法，可以将服务一直开启 
	public static void main(String[] args) throws Exception {
			MydfsStorageServer storageServer=new MydfsStorageServer(9999,"D:/data/mydfs/store",4,"127.0.0.1");
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
		InputStream inputStream = new FileInputStream("D:/20130412062742872.jpg");
		String storepath = client.upload(inputStream,"jpg");
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
		String url="http://www.jobs.com/group/M00/9A/6F/8C66-C63F-4296-BDCC-AAF0BF1343E7.flv?w=100&h=100";
		InputStream inputStream = client.receiveData(url);
		int data=0;
		while((data=inputStream.read())!=-1){
			System.out.print(data);
		}
	}
	
}
