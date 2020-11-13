package me.fluffycq.icehack.mixin.client;

import java.util.List;
import me.fluffycq.icehack.module.modules.render.Tablist;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {GuiPlayerTabOverlay.class}, priority = 2147483647)
public class MixinTabList {
  @Redirect(method = {"renderPlayerlist"}, at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
  public List subList(List paramList, int paramInt1, int paramInt2) {
    return paramList.subList(paramInt1, Tablist.INST.isEnabled() ? Math.min(240, paramList.size()) : paramInt2);
  }
  
  @Inject(method = {"getPlayerName"}, at = {@At("HEAD")}, cancellable = true)
  public void getPlayerName(NetworkPlayerInfo paramNetworkPlayerInfo, CallbackInfoReturnable paramCallbackInfoReturnable) {
    if (Tablist.INST.isEnabled()) {
      paramCallbackInfoReturnable.cancel();
      paramCallbackInfoReturnable.setReturnValue(Tablist.getName(paramNetworkPlayerInfo));
    } 
  }
}
