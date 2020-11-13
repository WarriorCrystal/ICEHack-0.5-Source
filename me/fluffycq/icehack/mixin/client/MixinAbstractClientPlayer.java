package me.fluffycq.icehack.mixin.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {AbstractClientPlayer.class}, priority = 2147483647)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {}
