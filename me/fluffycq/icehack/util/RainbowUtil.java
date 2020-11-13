package me.fluffycq.icehack.util;

import java.util.ArrayList;

public class RainbowUtil {
  public RainbowUtil(int paramInt) {
    this.m_Timer = paramInt;
    for (byte b = 0; b < 'Ũ'; b++) {
      this.RainbowArrayList.add(Integer.valueOf(ColorUtil.GetRainbowColor(b, 90.0F, 50.0F, 1.0F).getRGB()));
      this.CurrentRainbowIndexes.add(Integer.valueOf(b));
    } 
  }
  
  public int GetRainbowColorAt(int paramInt) {
    if (paramInt > this.CurrentRainbowIndexes.size() - 1)
      paramInt = this.CurrentRainbowIndexes.size() - 1; 
    return ((Integer)this.RainbowArrayList.get(((Integer)this.CurrentRainbowIndexes.get(paramInt)).intValue())).intValue();
  }
  
  public void OnRender() {
    if (this.RainbowSpeed.passed(this.m_Timer)) {
      this.RainbowSpeed.reset();
      MoveListToNextColor();
    } 
  }
  
  public int getTimer() {
    return this.m_Timer;
  }
  
  public void SetTimer(int paramInt) {
    this.m_Timer = paramInt;
  }
  
  private void MoveListToNextColor() {
    if (this.CurrentRainbowIndexes.isEmpty())
      return; 
    this.CurrentRainbowIndexes.remove(this.CurrentRainbowIndexes.get(0));
    int i = ((Integer)this.CurrentRainbowIndexes.get(this.CurrentRainbowIndexes.size() - 1)).intValue() + 1;
    if (i >= this.RainbowArrayList.size() - 1)
      i = 0; 
    this.CurrentRainbowIndexes.add(Integer.valueOf(i));
  }
}
