package com.common;

public class Constant {

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
	public static final String ANKI_NOTE_ROOT_PATH = "C:\\Users\\ksusa\\Documents\\作業用\\ank\\";
	public static final String ANKI_NOTE_FOLDER_PASSWORD = "Xdsjiafr8FSfjIORFJSIOE+U";
	public static final String DB_FOLDER_PATH = "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\";
	public static final String H2DB_FOLDER_PATH = "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\h2db\\";
	public static final String SQLITE_FOLDER_PATH = "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\sqlite\\";
	public static final String SQLITE_LOG_FOLDER_PATH = "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\sqlite\\data\\log_db\\";
	public static final String SQLITE_USER_DB_FOLDEDR_PATH = "C:\\Users\\ksusa\\Documents\\作業用\\ank\\databases\\sqlite\\data\\user_db\\";
	*/
}
