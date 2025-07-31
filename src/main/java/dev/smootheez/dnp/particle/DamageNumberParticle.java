package dev.smootheez.dnp.particle;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.*;

@Environment(EnvType.CLIENT)
public class DamageNumberParticle extends Particle {
    private final String text;
    private final float initialScale;
    private final int color;

    public DamageNumberParticle(ClientLevel level, Vec3 pos, Vec3 velocity, String text, float initialScale, int color) {
        super(level, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        this.text = text;
        this.initialScale = initialScale;
        this.color = color;
        this.gravity = 0;
        this.lifetime = 30;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();

        float x = (float) (this.xo + (this.x - this.xo) * partialTicks - cameraPos.x());
        float y = (float) (this.yo + (this.y - this.yo) * partialTicks - cameraPos.y());
        float z = (float) (this.zo + (this.z - this.zo) * partialTicks - cameraPos.z());

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.scale(-initialScale, -initialScale, initialScale);

        // Alpha fading
        float alpha = 1.0f - (float) age / lifetime;
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);

        // Compose ARGB color with fading alpha
        int a = (int) (alpha * 255.0f) << 24;
        int rgb = color & 0x00FFFFFF;
        int argbWithAlpha = a | rgb;

        font.drawInBatch(
                text,
                -font.width(text) / 2f,
                0,
                argbWithAlpha,
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                0xF000F0
        );

        poseStack.popPose();
        bufferSource.endBatch();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}


