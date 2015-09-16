package mydfs.storage.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MydfsServerServlet extends HttpServlet {

	private static final long serialVersionUID = 2761239131555841058L;
	private String host;
	private int port;
	public MydfsServerServlet(){}

	public MydfsServerServlet(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		String[] hostPort = config.getInitParameter("mydfsServerHost:Port").split(":");
		host=hostPort[0];
		port=Integer.valueOf(hostPort[1]);
		super.init(config);
	}
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		MydfsTrackerServer storageTracker=new MydfsTrackerServer(host, port);
		String url=req.getRequestURL().toString();
		String httpQueryString=req.getQueryString();
		fushImage(url, httpQueryString, resp.getOutputStream(), storageTracker);
	}
	
	private void fushImage(String url,String httpQueryString,OutputStream outputStream, MydfsTrackerServer storageTracker){
		System.out.println("getQueryString():"+httpQueryString);
		if(httpQueryString!=null){
			 url = url+"?"+httpQueryString;
		}
		System.out.println(url);
		InputStream inputStream = storageTracker.receiveData(url);
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		BufferedOutputStream out = new BufferedOutputStream(outputStream);
		byte[] buf = new byte[1024];
		int len;
		try {
			while((len = bis.read(buf)) > 0){
				out.flush();
				out.write(buf, 0, len);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bis!=null)
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(out!=null)
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
