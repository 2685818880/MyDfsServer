package mydfs.storage.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import mydfs.storage.server.MydfsClient;
import mydfs.storage.server.MydfsStorageServer;
import mydfs.storage.server.MydfsTrackerServer;

public class Main {
	// host port worker basepath
	public static void main(String[] args) {
		try{
			InputStream stream = Main.class.getClassLoader().getResourceAsStream("mydfs.properties");
			Properties properties = new Properties();
			properties.load(stream);
			final String host = (String) properties.get("mydfs.host");
			final String port = (String) properties.get("mydfs.port");
			final String worker = (String) properties.get("mydfs.worker");
			final String basepath = (String) properties.get("mydfs.basepath");
			final String httpPort = (String) properties.get("mydfs.http.port");
			
			Server httpserver=new Server(Integer.parseInt(httpPort));
			if(!httpserver.isRunning()){
				httpserver.setHandler(new AbstractHandler() {
					
					@Override
					public void handle(String arg0, Request request, HttpServletRequest httpRequest,
							HttpServletResponse httpResponse) throws IOException, ServletException {
						ServletOutputStream out = httpResponse.getOutputStream();
						PrintWriter writer=new PrintWriter(out,true);
						String httpQueryArgs=httpRequest.getQueryString();
						if(!String.valueOf(httpQueryArgs).equals("null")){
							String url=httpRequest.getRequestURL().toString();
							MydfsTrackerServer storageTracker=new MydfsTrackerServer(host, Integer.parseInt(port));
							MydfsClient.fushImage(url, httpQueryArgs, out, storageTracker);
						}
						
						writer.println("mydfsServer listen port:"+port);
						writer.println("mydfsServer thread woker:"+worker);
						writer.println("mydfsserver basebase:"+basepath);
						writer.close();
					}
				});
			}
			//启动mydfsserver服务
			MydfsStorageServer storageServer = new MydfsStorageServer(Integer.parseInt(port), basepath, Integer.parseInt(worker),host);
			storageServer.startServer();
			System.out.println("url access:http://localhost:5555");
			System.out.println("Listen the host:" + host);
			System.out.println("Listen the port:" + port);
			System.out.println("mydfsServer worker number:" + worker);
			System.out.println("the mydfsserver basepath:" + basepath);
			httpserver.start();
			httpserver.join();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
