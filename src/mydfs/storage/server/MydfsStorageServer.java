package mydfs.storage.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuqiwei
 * Email 1058633117@qq.com
 * Data   2014-06-05
 * AddReason 文件上传会出现视频和附件这些比较大的文件
 * 			 并且,并且后期tomcat进行集群的时候，访问
 * 			 公共图片数据的时候，常规的方式出现访问
 * 			 上的烦人事,数据迁移的时候变得困难。
 *           故自己写了一个文件管理系统，管理上传的文件
 * */
public class MydfsStorageServer {

	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private boolean stop=false;
	//监听本机的哪个ip地址
	private String host;
	// 本地监听端口
	private int port;
	private int workers;
	// 文件存放的基目录
	private String basepath;
	// 返回路径的前缀
	private String pathPrefix;
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getBasepath() {
		return basepath;
	}

	public void setBasepath(String basepath) {
		this.basepath = basepath;
	}

	public int getWorkers() {
		return workers;
	}

	public void setWorkers(int workers) {
		this.workers = workers;
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public MydfsStorageServer() {
		this.pathPrefix = "group/M00";
	}

	public MydfsStorageServer(int port, String basepath, int workers,String host) {
			this.port = port;
			this.workers = workers;
			this.basepath = basepath;
			this.host=host;
			// 指定前缀方便访问之用
			this.pathPrefix = "group/M00";
	}

	public void startServer() throws IOException {
		serverSocket = new ServerSocket();
		//ServerSocket 绑定的ip和端口号
		SocketAddress socketAddress=new InetSocketAddress(host,port);
		serverSocket.bind(socketAddress);
		// 初始化存储文件文件夹
		Folder.initFolder(basepath);
		
		executorService = Executors.newFixedThreadPool(workers);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Date date=new Date();
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				while (true) {
					if(stop){break;}
					try {
						System.out.println("start up time:"+currentTime+" Storage Server start!");
						final Socket socket = serverSocket.accept();
						//保存客户端上传的文件,并返回文件存储的路径给客户端
						// 每次接受一个客户端连接都执行一个线程
						executorService.execute(new Runnable() {

							@Override
							public void run() {
								try {
									InputStream in = socket.getInputStream();
									DataInputStream datais=new DataInputStream(in);
									String status = datais.readUTF();
									//获取文件数据
									if(status.equals("receive")){
										// 读取到请求的url
										String url = datais.readUTF();
										CoreServer.sendToClient(socket, url,basepath);
									}
									// 文件上传
									else if(status.equals("upload")){
										// 获取文件后缀名
										String extension=datais.readUTF();
										CoreServer.clientUpload(socket, in, datais,extension,basepath,pathPrefix);
									}
									// 删除StoreServer中的数据
									else if(status.equals("remove")){
										CoreServer.clientRemove(socket, datais,basepath);
									}
									
								} catch (IOException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}finally{
									try {
										if (socket != null&& !socket.isClosed()) {
											socket.close();
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();	

	}

	public void stopServer() {
		stop=true;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
			if (executorService != null) {
				executorService.shutdown();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
