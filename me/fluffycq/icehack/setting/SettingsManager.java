package me.fluffycq.icehack.setting;

import java.util.ArrayList;
import me.fluffycq.icehack.module.Module;

public class SettingsManager {
  public ArrayList<Setting> getSettings() {
    return this.settings;
  }
  
  public void rSetting(Setting paramSetting) {
    this.settings.add(paramSetting);
  }
  
  public ArrayList<Setting> getSettingsByMod(Module paramModule) {
    ArrayList<Setting> arrayList = new ArrayList();
    for (Setting setting : getSettings()) {
      if (setting.getParentMod().equals(paramModule))
        arrayList.add(setting); 
    } 
    return arrayList.isEmpty() ? null : arrayList;
  }
  
  public Setting getSettingByMod(String paramString, Module paramModule) {
    for (Setting setting : getSettings()) {
      if (setting.getName().equalsIgnoreCase(paramString) && setting.getParentMod() == paramModule)
        return setting; 
    } 
    return null;
  }
  
  public Setting getSetting(String paramString1, String paramString2) {
    for (Setting setting : getSettings()) {
      if (setting.getName().equalsIgnoreCase(paramString2) && setting.getParentMod().getName().equalsIgnoreCase(paramString2))
        return setting; 
    } 
    return null;
  }
  
  public Setting getSettingByName(String paramString) {
    for (Setting setting : getSettings()) {
      if (setting.getName().equalsIgnoreCase(paramString))
        return setting; 
    } 
    System.err.println(String.valueOf((new StringBuilder()).append("[icehack] Error Setting NOT found: '").append(paramString).append("'!")));
    return null;
  }
}
