package java_file;

import java.io.File;
import java.io.IOException;

public class CreateMkdirAuto {
	
	
	
	public static void main(String[] args) throws IOException {
		String path = "D:\\mkdir_test\\aa\\bb\\cc";
		String fileName = "hehe.txt";
		
		File fileDir = new File(path);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		File file = new File(path,fileName);
		if(!file.exists()){
			file.createNewFile();
		}
	}

}
