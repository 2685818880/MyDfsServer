package mydfs.storage.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import mydfs.storage.server.MydfsStorageServer;

public class Main {
	// host port worker basepath
	public static void main(String[] args) {
		InputStream stream = Main.class.getClassLoader().getResourceAsStream("mydfs.properties");
		Properties properties=new Properties();
		try {
			properties.load(stream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String host=(String) properties.get("host");
		String port=(String) properties.get("port");
		String worker=(String) properties.get("worker");
		String basepath=(String) properties.get("basepath");
		MydfsStorageServer storageServer=new MydfsStorageServer(Integer.parseInt(port),basepath,Integer.parseInt(worker),host);
		System.err.println("http://www.believeus.cn 研发文件上传管理系统");
		System.out.println("Listen the host:"+host);
		System.out.println("Listen the port:"+port);
		System.out.println("mydfsServer worker number:"+worker);
		System.out.println("the mydfsserver basepath:"+ basepath);
		try {
			storageServer.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
