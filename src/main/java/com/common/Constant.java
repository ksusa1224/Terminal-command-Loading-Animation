package com.common;

public class Constant {

	/**
	 * DB関連
	 */
	
	// 現在のオーナーDBバージョン
	public static final String OWNER_DB_CURRENT_VERSION = "version001";
	
	// Common DB 接続先URLの頭
	public static final String COMMON_DB_ROOT_URL = "jdbc:h2:tcp://localhost/";
	
	// Common DB DB名
	public static final String COMMON_DB_NAME = "common";
	
	// Common DB Admin ユーザ
	public static final String COMMON_DB_ADMIN_USER = "common_admin";
	
	// Common DB 接続パスワード
	public static final String COMMON_DB_PASSWORD = "DSFRsD0eSD2Sh5#8";

	/**
	 * 課金タイプ
	 */
	
	// 課金タイプ：無料（general owner）
	public static final String KAKIN_TYPE_FREE = "01";
	
	// 課金タイプ：プレミアム
	public static final String KAKIN_TYPE_PREMIUM = "02";
	
	// 課金タイプ：無料かつプレミアム・・・友人知人等に、無料で有料会員と同じ機能を使ってもらう用
	public static final String KAKIN_TYPE_FREE_PREIMIUM = "03";
	
	/**
	 * 暗記ノート関連のディレクトリのパス
	 */
	// MAC
	public static final String ANKI_NOTE_ROOT_PATH 
		= "/usr/local/anki_note/";

	public static final String DB_FOLDER_PATH 
		= "/usr/local/anki_note/databases/";
	
	public static final String H2DB_FOLDER_PATH 
		= "/usr/local/anki_note/databases/h2db/data/";
	
	public static final String SQLITE_FOLDER_PATH 
		= "/usr/local/anki_note/databases/sqlite/data/";
	
	public static final String SQLITE_LOG_FOLDER_PATH 
		= SQLITE_FOLDER_PATH + "log_db" + "/";
	
	public static final String SQLITE_OWNER_DB_FOLDEDR_PATH 
		= SQLITE_FOLDER_PATH + "owner_db" + "/" + OWNER_DB_CURRENT_VERSION;

	// WINDOWS
	/* 
	public static final String ANKI_NOTE_ROOT_PATH 
		= "C:\\Users\\ksusa\\Documents\\作業用\\ank\\";

	public static final String DB_FOLDER_PATH 
		= "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\";
	
	public static final String H2DB_FOLDER_PATH 
		= "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\h2db\\data\\";
	
	public static final String SQLITE_FOLDER_PATH 
		= "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\sqlite\\data\\";
	
	public static final String SQLITE_LOG_FOLDER_PATH 
		= SQLITE_FOLDER_PATH + "log_db" + "\\";
	
	public static final String SQLITE_OWNER_DB_FOLDEDR_PATH 
		= SQLITE_FOLDER_PATH + "owner_db" + "\\" + OWNER_DB_CURRENT_VERSION;	
	*/
	
	/**
	 * QAタイプ
	 */
	// 問題１に対して正答１
	public static final int QA_TYPE_1_ON_1 = 0;
	
	// 問題１に対して正答複数
	public static final int QA_TYPE_1_ON_N = 1;
	
	// 問題パーツ複数に対して正答１
	public static final int QA_TYPE_N_ON_1 = 2;
	
	// 問題パーツ複数に対して正答複数
	public static final int QA_TYPE_N_ON_N = 3;	
	
	/**
	 * 公開範囲
	 */
	// 自分のみ
	public static final int KOUKAI_LEVEL_SELF_ONLY = 0;
	
	// 友人のみ
	public static final int KOUKAI_LEVEL_FRIENDS = 1;
	
	// ログインしてるオーナー全体
	public static final int KOUKAI_LEVEL_ALL_OWNERS = 2;
	
	// プレミアムオーナーのみ
	public static final int KOUKAI_LEVEL_PREMIUM_OWNERS = 3;
	
	// 外部公開
	public static final int KOUKAI_LEVEL_EXTERNAL = 4;
	
	/**
	 * 問題、正答の言語
	 */
	// 日本語
	public static final String JAPANESE = "日本語";
	// 英語
	public static final String ENGLISH = "英語";
	
	/**
	 * その他
	 */
	// DB(SQLite、H2共通)の日付型のフォーマット
	public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
