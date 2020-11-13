package org.spongepowered.asm.mixin.injection.callback;

public enum LocalCapture {
  NO_CAPTURE(false, false),
  PRINT(false, true),
  CAPTURE_FAILSOFT,
  CAPTURE_FAILHARD,
  CAPTURE_FAILEXCEPTION;
  
  private final boolean captureLocals;
  
  private final boolean printLocals;
  
  LocalCapture(boolean paramBoolean1, boolean paramBoolean2) {
    this.captureLocals = paramBoolean1;
    this.printLocals = paramBoolean2;
  }
  
  boolean isCaptureLocals() {
    return this.captureLocals;
  }
  
  boolean isPrintLocals() {
    return this.printLocals;
  }
}
