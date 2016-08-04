package com.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StopWatch { 

	  private long startTime = 0;
	  private long stopTime = 0;

	  public void start() {
	    startTime = System.currentTimeMillis();
	  }

	  public void stop() {
	    stopTime = System.currentTimeMillis();
	  }

	  //elaspsed time in milliseconds
	  public long getElapsedTime() {
		  return stopTime - startTime;
	  }

	  //elaspsed time in seconds
	  public double getElapsedTimeSecs() {
	    double elapsed;
	      elapsed = (double)(stopTime - startTime) / 1000;
	    return elapsed;
	  }
} 