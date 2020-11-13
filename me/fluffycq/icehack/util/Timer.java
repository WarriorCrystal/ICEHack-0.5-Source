package me.fluffycq.icehack.util;

public final class Timer {
  public boolean passed(double paramDouble) {
    return ((System.currentTimeMillis() - this.time) >= paramDouble);
  }
  
  public void setTime(long paramLong) {
    this.time = paramLong;
  }
  
  public long getTime() {
    return this.time;
  }
  
  public void resetTimeSkipTo(long paramLong) {
    this.time = System.currentTimeMillis() + paramLong;
  }
  
  public void reset() {
    this.time = System.currentTimeMillis();
  }
}
