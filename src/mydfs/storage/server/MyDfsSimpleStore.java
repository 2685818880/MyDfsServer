package mydfs.storage.server;

public class MyDfsSimpleStore {
	public MyDfsSimpleStore(String basepath) {
		Folder.initFolder(basepath);
	}
}
