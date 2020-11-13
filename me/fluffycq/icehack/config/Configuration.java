//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.element.Panel;
import me.fluffycq.icehack.hudeditor.frame.Frame;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.ModuleManager;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.setting.SettingsManager;
import net.minecraft.client.Minecraft;

public class Configuration {
  public boolean checkPanelDir() {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("gui").append(File.separator)));
    return file.exists();
  }
  
  public void loadFrames() {
    if (ICEHack.hudeditor.frames != null)
      for (Frame frame : ICEHack.hudeditor.frames) {
        try (BufferedReader null = new BufferedReader(new FileReader(new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("frames").append(File.separator).append(frame.title).append(".txt")))))) {
          String str;
          while ((str = bufferedReader.readLine()) != null) {
            if (!str.contains(":"))
              continue; 
            if (str.split(":")[0].equalsIgnoreCase("x"))
              frame.x = Integer.valueOf(str.split(":")[1]).intValue(); 
            if (str.split(":")[0].equalsIgnoreCase("y"))
              frame.y = Integer.valueOf(str.split(":")[1]).intValue(); 
            if (str.split(":")[0].equalsIgnoreCase("extended"))
              frame.extended = Boolean.valueOf(str.split(":")[1]).booleanValue(); 
          } 
        } catch (FileNotFoundException fileNotFoundException) {
          fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
          iOException.printStackTrace();
        } 
      }  
  }
  
  public void saveFrame(Frame paramFrame) {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("frames").append(File.separator).append(paramFrame.title).append(".txt")));
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(file));
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    if (bufferedWriter != null) {
      try {
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("x:").append(String.valueOf(paramFrame.x)).append("\r\n")));
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("y:").append(String.valueOf(paramFrame.y)).append("\r\n")));
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("extended:").append(String.valueOf(paramFrame.extended)).append("\r\n")));
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
      try {
        bufferedWriter.close();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
    } 
  }
  
  public void loadSettings(Module paramModule) {
    if (existingSettings(paramModule))
      try (BufferedReader null = new BufferedReader(new FileReader(new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("settings").append(File.separator).append(paramModule.name).append(".txt")))))) {
        String str;
        while ((str = bufferedReader.readLine()) != null) {
          if (!str.contains(":"))
            continue; 
          if (str.split(":")[0].equalsIgnoreCase("module"))
            paramModule.setState(Boolean.valueOf(str.split(":")[2]).booleanValue()); 
          for (Setting setting : ICEHack.setmgr.getSettingsByMod(paramModule)) {
            if (setting.getName().equalsIgnoreCase(str.split(":")[1].split(":")[0])) {
              if (setting.isCheck())
                setting.setValBoolean(Boolean.valueOf(str.split(":")[2]).booleanValue()); 
              if (setting.isSlider())
                if (setting.onlyInt()) {
                  setting.setValDouble(Double.valueOf(str.split(":")[2]).doubleValue());
                } else if (!setting.onlyInt()) {
                  setting.setValDouble(Double.valueOf(str.split(":")[2]).doubleValue());
                }  
              if (setting.isCombo())
                setting.setValString(str.split(":")[2]); 
              if (setting.isBind())
                setting.getParentMod().setKey(Integer.valueOf(str.split(":")[2]).intValue()); 
            } 
          } 
        } 
      } catch (FileNotFoundException fileNotFoundException) {
        fileNotFoundException.printStackTrace();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      }  
  }
  
  public void loadPanels() {
    if (ICEHack.clickgui.panels != null)
      for (Panel panel : ICEHack.clickgui.panels) {
        try (BufferedReader null = new BufferedReader(new FileReader(new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("gui").append(File.separator).append(panel.title).append(".txt")))))) {
          String str;
          while ((str = bufferedReader.readLine()) != null) {
            if (!str.contains(":"))
              continue; 
            if (str.split(":")[0].equalsIgnoreCase("x"))
              panel.x = Integer.valueOf(str.split(":")[1]).intValue(); 
            if (str.split(":")[0].equalsIgnoreCase("y"))
              panel.y = Integer.valueOf(str.split(":")[1]).intValue(); 
            if (str.split(":")[0].equalsIgnoreCase("extended"))
              panel.extended = Boolean.valueOf(str.split(":")[1]).booleanValue(); 
          } 
        } catch (FileNotFoundException fileNotFoundException) {
          fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
          iOException.printStackTrace();
        } 
        panel.addModules();
      }  
  }
  
  static {
  
  }
  
  public void makeSettings(Module paramModule, File paramFile) {
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(paramFile));
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    if (bufferedWriter != null) {
      try {
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("module:enabled:").append(paramModule.getState() ? "true" : "false").append("\r\n")));
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
      for (Setting setting : ICEHack.setmgr.getSettingsByMod(paramModule)) {
        if (setting.isCheck())
          try {
            bufferedWriter.write(String.valueOf((new StringBuilder()).append("bool:").append(setting.getName()).append(":").append(setting.getValBoolean() ? "true" : "false").append("\r\n")));
          } catch (IOException iOException) {
            iOException.printStackTrace();
          }  
        if (setting.isSlider())
          try {
            if (setting.onlyInt()) {
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("sliderint:").append(setting.getName()).append(":").append(String.valueOf(setting.getValDouble())).append("\r\n")));
            } else if (!setting.onlyInt()) {
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("sliderdouble:").append(setting.getName()).append(":").append(String.valueOf(setting.getValDouble())).append("\r\n")));
            } 
          } catch (IOException iOException) {
            iOException.printStackTrace();
          }  
        if (setting.isCombo() && setting.getOptions() != null)
          try {
            bufferedWriter.write(String.valueOf((new StringBuilder()).append("combo:").append(setting.getName()).append(":").append(setting.getValString()).append("\r\n")));
          } catch (IOException iOException) {
            iOException.printStackTrace();
          }  
        if (setting.isBind())
          try {
            bufferedWriter.write(String.valueOf((new StringBuilder()).append("key:Bind:").append(String.valueOf(setting.getParentMod().getKey())).append("\r\n")));
          } catch (IOException iOException) {
            iOException.printStackTrace();
          }  
      } 
    } else {
      System.out.println(String.valueOf((new StringBuilder()).append("[icehack] Could not write Settings for Module ").append(paramModule.name)));
    } 
    try {
      bufferedWriter.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public boolean existingSettings(Module paramModule) {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("settings").append(File.separator).append(paramModule.name).append(".txt")));
    return file.exists();
  }
  
  public boolean checkModuleListDir() {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("arraylist").append(File.separator)));
    return file.exists();
  }
  
  public void saveFrames() {
    if (ICEHack.hudeditor.frames != null)
      for (Frame frame : ICEHack.hudeditor.frames) {
        File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("frames").append(File.separator).append(frame.title).append(".txt")));
        PrintWriter printWriter = null;
        try {
          printWriter = new PrintWriter(file.getAbsolutePath());
        } catch (FileNotFoundException fileNotFoundException) {
          fileNotFoundException.printStackTrace();
        } 
        if (printWriter != null) {
          printWriter.print("");
          printWriter.close();
        } 
        if (file.exists())
          saveFrame(frame); 
      }  
  }
  
  public void saveSettings(ModuleManager paramModuleManager, SettingsManager paramSettingsManager) {
    if (paramModuleManager.moduleList != null)
      for (Module module : paramModuleManager.moduleList) {
        File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("settings").append(File.separator).append(module.name).append(".txt")));
        PrintWriter printWriter = null;
        try {
          printWriter = new PrintWriter(file.getAbsolutePath());
        } catch (FileNotFoundException fileNotFoundException) {
          fileNotFoundException.printStackTrace();
        } 
        if (printWriter != null) {
          printWriter.print("");
          printWriter.close();
        } 
        if (file.exists() && paramSettingsManager.getSettingsByMod(module) != null)
          makeSettings(module, file); 
      }  
  }
  
  public void makeSetting(Setting paramSetting, File paramFile) {
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(paramFile, true));
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    if (bufferedWriter != null) {
      if (paramSetting.isCheck())
        try {
          bufferedWriter.append(String.valueOf((new StringBuilder()).append("bool:").append(paramSetting.getName()).append(":").append(paramSetting.getValBoolean() ? "true" : "false").append("\r\n")));
        } catch (IOException iOException) {
          iOException.printStackTrace();
        }  
      if (paramSetting.isSlider())
        try {
          if (paramSetting.onlyInt()) {
            bufferedWriter.append(String.valueOf((new StringBuilder()).append("sliderint:").append(paramSetting.getName()).append(":").append(String.valueOf(paramSetting.getValDouble())).append("\r\n")));
          } else if (!paramSetting.onlyInt()) {
            bufferedWriter.append(String.valueOf((new StringBuilder()).append("sliderdouble:").append(paramSetting.getName()).append(":").append(String.valueOf(paramSetting.getValDouble())).append("\r\n")));
          } 
        } catch (IOException iOException) {
          iOException.printStackTrace();
        }  
      if (paramSetting.isCombo() && paramSetting.getOptions() != null)
        try {
          String str = String.valueOf((new StringBuilder()).append(Character.toUpperCase(paramSetting.getValString().toLowerCase().charAt(0))).append(paramSetting.getValString().toLowerCase().substring(1)));
          bufferedWriter.append(String.valueOf((new StringBuilder()).append("combo:").append(paramSetting.getName()).append(":").append(str).append("\r\n")));
        } catch (IOException iOException) {
          iOException.printStackTrace();
        }  
      if (paramSetting.isBind())
        try {
          bufferedWriter.append(String.valueOf((new StringBuilder()).append("key:Bind:").append(String.valueOf(paramSetting.getKeyBind())).append("\r\n")));
        } catch (IOException iOException) {
          iOException.printStackTrace();
        }  
    } 
    try {
      bufferedWriter.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public void savePanels() {
    if (ICEHack.clickgui.panels != null)
      for (Panel panel : ICEHack.clickgui.panels) {
        File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("gui").append(File.separator).append(panel.title).append(".txt")));
        PrintWriter printWriter = null;
        try {
          printWriter = new PrintWriter(file.getAbsolutePath());
        } catch (FileNotFoundException fileNotFoundException) {
          fileNotFoundException.printStackTrace();
        } 
        if (printWriter != null) {
          printWriter.print("");
          printWriter.close();
        } 
        if (file.exists())
          savePanel(panel); 
      }  
  }
  
  public void initFrames() {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("frames").append(File.separator)));
    if (!file.exists())
      file.mkdir(); 
    if (ICEHack.hudeditor.frames != null)
      for (Frame frame : ICEHack.hudeditor.frames) {
        File file1 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("frames").append(File.separator).append(frame.title).append(".txt")));
        if (!file1.exists()) {
          try {
            file1.createNewFile();
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          BufferedWriter bufferedWriter = null;
          try {
            bufferedWriter = new BufferedWriter(new FileWriter(file1));
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          if (bufferedWriter != null) {
            try {
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("x:").append(String.valueOf(frame.x)).append("\r\n")));
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("y:").append(String.valueOf(frame.y)).append("\r\n")));
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("extended:").append(String.valueOf(frame.extended)).append("\r\n")));
            } catch (IOException iOException) {
              iOException.printStackTrace();
            } 
            try {
              bufferedWriter.close();
            } catch (IOException iOException) {
              iOException.printStackTrace();
            } 
          } 
        } 
      }  
  }
  
  public void savePanel(Panel paramPanel) {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("gui").append(File.separator).append(paramPanel.title).append(".txt")));
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(file));
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    if (bufferedWriter != null) {
      try {
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("x:").append(String.valueOf(paramPanel.x)).append("\r\n")));
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("y:").append(String.valueOf(paramPanel.y)).append("\r\n")));
        bufferedWriter.write(String.valueOf((new StringBuilder()).append("extended:").append(String.valueOf(paramPanel.extended)).append("\r\n")));
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
      try {
        bufferedWriter.close();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
    } 
  }
  
  public void initPanels() {
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("gui").append(File.separator)));
    if (!file.exists())
      file.mkdir(); 
    if (ICEHack.clickgui.panels != null)
      for (Panel panel : ICEHack.clickgui.panels) {
        File file1 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("gui").append(File.separator).append(panel.title).append(".txt")));
        if (!file1.exists()) {
          try {
            file1.createNewFile();
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          BufferedWriter bufferedWriter = null;
          try {
            bufferedWriter = new BufferedWriter(new FileWriter(file1));
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          if (bufferedWriter != null) {
            try {
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("x:").append(String.valueOf(panel.x)).append("\r\n")));
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("y:").append(String.valueOf(panel.y)).append("\r\n")));
              bufferedWriter.write(String.valueOf((new StringBuilder()).append("extended:").append(String.valueOf(panel.extended)).append("\r\n")));
            } catch (IOException iOException) {
              iOException.printStackTrace();
            } 
            try {
              bufferedWriter.close();
            } catch (IOException iOException) {
              iOException.printStackTrace();
            } 
          } 
        } 
        panel.addModules();
      }  
  }
  
  public boolean hasSettings(Setting paramSetting) {
    boolean bool = false;
    try (BufferedReader null = new BufferedReader(new FileReader(new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("settings").append(File.separator).append((paramSetting.getParentMod()).name).append(".txt")))))) {
      String str;
      while ((str = bufferedReader.readLine()) != null) {
        if (str.contains(":") && str.contains(paramSetting.getName()))
          bool = true; 
      } 
    } catch (FileNotFoundException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    return bool;
  }
  
  public void createConfig(ModuleManager paramModuleManager, SettingsManager paramSettingsManager) {
    if (paramModuleManager.moduleList != null)
      for (Module module : paramModuleManager.moduleList) {
        File file1 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator)));
        if (!file1.exists())
          file1.mkdir(); 
        File file2 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("settings").append(File.separator)));
        if (!file2.exists())
          file2.mkdir(); 
        File file3 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), String.valueOf((new StringBuilder()).append(File.separator).append("ICEHack").append(File.separator).append("settings").append(File.separator).append(module.name).append(".txt")));
        if (!file3.exists()) {
          try {
            file3.createNewFile();
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          if (paramSettingsManager.getSettingsByMod(module) != null)
            makeSettings(module, file3); 
          continue;
        } 
        if (paramSettingsManager.getSettingsByMod(module) != null)
          for (Setting setting : ICEHack.setmgr.getSettingsByMod(module)) {
            if (!hasSettings(setting))
              makeSetting(setting, file3); 
          }  
      }  
  }
}
