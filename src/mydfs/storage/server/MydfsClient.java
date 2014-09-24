package mydfs.storage.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/*图片或视频数据输出*/
public class MydfsClient{
	
	public static void fushImage(String url,String httpQueryString,OutputStream outputStream, MydfsTrackerServer storageTracker){
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
