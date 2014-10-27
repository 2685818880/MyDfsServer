package mydfs.storage.main;

import java.io.IOException;
import java.io.InputStream;
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
		System.out.println("http://www.believeus.cn 软件外包请联系我们");
		System.out.println("mydfsStoreServer start up host:"+host+" port:"+port+" worker number:"+worker+" basepath:"+basepath);
		try {
			storageServer.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
