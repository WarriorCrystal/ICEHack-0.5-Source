package me.fluffycq.icehack.events;

public class PlayerMoveEvent extends ICEEvent {
  public double getX() {
    return this.x;
  }
  
  public void setZ(double paramDouble) {
    this.z = paramDouble;
  }
  
  public double getZ() {
    return this.z;
  }
  
  public void setY(double paramDouble) {
    this.y = paramDouble;
  }
  
  public void setX(double paramDouble) {
    this.x = paramDouble;
  }
  
  public PlayerMoveEvent(double paramDouble1, double paramDouble2, double paramDouble3, boolean paramBoolean) {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
    this.onGround = paramBoolean;
  }
  
  public double getY() {
    return this.y;
  }
}
