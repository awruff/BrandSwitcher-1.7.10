package com.github.awruff.brandswitcher.mixin;

import com.github.awruff.brandswitcher.BrandSwitcherConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import cpw.mods.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class FMLCommonHandlerMixin {
    @ModifyReturnValue(method = "getModName", at = @At("RETURN"))
    private String brandswitcher$getBrand(String original) {
        String brand = BrandSwitcherConfig.sendBrand();
        return brand != null ? brand : original;
    }
}
