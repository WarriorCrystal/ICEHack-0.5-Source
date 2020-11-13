package me.fluffycq.icehack.setting;

import java.util.ArrayList;
import me.fluffycq.icehack.module.Module;

public class Setting {
  public Setting(String paramString, Module paramModule, int paramInt) {
    this.name = paramString;
    this.parent = paramModule;
    this.keyCode = paramInt;
    this.mode = "Bind";
    paramModule.addSetting(this);
  }
  
  public boolean isBind() {
    return this.mode.equalsIgnoreCase("Bind");
  }
  
  public double getMin() {
    return this.min;
  }
  
  public void setValBoolean(boolean paramBoolean) {
    this.bval = paramBoolean;
  }
  
  public boolean isCombo() {
    return this.mode.equalsIgnoreCase("Combo");
  }
  
  public Setting(String paramString1, Module paramModule, String paramString2, ArrayList<String> paramArrayList) {
    this.name = paramString1;
    this.parent = paramModule;
    this.sval = paramString2;
    this.options = paramArrayList;
    this.mode = "Combo";
    paramModule.addSetting(this);
  }
  
  public double getMax() {
    return this.max;
  }
  
  public void setValString(String paramString) {
    this.sval = paramString;
  }
  
  public boolean onlyInt() {
    return this.onlyint;
  }
  
  public boolean getValBoolean() {
    return this.bval;
  }
  
  public boolean isSlider() {
    return this.mode.equalsIgnoreCase("Slider");
  }
  
  public void setValDouble(double paramDouble) {
    this.dval = paramDouble;
  }
  
  public boolean isCheck() {
    return this.mode.equalsIgnoreCase("Check");
  }
  
  public int getKeyBind() {
    return this.keyCode;
  }
  
  public ArrayList<String> getOptions() {
    return this.options;
  }
  
  public Module getParentMod() {
    return this.parent;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Setting(String paramString, Module paramModule, double paramDouble1, double paramDouble2, double paramDouble3, boolean paramBoolean) {
    this.name = paramString;
    this.parent = paramModule;
    this.dval = paramDouble1;
    this.min = paramDouble2;
    this.max = paramDouble3;
    this.onlyint = paramBoolean;
    this.mode = "Slider";
    paramModule.addSetting(this);
  }
  
  public void setValKey(int paramInt) {
    this.keyCode = paramInt;
  }
  
  public Setting(String paramString, Module paramModule, boolean paramBoolean) {
    this.name = paramString;
    this.parent = paramModule;
    this.bval = paramBoolean;
    this.mode = "Check";
    paramModule.addSetting(this);
  }
  
  public double getValDouble() {
    if (this.onlyint)
      this.dval = (int)this.dval; 
    return this.dval;
  }
  
  public String getValString() {
    return this.sval;
  }
}
