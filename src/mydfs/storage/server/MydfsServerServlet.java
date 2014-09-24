package mydfs.storage.server;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mydfs.storage.server.MydfsClient;
import mydfs.storage.server.MydfsTrackerServer;

public class MydfsServerServlet extends HttpServlet {

	private static final long serialVersionUID = 2761239131555841058L;
	private String host;
	private int port;
	@Override
	public void init(ServletConfig config) throws ServletException {
		String[] hostPort = config.getInitParameter("mydfsServerHost:Port").split(":");
		host=hostPort[0];
		port=Integer.valueOf(hostPort[1]);
		super.init(config);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		MydfsTrackerServer storageTracker=new MydfsTrackerServer(host, port);
		String url=req.getRequestURL().toString();
		String httpQueryString=req.getQueryString();
		MydfsClient.fushImage(url, httpQueryString, resp.getOutputStream(), storageTracker);
	}
}
