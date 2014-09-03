package mydfs.storage.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
public class MydfsTrackerServer {
	private MydfsStorageServer mydfsStorageServer;
	private String host;
	private int port;

	public String getHost() {
		return host;
	}

	public MydfsStorageServer getMydfsStorageServer() {
		return mydfsStorageServer;
	}

	public void setMydfsStorageServer(MydfsStorageServer mydfsStorageServer) {
		this.mydfsStorageServer = mydfsStorageServer;
	}

	public MydfsTrackerServer() {
	}

	public MydfsTrackerServer(String host, int port) {
		super();
		this.host = host;
		this.port = port;
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

	/**
	 * 发送数据到服务端
	 * @param inputStream 上传的文件流
	 * @param fileSuffix  文件的后缀名
	 * */ 
	public String upload(final InputStream inputStream,String fileSuffix) {
		try {
			System.out.println("client:size:"+inputStream.available());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if("".equals(fileSuffix)||fileSuffix==null){
			throw new RuntimeException("第二个参数必须指定");
		}
		String storepath = new String();
		Socket socket;
		try {
			socket = new Socket(host, port);
			// 得到socket发送数据的输出流
			OutputStream out = socket.getOutputStream();
			DataOutputStream socketOut = new DataOutputStream(out);
			//通知服务器,将要上传数据
			socketOut.writeUTF("upload");
			//将文件的后缀发送个服务器
			socketOut.writeUTF(fileSuffix);
			int size=inputStream.available();
			socketOut.writeInt(size);
			final BufferedOutputStream bos = new BufferedOutputStream(out);
			try {
				byte[] buf = new byte[1024];
				int len = 0;
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				while ((len = bis.read(buf)) != -1) {
					bos.write(buf, 0, len);
					bos.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStream in = socket.getInputStream();
			DataInputStream socketIn=new DataInputStream(in);
			storepath=socketIn.readUTF();
			System.out.println("file store path:" + storepath);
			socket.close();
			bos.close();
			socketOut.close();
			socketIn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return storepath;
	}
	//获取数据
	public InputStream receiveData(String currenturl) {
		InputStream inputStream = null;
		try {
			Socket socket = receive(currenturl);
			inputStream = socket.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	private Socket receive(String currenturl) {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			// 得到socket发送数据的输出流
			OutputStream out = socket.getOutputStream();
			DataOutputStream ps = new DataOutputStream(out);
			ps.writeUTF("receive");
			ps.flush();
			ps.writeUTF(currenturl);
			ps.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return socket;
	}
	// 删除数据
	public boolean removeData(String url){
		Socket socket = null;
		Boolean success=false;
		try {
			socket = new Socket(host, port);
			// 得到socket发送数据的输出流
			OutputStream out = socket.getOutputStream();
			DataOutputStream ps = new DataOutputStream(out);
			ps.writeUTF("remove");
			ps.flush();
			ps.writeUTF(url);
			ps.flush();
			InputStream in = socket.getInputStream();
			DataInputStream dataIn = new DataInputStream(in);
			success=dataIn.readBoolean();
			return success;
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	
}
