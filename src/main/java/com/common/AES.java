package com.common;

import java.util.Arrays;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 公開鍵暗号方式を用いた、文字列の暗号化と復号化を行う。
 * 
 * 使用法:
 * AES aes = new AES();
 * byte[] encrypted = aes.encrypt("てすと");
 * String original = aes.decrypt(encrypted);
 * @author ksusa
 *
 */
public class AES {

	// 公開鍵生成用文字列
	private static String public_key_str = "FHDIs284CSDGlseEwa";
	
	// 公開鍵
	private static byte[] public_key = null;
	
	//秘密鍵
	private static SecretKeySpec secret_key = null;

	// 暗号オブジェクト
	private static Cipher cipher_obj = null;
	
	// 暗号（バイナリ）
	private static byte[] encrypted = null;
	
	/**
	 * コンストラクタ
	 */
	public AES()
	{
		try
		{
			// 公開鍵作成
			public_key = (public_key_str).getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			public_key = sha.digest(public_key);
			public_key = Arrays.copyOf(public_key, 16); // use only first 128 bit
		
			//秘密鍵作成
			secret_key = new SecretKeySpec(public_key, "AES");
			
			// 暗号オブジェクト作成
			cipher_obj = Cipher.getInstance("AES");
		}
		catch(Exception ex)
		{
			// TODO ログ出力
			ex.printStackTrace();
		}
	}
	
	/**
	 * 引数の文字列の暗号化を行う
	 * @param secret_str
	 * @return 暗号（バイナリ）※DBに挿入するときは、binary型で保存する
	 */
	public byte[] encrypt(String secret_str)
	{
		try
		{		
			// 秘密鍵で暗号化
			cipher_obj.init(Cipher.ENCRYPT_MODE, secret_key);		
		
			// 暗号文作成
			encrypted = cipher_obj.doFinal((secret_str).getBytes());
		}
		catch(Exception ex)
		{
			// TODO ログ出力
			ex.printStackTrace();
		}
		return encrypted;
	}
	
	/**
	 * 暗号から復号化された文字列を取得
	 * @param encrypted
	 * @return
	 */
	public String decrypt(byte[] encrypted)
	{
		// 元の文字列
		String original_str = null;
		
		// 秘密鍵で複合化
	    try {
			cipher_obj.init(Cipher.DECRYPT_MODE, secret_key);
		    //　復号文
		    byte[] original = cipher_obj.doFinal(encrypted);
		    original_str = new String(original);
		} catch (Exception ex) {
			// TODO ログ出力
			ex.printStackTrace();
		}
	    
		return original_str;
	}
}
