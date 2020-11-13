package me.fluffycq.icehack.util;

public class StopWatch {
  public boolean hasCompleted(long paramLong) {
    return (getCurrentMS() - this.previousMS >= paramLong);
  }
  
  public long getPreviousMS() {
    return this.previousMS;
  }
  
  public void reset() {
    this.previousMS = getCurrentMS();
  }
  
  public long getCurrentMS() {
    return System.nanoTime() / 1000000L;
  }
  
  public StopWatch() {
    reset();
  }
}
