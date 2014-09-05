package mydfs.storage.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
										System.out.println("access url:"+url);
										// 无参数的url正则表达式
										String parameterlessPattern="/[A-Z0-9]{2}/[A-Z0-9]{2}/[A-Za-z0-9-]+\\.[a-zA-Z]+";
										Matcher parameterless = Pattern.compile(parameterlessPattern).matcher(url);
										// 有参数的url正则表达式
										String parameterPattern="/[A-Z0-9]{2}/[A-Z0-9]{2}/[A-Za-z0-9-]+\\.[a-zA-Z]+(\\?w=[0-9]+&h=[0-9]+){0,1}";
										Matcher parameter = Pattern.compile(parameterPattern).matcher(url);
										File file=null;
										OutputStream outputStream = socket.getOutputStream();
										// 客户端根据参数获取对应的缩略图片
										if(parameter.find()){
											String storepath_parameter = basepath+ parameter.group();
											System.out.println("storepath:"+ storepath_parameter);
											String heigth = FileToolkit.getHeigth(storepath_parameter);
											String width = FileToolkit.getWidth(storepath_parameter);
											// 获取缩略图路径
											String thumbnailPath = FileToolkit.thumbnailPath(storepath_parameter,width, heigth);
											file = new File(thumbnailPath);
											// 如果没有缩率图文件,
											if (!file.exists()) {
												String storepath=storepath_parameter.replaceAll("\\?w=[0-9]+&h=[0-9]+", "");
												file=new File(storepath);
												// 如果原文件存在,生成缩略图
												if(file.exists()){
													ScaleImage scaleImage = new ScaleImage();
													scaleImage.thumbnail(
															storepath,
															thumbnailPath,
															Integer.valueOf(width),
															Integer.valueOf(heigth),
															FileToolkit.getExtensionName(storepath));
													file = new File(thumbnailPath);
													System.out.println("thumbnailPath:"+thumbnailPath);
												// 文件不存在返回默认的图片
												}else {
													file=FileToolkit.diggingFile(basepath+"the-file-is-not-exist.jpg");
												}
											}
											
										// 客户端获取原图
										}else if(parameterless.find()){
											String storepath=basepath+parameterless.group();
											System.out.println("storepath:"+storepath);
											file = FileToolkit.diggingFile(storepath);
										// 如果所有正则都不匹配返回一张默认的图片
										}else {
											file=FileToolkit.diggingFile(basepath+"the-file-is-not-exist.jpg");
										}
										FileToolkit.flushImage(url, outputStream, file);
									}
									// 文件上传
									else if(status.equals("upload")){
										// 获取文件后缀名
										String extension=datais.readUTF();
										System.out.println("client file stuffix: "+extension);
										String uuid = UUID.randomUUID().toString().toUpperCase();
										// 得到存储路径
										String storepath = Folder.getStoragePath(uuid);
										// 文件存储路径
										File file = new File(storepath);
										OutputStream out = new FileOutputStream(file);
										BufferedOutputStream bos = new BufferedOutputStream(out);
										// 获取文件流的大小
										int size = datais.readInt();
										System.out.println("server file size:" + size);
										// InputStreamReader将字节流转化为字符流
										BufferedInputStream br = new BufferedInputStream(in);
										// 读取客户端数据
										int len = 0;
										byte[] buf = new byte[1024];
										//客户端不关闭,br.read(buf);会一直等待,所以必须
										//手动判断退出循环
										System.out.println("virtual times:"+(float)size/(float)buf.length);
										double times=Math.ceil((float)size / (float)buf.length);
										System.out.println("read times:"+times);
										for (int i = 0; i < times; i++) {
											len = br.read(buf);
											bos.write(buf, 0, len);
											bos.flush();
										}
										storepath = storepath + "."+extension ;
										/**Begin Author:wuqiwei Date:2014-08-15  AddReason:解决windows不能重名名*/
										String os = System.getProperty("os.name");
										/**Begin Author:wuqiqwi Date:2014-09-05 AddReason:windows 文件重命名无效,linux有效*/
										if(os.contains("Windows")){
											FileToolkit.reName(file,new File(storepath));
										}else {
											file.renameTo(new File(storepath));
										}	
										/**End Author:wuqiqwi Date:2014-09-05 AddReason:windows 文件重命名无效,linux有效*/
										/**End Author:wuqiwei Date:2014-08-15  AddReason:解决windows不能重名名*/
										storepath=storepath.replaceAll(basepath, pathPrefix);
										System.out.println("access path:"+storepath);
										out = socket.getOutputStream();
										DataOutputStream socketOut=new DataOutputStream(out);
										socketOut.writeUTF(storepath);
										bos.flush();
										bos.close();
										br.close();
										socketOut.close();
										out.close();
									}
									// 删除StoreServer中的数据
									else if(status.equals("remove")){
										boolean success=false;
										OutputStream out = socket.getOutputStream();
										DataOutputStream dataos = new DataOutputStream(out);
										// 读取到请求的url
										String url = datais.readUTF();
										Pattern regex = Pattern.compile("/[A-Z0-9]{2}/[A-Z0-9]{2}/[A-Za-z0-9-]+\\.[a-z]+");
										Matcher matcher = regex.matcher(url);
										if(matcher.find()){
											String storepath=basepath+matcher.group();
											System.out.println("file disk store path:"+storepath);
											File file=new File(storepath);
											if(file.exists())success= file.delete();
										}
										dataos.writeBoolean(success);
										dataos.flush();
										dataos.close();
									}
									
								} catch (IOException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}finally{
									try {
										socket.close();
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
