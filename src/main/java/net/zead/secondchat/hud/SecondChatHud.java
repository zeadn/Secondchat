package net.zead.secondchat.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.zead.secondchat.SecondChat;
import net.zead.secondchat.config.HudConfig;
import net.zead.secondchat.config.SecondChatHudConfig;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;

public class SecondChatHud {
    private static final List<Pair<Text, Long>> filteredMessages = new ArrayList<>();
    private static long MESSAGE_LIFETIME_MS = 10000; // 10 seconds
    private static final long FADE_DURATION_MS = 2000; // 2 seconds for fade-out
    private static HudConfig hudConfig;

    public static void init() {
        hudConfig = SecondChatHudConfig.load();
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            try {
                SecondChat.LOGGER.debug("HudRenderCallback triggered, tickDelta: {}", tickDelta);
                MinecraftClient client = MinecraftClient.getInstance();
                if (client == null || client.textRenderer == null) {
                    SecondChat.LOGGER.warn("Client or textRenderer is null, skipping render");
                    return;
                }
                int x = client.getWindow().getScaledWidth() + hudConfig.getXOffset(); // Use offset from right
                int y = hudConfig.getY(); // Configurable Y position
                int width = hudConfig.getWidth(); // Configurable width
                int backgroundColor = hudConfig.getBackgroundColor(); // Configurable background color
                SecondChat.LOGGER.debug("Rendering HUD at x={}, y={}, width={}, messages: {}", x, y, width, filteredMessages.size());

                // Save shader color state
                float[] shaderColor = RenderSystem.getShaderColor();
                float[] savedShaderColor = new float[] { shaderColor[0], shaderColor[1], shaderColor[2], shaderColor[3] };
                SecondChat.LOGGER.debug("Saved shaderColor=[{},{},{},{}]",
                        savedShaderColor[0], savedShaderColor[1], savedShaderColor[2], savedShaderColor[3]);

                // Set up blending
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc(); // Uses SRC_ALPHA, ONE_MINUS_SRC_ALPHA

                // Remove expired messages
                long currentTime = System.currentTimeMillis();
                filteredMessages.removeIf(pair -> currentTime - pair.getRight() > MESSAGE_LIFETIME_MS);
                SecondChat.LOGGER.debug("After cleanup, {} messages remain", filteredMessages.size());

                for (int i = 0; i < filteredMessages.size(); i++) {
                    Pair<Text, Long> pair = filteredMessages.get(i);
                    Text message = pair.getLeft();
                    long messageTime = pair.getRight();
                    int messageY = y - (filteredMessages.size() - 1 - i) * 10; // Stack messages downward
                    int textWidth = client.textRenderer.getWidth(message);
                    int textColor = message.getStyle().getColor() != null ? message.getStyle().getColor().getRgb() : 0xFFFFFF; // Default to white

                    // Calculate alpha for fading
                    long age = currentTime - messageTime;
                    float alpha = 1.0F;
                    if (age > MESSAGE_LIFETIME_MS - FADE_DURATION_MS) {
                        alpha = 1.0F - ((float) (age - (MESSAGE_LIFETIME_MS - FADE_DURATION_MS)) / FADE_DURATION_MS);
                        alpha = Math.max(0.0F, Math.min(1.0F, alpha)); // Clamp between 0 and 1
                    }
                    SecondChat.LOGGER.debug("Rendering message: {} with style: {}, color: {}, age: {}, alpha: {}",
                            message.getString(),
                            message.getStyle(),
                            message.getStyle().getColor() != null ? message.getStyle().getColor().getHexCode() : "null (using default #FFFFFF)",
                            age, alpha);

                    // Draw background with adjusted alpha
                    int adjustedBackgroundColor = adjustAlpha(backgroundColor, alpha);
                    drawBackground(matrixStack, x - 2, messageY - 1, x + textWidth + 2, messageY + 9, adjustedBackgroundColor);

                    // Reset shader color before text rendering
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                    // Draw text with shadow
                    client.textRenderer.drawWithShadow(matrixStack, message, x, messageY, textColor);
                }

                // Restore state
                RenderSystem.setShaderColor(savedShaderColor[0], savedShaderColor[1], savedShaderColor[2], savedShaderColor[3]);
                RenderSystem.disableBlend();
                SecondChat.LOGGER.debug("Restored shaderColor=[{},{},{},{}]",
                        savedShaderColor[0], savedShaderColor[1], savedShaderColor[2], savedShaderColor[3]);
            } catch (Exception e) {
                SecondChat.LOGGER.error("Error in HudRenderCallback: {}", e.getMessage(), e);
            }
        });
        SecondChat.LOGGER.info("Registered HudRenderCallback");
    }

    private static void drawBackground(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color) {
        // Use Minecraft's built-in fill method
        DrawableHelper.fill(matrixStack, x1, y1, x2, y2, color);
    }

    private static int adjustAlpha(int color, float alpha) {
        int originalAlpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        // Apply the fade alpha to the original alpha
        int alphaInt = (int) (originalAlpha * alpha) & 0xFF;
        return (alphaInt << 24) | (red << 16) | (green << 8) | blue;
    }

    public static void addMessage(Text message) {
        filteredMessages.add(Pair.of(message, System.currentTimeMillis()));
        SecondChat.LOGGER.debug("Added message to HUD: {}, style: {}", message.getString(), message.getStyle());
        if (filteredMessages.size() > 10) {
            filteredMessages.remove(0);
        }
    }

    public static long getMessageLifetime() {
        return MESSAGE_LIFETIME_MS;
    }

    public static void setMessageLifetime(long lifetime) {
        MESSAGE_LIFETIME_MS = Math.max(1000, lifetime); // Minimum 1 second
        SecondChat.LOGGER.debug("Set message lifetime to {} ms", MESSAGE_LIFETIME_MS);
    }
}