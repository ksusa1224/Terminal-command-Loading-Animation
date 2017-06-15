package com.common;

/**
 * StringBuilderにC#のAppendLineを追加したUtility
 * @author ksusa
 */
public class StringBuilderPlus {

	private static StringBuilder sb;

	public StringBuilderPlus(){
		 sb = new StringBuilder();
	}

	/**
	 * 改行なしで行追加（既存のStringBuilder.appendと同じ）
	 * @param str
	 */
	public void append(String str)
	{
		sb.append(str != null ? str : "");
	}
	
	/**
	 * 改行付きで行追加
	 * @param str
	 */
	public void appendLine(String str)
	{
		sb.append(str != null ? str : "").append(System.getProperty("line.separator"));
	}
	
	public String toString()
	{
		return sb.toString();
	}
}