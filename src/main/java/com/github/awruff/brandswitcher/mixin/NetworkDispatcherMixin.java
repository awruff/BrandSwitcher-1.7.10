package com.github.awruff.brandswitcher.mixin;

import com.github.awruff.brandswitcher.BrandSwitcherConfig;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkDispatcher.class, remap = false)
public class NetworkDispatcherMixin {
    @Shadow @Final private Side side;

    @Unique private boolean brandswitcher$vanilla;

    @Inject(method = "clientToServerHandshake", at = @At("HEAD"))
    private void brandswitcher$decide(CallbackInfo ci) {
        brandswitcher$vanilla = side == Side.CLIENT && BrandSwitcherConfig.antiForgeForNewConnection();
    }

    @Inject(method = "handleClientSideCustomPacket", at = @At("HEAD"), cancellable = true)
    private void brandswitcher$ignoreModdedPayloads(S3FPacketCustomPayload msg, ChannelHandlerContext context, CallbackInfoReturnable<Boolean> cir) {
        if (brandswitcher$vanilla) cir.setReturnValue(false);
    }

    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    private void brandswitcher$dropModdedPayloads(ChannelHandlerContext ctx, Object msg, ChannelPromise promise, CallbackInfo ci) {
        if (!brandswitcher$vanilla) return;
        if (msg instanceof FMLProxyPacket || msg instanceof C17PacketCustomPayload && !((C17PacketCustomPayload) msg).func_149559_c().startsWith("MC|")) {
            promise.trySuccess();
            ci.cancel();
        }
    }
}
