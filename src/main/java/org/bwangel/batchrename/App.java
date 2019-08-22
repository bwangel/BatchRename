package org.bwangel.batchrename;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Hello world!
 *
 */
public class App {
	// 计算文件数量
	static int count = 0;

	static String keywords = "";
	static String replacement = "";

	static final String[] TXT_EXTS = new String[] { ".txt", ".java", ".properties", ".xml", ".config", ".iml", ".yml",
			".js", ".json" };

	public static void main(String[] args) {
		String path = args[0];
		keywords = args[1];
		replacement = args[2];

		File file = new File(path);
		if (!file.exists()) {
			System.out.println("文件或目录不存在！");
			return;
		}
		getFiles(new File[] { file });

		System.out.printf("共找到：%d个文件", count);
	}

	// 递归搜索文件
	public static void getFiles(File[] files) {
		for (File file : files) {
			count++;
			if (file.isDirectory()) {
				getFiles(file.listFiles());
			} else {
				for (String ext : TXT_EXTS) {
					if (file.getName().endsWith(ext)) {
						renameContent(file);
						break;
					}
				}
			}
			renameFile(file);
		}

	}

	private static void renameContent(File file) {
		try {
			StringBuilder sb = new StringBuilder();
			byte[] bytes = new byte[1024];
			FileInputStream fis = new FileInputStream(file);
			int len = 0;
			while ((len = fis.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, len));
			}
			fis.close();

			if (sb.indexOf(keywords) >= 0) {
				FileOutputStream fos = new FileOutputStream(file);
				String content = sb.toString();
				fos.write((content.replaceAll(keywords, replacement)).getBytes());
				fos.flush();
				System.out.printf("FileContent: %s => %s\n", content.substring(0, Math.min(50, content.length())),
						replacement);
				fos.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static File renameFile(File file) {
		String fileName = file.getName();
		File newFile = file;
		if (fileName.contains(keywords)) {
			String newName = fileName.replaceFirst(keywords, replacement);
			newFile = new File(
					file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('\\')) + '\\' + newName);
			file.renameTo(newFile);
			System.out.printf("FileName: %s => %s\n", file.getAbsolutePath(), newFile.getAbsolutePath());
		}
		return newFile;
	}

}
