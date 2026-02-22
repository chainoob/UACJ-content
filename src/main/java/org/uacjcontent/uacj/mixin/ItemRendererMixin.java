package org.uacjcontent.uacj.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.uacjcontent.uacj.client.SoulGlintRenderType;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static final ThreadLocal<Boolean> uacj$GUARD = ThreadLocal.withInitial(() -> false);

    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void uacj$onGetFoilBuffer(MultiBufferSource source, RenderType type, boolean isEntity, boolean hasFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!uacj$GUARD.get() && !isEntity && uacj$isGlowActive()) {
            uacj$GUARD.set(true);
            try {
                cir.setReturnValue(VertexMultiConsumer.create(source.getBuffer(SoulGlintRenderType.soulGlint()), source.getBuffer(type)));
            } finally { uacj$GUARD.set(false); }
        }
    }

    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"), cancellable = true)
    private static void uacj$onGetFoilBufferDirect(MultiBufferSource source, RenderType type, boolean isEntity, boolean hasFoil, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!uacj$GUARD.get() && !isEntity && uacj$isGlowActive()) {
            uacj$GUARD.set(true);
            try {
                cir.setReturnValue(VertexMultiConsumer.create(source.getBuffer(SoulGlintRenderType.soulGlint()), source.getBuffer(type)));
            } finally { uacj$GUARD.set(false); }
        }
    }

    @Unique
    private static boolean uacj$isGlowActive() {
        var p = Minecraft.getInstance().player;
        return p != null && p.getPersistentData().getBoolean("uacj_harvest_glow");
    }
}