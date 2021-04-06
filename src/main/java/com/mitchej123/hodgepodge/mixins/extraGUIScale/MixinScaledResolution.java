package com.mitchej123.hodgepodge.mixins.extraGUIScale;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraft.client.gui.ScaledResolution;

import net.minecraft.client.Minecraft;
import com.mitchej123.hodgepodge.core.HodgepodgeMixinPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScaledResolution.class)
public class MixinScaledResolution {
    @Shadow
    private int scaleFactor;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    private double scaledWidthD;
    @Shadow
    private double scaledHeightD;

    private static final Logger log = LogManager.getLogger("Hodgepodge");
    /**
     * @author ElNounch
     * @reason Apply alternative scale to non-chat GUIs
     */
    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(Minecraft currentWorld, int width, int height, CallbackInfo ci) {
        double altScale = HodgepodgeMixinPlugin.config.nonChatGUIScale;
        /* 0 : use game settings' scale
         * 1 : small
         * 2 : normal (full screen - 640x480)
         * 3 : large
         * <0 : Compute maximal scale and reduce by abs(altScale) - (Auto is -1)
         */
        if ((altScale != 0) && (currentWorld.currentScreen != null) && (!(currentWorld.currentScreen instanceof net.minecraft.client.gui.GuiChat))) {
            if (altScale < 0) {
                // Find maximal scale, keeping original width/height ratio.
                altScale = altScale + Math.min(Math.floor((double)width / 320.0), Math.floor((double)height / 240.0));
                // At least a scale of 1...
                altScale = Math.max(1.0, altScale);
            }
            this.scaleFactor = (int)altScale;
            this.scaledWidthD = (double)width / altScale;
            this.scaledHeightD = (double)height / altScale;
            this.scaledWidth = (int) Math.ceil(this.scaledWidthD);
            this.scaledHeight = (int) Math.ceil(this.scaledHeightD);
        }
    }
}
