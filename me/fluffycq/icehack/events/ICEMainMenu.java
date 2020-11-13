//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.events;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GLContext;

public class ICEMainMenu extends GuiMainMenu {
  public ICEMainMenu() {
    this.mc.fontRenderer.drawStringWithShadow("ICEHack b1.5", 1.0F, 1.0F, -1);
    this.openGLWarning2 = MORE_INFO_TEXT;
    this.splashText = "missingno";
    IResource iResource = null;
    try {
      ArrayList<String> arrayList = Lists.newArrayList();
      iResource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iResource.getInputStream(), StandardCharsets.UTF_8));
      String str;
      while ((str = bufferedReader.readLine()) != null) {
        str = str.trim();
        if (!str.isEmpty())
          arrayList.add(str); 
      } 
      if (!arrayList.isEmpty())
        do {
          this.splashText = arrayList.get(RANDOM.nextInt(arrayList.size()));
        } while (this.splashText.hashCode() == 125780783); 
    } catch (IOException iOException) {
    
    } finally {
      IOUtils.closeQuietly((Closeable)iResource);
    } 
    this.openGLWarning1 = "";
    if (!(GLContext.getCapabilities()).OpenGL20 && !OpenGlHelper.areShadersSupported()) {
      this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
      this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
      this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
    } 
  }
  
  static {
  
  }
}
