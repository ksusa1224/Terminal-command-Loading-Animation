package com.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

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
	
	public String getDay(String dbdate)
	{
		if(dbdate == null)
		{
			return "";
		}
		String day = "";
		if (dbdate.length() > 6)
		{
			String[] timestamp = dbdate.split("-");
			day = timestamp[1] + "/" + timestamp[2].split(" ")[0];
		}
		return day;
	}
	
	/**
	 * 引数の文字列が日本語か日本語以外（現状英語しか対応していない）かを判定する
	 * @param text
	 * @return
	 */
	public static String check_japanese_or_english(String text)
	{
		if (text.matches("^.*[ぁ-ゞァ-タダ-ヶ一-龠].*"))
		{
			return Constant.JAPANESE;
		}
		else
		{
			// TODO 英語以外も判定できるようにする
			return Constant.ENGLISH;
		}
	}
	
	/**
	 * 引数の文字が何語かを判定する
	 * TODO　loadProfileの部分で実行時エラーとなる
	 * @param text
	 * @return
	 */
	public static String langDetect(String text) { 
		String language = "";
		try
		{
			String profileDirectory = "/usr/local/anki_note/langdetect/profiles/";

			// 言語プロファイルの読み込み
	        DetectorFactory.loadProfile(profileDirectory);

	        Detector detector = DetectorFactory.create();

	        // 判定対象のテキスト設定
	        detector.append(text);	
	        
	        language = detector.detect();
	        
	        // 判定結果(言語候補)を取得
	        ArrayList<Language> languages = detector.getProbabilities();
	        for (Language lang : languages) {
	            System.out.println(lang.lang + ":" + lang.prob);
	        }
	        
//			String profileDirectory = "/usr/local/anki_note/langdetect/profiles/";
//			DetectorFactory.loadProfile(new File(profileDirectory)); // SmProfile is also available
//			Detector detector = DetectorFactory.create();
//			detector.append(text);
//			language = detector.detect();
		}
		catch(Exception ex)
		{
			language = "";
			ex.printStackTrace();
		}
		return language;

		//		try 
//		  { 
//		   Detector detector; 
//		   detector = DetectorFactory.create(); 
//		   detector.append(text); 
//		   String lang = detector.detect(); 
//		   return lang;
//		  }
//		  catch (LangDetectException e) 
//		  { 
//		   return ""; 
//		  }
	}
	
	/**
	 * SQL内のシングルクォートをエスケープ（SQLインジェクション対策を兼ねる）
	 * TODO 動作不良
	 * ※パラメータ内に２つ以上シングルクォートがあった場合は非対応
	 * @param sql
	 * @return
	 */
	public static String sql_escape(String sql)
	{
		// パラメータ内のシングルクォートを探す
		String	regex = "'[^']*'[^']*'\\)|'[^']*'[^']*',";
		Pattern pattern = Pattern.compile(regex);	
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find())
		{
			String group = matcher.group();
			System.out.println("グループ:"+ group);
			group = group.substring(1, group.length()-2);
			String replacement = group.replace("'", "''");
			sql = sql.replace(group, replacement);
		}
		return sql;
	}
}
