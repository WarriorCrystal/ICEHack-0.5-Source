package me.fluffycq.icehack;

import me.fluffycq.icehack.clickgui.ClickGUI;
import me.fluffycq.icehack.command.CommandManager;
import me.fluffycq.icehack.config.Configuration;
import me.fluffycq.icehack.events.ForgeEvents;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.hudeditor.HUDScreen;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.modules.screen.DupeScreen;
import me.fluffycq.icehack.setting.SettingsManager;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "icehack", name = "ICE Hack", version = "b1.5")
public class ICEHack {
  @EventHandler
  public void init(FMLInitializationEvent paramFMLInitializationEvent) {
    setmgr = new SettingsManager();
    MinecraftForge.EVENT_BUS.register(fevents = new ForgeEvents());
    clickgui = new ClickGUI();
    cmdmanager = new CommandManager();
    MinecraftForge.EVENT_BUS.register(new DupeScreen());
    hudeditor = new HUDScreen();
  }
  
  @EventHandler
  public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {
    Configuration configuration = new Configuration();
    friends = new Friends();
    configuration.createConfig(fevents.moduleManager, setmgr);
    for (Module module : fevents.moduleManager.moduleList)
      configuration.loadSettings(module); 
    if (!configuration.checkPanelDir()) {
      configuration.initPanels();
    } else {
      configuration.loadPanels();
    } 
    configuration.initFrames();
    configuration.loadFrames();
    friends.createFriends();
  }
  
  static {
    VERSION = "b1.5";
    MODID = "icehack";
    EVENT_BUS = (EventBus)new EventManager();
  }
  
  public static void save() {
    Configuration configuration = new Configuration();
    configuration.saveSettings(fevents.moduleManager, setmgr);
    configuration.savePanels();
    configuration.saveFrames();
    friends.saveFriends();
  }
}
