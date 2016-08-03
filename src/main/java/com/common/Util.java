package com.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author ksusa
 *
 */
public class Util {
	
	/**
	 * フォルダまたはファイルのpermissionを全て禁止する
	 * @param path
	 */
	public static void lock_folder_or_file(String path) {
		File target_folder_or_file = new File(path);
		target_folder_or_file.setExecutable(false);
		target_folder_or_file.setWritable(false);
		target_folder_or_file.setReadable(false);
		target_folder_or_file.isDirectory();
		
		// unlock
//		f1.setExecutable(true);
//		f1.setWritable(true);
//		f1.setReadable(true);
		
//		FileInputStream in;
//		try 
//		{
//			in = new FileInputStream("");
//			// ファイルロック
//		    java.nio.channels.FileLock lock = in.getChannel().lock();
//		    try 
//		    {
//		    } 
//		    finally 
//		    {
//		    	// ファイルロック解除
//		        lock.release();
//		    }
//		    in.close();
//		} 
//		catch (FileNotFoundException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally 
//		{
//		}
	}	

	
	/**
	 * フォルダまたはファイルのpermissionを全て許可する
	 * @param path
	 */
	public static void unlock_folder_or_file(String path) {
		File target_folder_or_file = new File(path);
		target_folder_or_file.setExecutable(true);
		target_folder_or_file.setWritable(true);
		target_folder_or_file.setReadable(true);
	}	
	
	/**
	 * 現在日時を指定したフォーマットで取得する
	 * @param format
	 * @return
	 */
	public static String getNow(String format)
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;	
	}	
}
