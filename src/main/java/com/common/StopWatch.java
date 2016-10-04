package com.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StopWatch { 

	  private long startTime = 0;
	  private long stopTime = 0;

	  public StopWatch()
	  {
		    startTime = System.currentTimeMillis();
	  }
	  
	  public void start() {
	    startTime = System.currentTimeMillis();
	  }

	  public void stop() {
	    stopTime = System.currentTimeMillis();
	    System.out.println("StopWatch: " + getElapsedTime() + " milliseconds.");
	    System.out.println("StopWatch: " + getElapsedTimeSecs() + " seconds.");
	  }

	  /**
	   * メソッド名などを同時に出力したい時用
	   * @param process_name　処理名
	   */
	  public void stop(String process_name) {
		    stopTime = System.currentTimeMillis();
		    System.out.println(process_name + " StopWatch: " + getElapsedTime() + " milliseconds.");
		    System.out.println(process_name + " StopWatch: " + getElapsedTimeSecs() + " seconds.");
	  }	  
	  
	  //elaspsed time in milliseconds
	  public long getElapsedTime() {
		  return stopTime - startTime;
	  }

	  //elaspsed time in seconds
	  public double getElapsedTimeSecs() {
	    double elapsed;
	      elapsed = ((double)(stopTime - startTime)) / 1000;
	    return elapsed;
	  }
} 