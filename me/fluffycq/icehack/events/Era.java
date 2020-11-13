package me.fluffycq.icehack.events;

public enum Era {
  POST, PERI, PRE;
  
  static {
    PERI = new Era("PERI", 1);
    POST = new Era("POST", 2);
    $VALUES = new Era[] { PRE, PERI, POST };
  }
}
