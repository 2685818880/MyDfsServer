package mydfs.storage.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import mydfs.storage.server.MydfsStorageServer;
import mydfs.storage.server.MydfsTrackerServer;


public class StorageServerTest {
	
	
	@Test
	public  void startUp() throws IOException{
		MydfsStorageServer storageServer=new MydfsStorageServer(9999,"D:/data/mydfs/store",4,"127.0.0.1");
		storageServer.startServer();
	}
	/*public static void main(String[] args) throws IOException{
		MydfsTrackerServer client=new MydfsTrackerServer("localhost", 9999);
		InputStream inputStream = new FileInputStream("D:/20130412062742872.jpg");
		String storepath = client.upload(inputStream,"jpg");
		System.out.println(storepath);
	}*/
	public void remove(){
		MydfsTrackerServer client=new MydfsTrackerServer("127.0.0.1", 9999);
		String url="http://www.jobs.com/group/M00/78/79/53D9-34E2-4110-BDBA-8BF808E2C4BD.swf";
		boolean success = client.removeData(url);
		System.out.println("删除情况："+success);
	}
	@Test
	public  void receiveDataTest() {
		MydfsTrackerServer client=new MydfsTrackerServer("127.0.0.1", 9999);
		String url="http://www.jobs.com/group/M00/EF/2C/3696-CCD2-43E7-9854-6B51F6AA2315.jpg?w=100&h=100";
		client.receiveData(url);
	}
}
