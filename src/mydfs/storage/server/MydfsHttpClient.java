package mydfs.storage.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/*图片或视频数据输出*/
public class MydfsHttpClient extends HttpServlet{

	private static final long serialVersionUID = -2163900148319741678L;
	WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext(); 
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		MydfsTrackerServer storageTracker=(MydfsTrackerServer)context.getBean("mydfsTrackerServer");
		System.out.println("storageTracker:"+storageTracker.hashCode());
		String url = req.getRequestURL().toString();
		InputStream inputStream = storageTracker.receiveData(url);
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		if(url.contains(".swf"))resp.setContentType("application/x-shockwave-flash");
		else resp.setContentType("image/*");
		OutputStream os = resp.getOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(os);
		byte[] buf = new byte[1024];
		int len;
		while((len = bis.read(buf)) > 0){
			out.flush();
			out.write(buf, 0, len);
		}
		out.flush();
		bis.close();
		out.close();
		
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
