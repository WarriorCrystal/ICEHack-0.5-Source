package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.KeyEvent;
import me.fluffycq.icehack.events.MiddleClickEvent;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Minecraft.class}, priority = 2147483647)
public class MixinMinecraft {
  @Inject(method = {"runTickKeyboard"}, at = {@At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE)})
  private void onKeyboard(CallbackInfo paramCallbackInfo) {
    int i = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 256) : Keyboard.getEventKey();
    if (Keyboard.getEventKeyState()) {
      KeyEvent keyEvent = new KeyEvent(i);
      ICEHack.EVENT_BUS.post(keyEvent);
      if (keyEvent.isCancelled())
        paramCallbackInfo.cancel(); 
      if (ICEHack.fevents.moduleManager != null)
        for (Module module : ICEHack.fevents.moduleManager.moduleList)
          module.onKey(i);  
    } 
  }
  
  @Inject(method = {"middleClickMouse"}, at = {@At("HEAD")})
  private void middleClickMouse(CallbackInfo paramCallbackInfo) {
    MiddleClickEvent middleClickEvent = new MiddleClickEvent();
    ICEHack.EVENT_BUS.post(middleClickEvent);
  }
  
  @Inject(method = {"run"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V", shift = At.Shift.BEFORE)})
  public void displayCrashReport(CallbackInfo paramCallbackInfo) {
    ICEHack.save();
  }
  
  @Inject(method = {"shutdown"}, at = {@At("HEAD")})
  public void shutdown(CallbackInfo paramCallbackInfo) {
    ICEHack.save();
  }
}
