package dev.smootheez.dnp.particle;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.*;
import org.joml.*;

public class DamageNumberParticle extends Particle {
    private String damageDealt;
    private float initialScale;
    private int color = 0xFFFFFF; // default white

    public DamageNumberParticle(ClientLevel level, Vec3 position, Vec3 velocity) {
        super(level, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        this.lifetime = 40; // 2 seconds instead of 1
        this.age = 0;
        this.gravity = 0.0F;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        float x = (float)(this.xo + (this.x - this.xo) * partialTicks - camera.getPosition().x());
        float y = (float)(this.yo + (this.y - this.yo) * partialTicks - camera.getPosition().y());
        float z = (float)(this.zo + (this.z - this.zo) * partialTicks - camera.getPosition().z());

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-initialScale, -initialScale, initialScale); // Billboard + scale

        Matrix4f matrix = poseStack.last().pose();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        float progress = (float) age / (float) lifetime;
        float alphaF = progress < 0.75F ? 1.0F : 1.0F - ((progress - 0.75F) / 0.25F);
        int alpha = (int)(Mth.clamp(alphaF, 0.0F, 1.0F) * 255) << 24;
        int packedLight = 0xF000F0;
        int finalColor = alpha | (color & 0xFFFFFF);

        float textX = -font.width(damageDealt) / 2f;
        font.drawInBatch(damageDealt, textX, 0, finalColor, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
        bufferSource.endBatch(); // Important to flush the text
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;
        this.yd += 0.001; // slower rise
        if (this.age >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void setDamageDealt(@NotNull String damageDealt) {
        this.damageDealt = damageDealt;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setInitialScale(float initialScale) {
        this.initialScale = initialScale;
    }
}

