package mydfs.storage.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import mydfs.storage.server.MydfsStorageServer;
import mydfs.storage.utils.PropertiesUtil;

public class Main {
	public static void main(String[] args) {
		try{
			String filename="mydfs.properties";
			createProfile(filename);
			final String host=PropertiesUtil.getValue("mydfs.host",filename);
			final Integer port=Integer.parseInt(PropertiesUtil.getValue("mydfs.port",filename));
			final Integer worker =Integer.parseInt(PropertiesUtil.getValue("mydfs.worker",filename));
			final String basepath =PropertiesUtil.getValue("mydfs.basepath",filename);
			final Integer httpPort =Integer.parseInt(PropertiesUtil.getValue("mydfs.http.port",filename));
			//启动mydfsserver服务
			MydfsStorageServer storageServer = new MydfsStorageServer(port, basepath,worker,host);
			storageServer.startServer();
			//使用jetty做http服务器,显示一些统计信息
			Server httpserver=new Server(httpPort);
			if(!httpserver.isRunning()){
				httpserver.setHandler(new AbstractHandler() {
					
					@Override
					public void handle(String arg0, Request request, HttpServletRequest httpRequest,
							HttpServletResponse httpResponse) throws IOException, ServletException {
						ServletOutputStream out = httpResponse.getOutputStream();
						PrintWriter writer=new PrintWriter(out,true);
						//获取文件上传的个数
						final String fileCount=PropertiesUtil.getValue("fileCount", "statistics");
						writer.println("mydfsServer listen port:"+port);
						writer.println("mydfsServer thread woker:"+worker);
						writer.println("mydfsserver basebase:"+basepath);
						writer.println("mydfsserver file count:"+fileCount);
						writer.close();
					}
				});
			}
			System.out.println("url access:http://localhost:5555");
			System.out.println("Listen the host:" + host);
			System.out.println("Listen the port:" + port);
			System.out.println("mydfsServer worker number:" + worker);
			System.out.println("the mydfsserver basepath:" + basepath);
			System.out.println("mydfsserver start success!");
			httpserver.start();
			httpserver.join();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//启动的时候就在jar包之外创建配置文件,如果没有该配置文件就创建
	private static void createProfile(String filename){
		File file=new File(filename);
		if(!file.exists()){
			try {
				
				file.createNewFile();
				PropertiesUtil.setValue("mydfs.host","localhost", filename, "");
				PropertiesUtil.setValue("mydfs.port","9999", filename, "");
				PropertiesUtil.setValue("mydfs.worker","5", filename, "");
				PropertiesUtil.setValue("mydfs.basepath","C:/data/mydfs/store", filename, "");
				PropertiesUtil.setValue("mydfs.http.port","5555", filename, "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
