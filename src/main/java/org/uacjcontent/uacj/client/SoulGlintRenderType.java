package org.uacjcontent.uacj.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SoulGlintRenderType extends RenderType {
    private static final ResourceLocation SOUL_GLINT = new ResourceLocation("uacj", "textures/misc/soul_glint.png");

    public SoulGlintRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType soulGlint() {
        return create("soul_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(SOUL_GLINT, true, false))
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY)
                        .setTexturingState(GLINT_TEXTURING)
                        .createCompositeState(false));
    }
}
