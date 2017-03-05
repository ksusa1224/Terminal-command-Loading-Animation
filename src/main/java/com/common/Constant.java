package com.common;

import org.springframework.beans.factory.annotation.Value;

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
	
	// 課金タイプはプレムアムだが、支払いの滞りなどで支払われていない状態
	public static final String KAKIN_TYPE_PREMIUM_CHARGE_FAILS = "04";
	
	/**
	 * オーナータイプ
	 */
	
	// ジェネラル
	public static final String OWNER_TYPE_GENERAL = "General Owner ( ¥0 )";
	
	// プレミアム
	public static final String OWNER_TYPE_PREMIUM = "Premium Owner ( ¥500 / 月 )";
	
	// フリープレミアム
	public static final String OWNER_TYPE_FREE_PREMIUM = "Premium Owner ( ¥0 )";
	
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
	
	// 金ノート
	public static final String SAMPLE_DB = "2017_02_14_18_57_26_sample.db";
	
	// 本番
//	public static final String SAMPLE_DB = "2017_01_21_11_02_55_sample.db";
		
//	// TODO 本番ではパスを変える
//	public static final String SPEECH_DATA_FOLDER_PATH
//		= "/Users/ksusa/Documents/anki_note_workspace/anki_note/src/main/resources/static/speech/";
//	public static final String SPEECH_DATA_FOLDER_PATH
//	= "/Users/slimebook/Documents/workspace/anki_note/src/main/resources/static/speech/";

	// 本番
	public static final String SPEECH_DATA_FOLDER_PATH
	= "/usr/local/anki_note/application/apache-tomcat-8.0/webapps/ROOT/WEB-INF/classes/static/speech/";
	
	// 本番 一時フォルダ
	public static final String SPEECH_DATA_TEMP_FOLDER_PATH
	= "/usr/local/anki_note/application/apache-tomcat-8.0/webapps/speech";
	
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
	public static final int QA_TYPE_1_ON_1 = 1;
	
	// 問題１に対して正答複数
	public static final int QA_TYPE_1_ON_N = 2;
	
	// 問題パーツ複数に対して正答１
	public static final int QA_TYPE_N_ON_1 = 3;
	
	// 問題パーツ複数に対して正答複数
	public static final int QA_TYPE_N_ON_N = 4;	
	
	/**
	 * 回答アクション
	 */
	public static final String ACTION_QA_TOUROKU = "問題を新規作成した";
	public static final String ACTION_IMPORT_FREE_QA = "無料の問題をインポートした";
	public static final String ACTION_IMPORT_PAYED_QA = "有料の問題をインポートした";
	public static final String ACTION_CHANGE_RED_CLICK = "覚えたのでクリックで赤くした";
	public static final String ACTION_CHANGE_WHITE_CLICK = "忘れたのでクリックして白くした";
	public static final String ACTION_CHANGE_ALL_WHITE_BY_TAG = "そのタグの全問を白くした";
	public static final String ACTION_SEIKAI_INPUT = "入力して解いて正解した";
	public static final String ACTION_HUSEIKAI_INPUT = "入力して解いて不正解した";
	
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
	 * ソート順
	 */
	public static final String SORT_NEWEST = "新着順";
	public static final String SORT_REGIETER = "登録順";
	public static final String SORT_RANDOM = "ランダム順";
	
	/**
	 * その他
	 */
	// DB(SQLite、H2共通)の日付型のフォーマット
	public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// ログDB用の日付型のフォーマット
	public static final String LOG_DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	// FATALエラー
	public static final String ERROR_TYPE_FATAL = "FATAL";

	// ERRORエラー
	public static final String ERROR_TYPE_ERROR = "ERROR";
	
	public static final int QA_NUM_PER_PAGE = 100;
}
