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
import java.net.Socket;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreServer {
	// 将数据输出到客户端
	public static void sendToClient(final Socket socket, String url,
			String basepath) {
		System.out.println("access url:" + url);
		// 有参数的url正则表达式
		url = FileToolkit.removeHost(url);
		File file = null;
		try {
			OutputStream outputStream = socket.getOutputStream();
			// 客户端根据参数获取对应的缩略图片
			if (url.matches("/[A-Z0-9]{2}/[A-Z0-9]{2}/[A-Za-z0-9-]+\\.[a-zA-Z]+(\\?w=[0-9]+&h=[0-9]+){1}")) {
				String storepath_parameter = basepath + url;
				System.out.println("storepath:" + storepath_parameter);
				String heigth = FileToolkit.getHeigth(storepath_parameter);
				String width = FileToolkit.getWidth(storepath_parameter);
				// 获取缩略图路径
				String thumbnailPath = FileToolkit.thumbnailPath(
						storepath_parameter, width, heigth);
				// 判断该文件是否可以被压缩(只有图片可以被压缩生成缩略图)
				if (FileToolkit.isCanThumbnail(thumbnailPath)) {
					// 如果这个缩略图不是图片格式
					file = new File(thumbnailPath);
					// 如果没有缩率图文件,
					if (!file.exists()) {
						String storepath = storepath_parameter.replaceAll("\\?w=[0-9]+&h=[0-9]+", "");
						file = new File(storepath);
						// 如果原文件存在,生成缩略图
						if (file.exists()) {
							ScaleImage scaleImage = new ScaleImage();
							scaleImage.thumbnail(storepath, thumbnailPath,
									Integer.valueOf(width),
									Integer.valueOf(heigth),
									FileToolkit.getExtensionName(storepath));
							file = new File(thumbnailPath);
							System.out.println("thumbnailPath:" + thumbnailPath);
							// 文件不存在返回默认的图片
						} else {
							file = FileToolkit.diggingFile(basepath+ "the-file-is-not-exist.jpg");
						}
					}
				} else {
					file = FileToolkit.diggingFile(basepath+ "the-file-is-not-exist.jpg");
				}
				// 客户端获取原图
			} else if (url.matches("/[A-Z0-9]{2}/[A-Z0-9]{2}/[A-Za-z0-9-]+\\.[a-zA-Z]+")) {
				String storepath = basepath + url;
				System.out.println("storepath:" + storepath);
				file = FileToolkit.diggingFile(storepath);
				// 如果所有正则都不匹配返回一张默认的图片
			} else {
				file = FileToolkit.diggingFile(basepath+ "the-file-is-not-exist.jpg");
			}
			FileToolkit.flushImage(url, outputStream, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void clientUpload(final Socket socket,InputStream inputStream, DataInputStream datais,String extension,String basepath,String pathPrefix){
		System.out.println("client file stuffix: " + extension);
		String uuid = UUID.randomUUID().toString().toUpperCase();
		// 得到存储路径
		String storepath = Folder.getStoragePath(uuid);
		// 文件存储路径
		File file = new File(storepath);
		DataOutputStream dataOutputStream=null;
		BufferedInputStream bufferedInputStream=null;
		try {
			FileOutputStream fileOutputStream=new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			// 获取文件流的大小
			int size = datais.readInt();
			System.out.println("server file size:" + size);
			// InputStreamReader将字节流转化为字符流
			bufferedInputStream = new BufferedInputStream(inputStream);
			// 读取客户端数据
			int len = 0;
			byte[] buf = new byte[1024];
			// 客户端不关闭,br.read(buf);会一直等待,所以必须
			// 手动判断退出循环
			System.out.println("virtual times:" + (float) size/ (float) buf.length);
			double times = Math.ceil((float) size / (float) buf.length);
			System.out.println("read times:" + times);
			for (int i = 0; i < times; i++) {
				len = bufferedInputStream.read(buf);
				bufferedOutputStream.write(buf, 0, len);
				bufferedOutputStream.flush();
			}
			
			bufferedOutputStream.close();
			fileOutputStream.close();
			storepath = storepath + "." + extension;
			// 文件重命名需把该文件有关的流关闭
			file.renameTo(new File(storepath));
			storepath = storepath.replaceAll(basepath, pathPrefix);
			System.out.println("access path:" + storepath);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF(storepath);
			dataOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(dataOutputStream!=null){
					dataOutputStream.close();
				}
				if(socket!=null){
					socket.close();
				}
				if(bufferedInputStream!=null){
					bufferedInputStream.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void clientRemove(final Socket socket,DataInputStream datais,String basepath) {
		boolean success=false;
		DataOutputStream dataOutputStream=null;
		OutputStream outputStream=null;
		try {
			outputStream=socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
			// 读取到请求的url
			String url = datais.readUTF();
			Pattern regex = Pattern.compile("/[A-Z0-9]{2}/[A-Z0-9]{2}/[A-Za-z0-9-]+\\.[a-z]+");
			Matcher matcher = regex.matcher(url);
			if (matcher.find()) {
				String storepath = basepath + matcher.group();
				System.out.println("file disk store path:" + storepath);
				File file = new File(storepath);
				if (file.exists())
					success = file.delete();
			}
			dataOutputStream.writeBoolean(success);
			dataOutputStream.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(dataOutputStream!=null){
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream!=null){
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
