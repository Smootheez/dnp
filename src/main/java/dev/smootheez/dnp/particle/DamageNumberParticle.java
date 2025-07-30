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

// Custom particle for rendering floating damage/healing numbers above entities
public class DamageNumberParticle extends Particle {
    private String damageDealt;         // Text to render (e.g., "10", "+5")
    private float initialScale;         // Base size of the particle
    private int color = 0xFFFFFF;       // Default color: white

    // Constructor: initializes the particle's position and motion
    public DamageNumberParticle(ClientLevel level, Vec3 position, Vec3 velocity) {
        super(level, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        this.lifetime = 40;  // Particle lasts 40 ticks (2 seconds at 20 TPS)
        this.age = 0;        // Start age
        this.gravity = 0.0F; // No gravity effect (it floats)
    }

    // Called every frame to render the particle
    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font; // Get Minecraft's font renderer

        // Calculate interpolated world position relative to camera
        float x = (float) (this.xo + (this.x - this.xo) * partialTicks - camera.getPosition().x());
        float y = (float) (this.yo + (this.y - this.yo) * partialTicks - camera.getPosition().y());
        float z = (float) (this.zo + (this.z - this.zo) * partialTicks - camera.getPosition().z());

        // Setup pose stack for rendering
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(x, y, z);         // Move to particle position
        poseStack.mulPose(camera.rotation()); // Face toward the camera (billboarding)
        poseStack.scale(-initialScale, -initialScale, initialScale); // Scale and flip to face player

        Matrix4f matrix = poseStack.last().pose(); // Get the transformation matrix
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource(); // Get buffer for rendering

        // Compute alpha fade out near end of lifetime
        float progress = (float) age / (float) lifetime;
        float alphaF = progress < 0.75F ? 1.0F : 1.0F - ((progress - 0.75F) / 0.25F); // Fade only in last 25%
        int alpha = (int) (Mth.clamp(alphaF, 0.0F, 1.0F) * 255) << 24;                // Convert alpha to ARGB
        int packedLight = 0xF000F0;                                                  // Full brightness
        int finalColor = alpha | (color & 0xFFFFFF);                                 // Combine alpha and RGB

        // Center text horizontally
        float textX = -font.width(damageDealt) / 2f;

        // Draw the text (the damage number)
        font.drawInBatch(damageDealt, textX, 0, finalColor, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();    // Restore pose stack
        bufferSource.endBatch(); // Flush the text render buffer
    }

    // Called every tick to update the particle's state
    @Override
    public void tick() {
        super.tick();
        this.age++;         // Increment particle age
        this.yd += 0.001;   // Slight upward movement every tick (smooth rise)

        // Remove the particle once it exceeds its lifetime
        if (this.age >= this.lifetime) {
            this.remove();
        }
    }

    // Specify that we are using a custom render type (not the default particle system)
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    // Set the number text to display
    public void setDamageDealt(@NotNull String damageDealt) {
        this.damageDealt = damageDealt;
    }

    // Set the RGB color of the text (alpha handled during render)
    public void setColor(int color) {
        this.color = color;
    }

    // Set the size scale of the particle text
    public void setInitialScale(float initialScale) {
        this.initialScale = initialScale;
    }
}


