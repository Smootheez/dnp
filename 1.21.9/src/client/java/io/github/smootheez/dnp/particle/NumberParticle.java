package io.github.smootheez.dnp.particle;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import io.github.smootheez.dnp.util.*;
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
public class NumberParticle extends Particle {
    private final String text;
    private final float initialScale;

    public NumberParticle(ClientLevel clientLevel, Vec3 pos, Vec3 velocity, String text, float initialScale) {
        super(clientLevel, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        this.text = text;
        this.initialScale = initialScale;
        this.gravity = 0;
        this.lifetime = 30;
        DebugMode.sendLoggerInfo("NumberParticle created at " + pos + " with velocity " + velocity);
    }

    public void render(Camera camera, float f) {
        Vec3 cameraPos = camera.getPosition();

        float x = (float) (this.xo + (this.x - this.xo) * f - cameraPos.x());
        float y = (float) (this.yo + (this.y - this.yo) * f - cameraPos.y());
        float z = (float) (this.zo + (this.z - this.zo) * f - cameraPos.z());

        Minecraft instance = Minecraft.getInstance();
        Font font = instance.font;
        MultiBufferSource.BufferSource bufferSource = instance.renderBuffers().bufferSource();
        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.scale(-initialScale, -initialScale, initialScale);

        float alpha = 1.0f - (float) age / lifetime;
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);

        int a = (int) (alpha * 255.0f) << 24;
        int rgb = -65536 & 0x00FFFFFF;
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
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return DnpParticleRenderType.NUMBER_RENDER;
    }
}

