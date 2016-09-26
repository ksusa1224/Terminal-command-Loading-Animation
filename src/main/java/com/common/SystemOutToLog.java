package com.common;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemOutToLog extends PrintStream
{
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
    
    public SystemOutToLog(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    // 現在日時をフォーマットして文字列で返す
    private String getDateTime(){
        return FORMATTER.format( new Date() );
    }
    
    
    // それぞれ、引数に getDateTime() を加えることで全出力に日時が記録される。
    @Override public void print(boolean b) { super.print( getDateTime()+b); }
    @Override public void print(char c) { super.print( getDateTime()+c); }
    @Override public void print(char[] s) { super.print( getDateTime()+String.valueOf(s)); }
    @Override public void print(double d) { super.print(getDateTime()+d); }
    @Override public void print(float f) { super.print(getDateTime()+f); }
    @Override public void print(int i) { super.print(getDateTime()+i); }
    @Override public void print(long l) { super.print(getDateTime()+l); }
    @Override public void print(String s) { super.print(getDateTime()+s); }
    @Override public void print(Object obj) { super.print(getDateTime() + String.valueOf(obj)); }	

    @Override public void println(boolean b) { super.print( getDateTime()+b); }
    @Override public void println(char c) { super.print( getDateTime()+c); }
    @Override public void println(char[] s) { super.print( getDateTime()+String.valueOf(s)); }
    @Override public void println(double d) { super.print(getDateTime()+d); }
    @Override public void println(float f) { super.print(getDateTime()+f); }
    @Override public void println(int i) { super.print(getDateTime()+i); }
    @Override public void println(long l) { super.print(getDateTime()+l); }
    @Override public void println(String s) { super.print(getDateTime()+s); }
    @Override public void println(Object obj) { super.print(getDateTime() + String.valueOf(obj)); }	
}
