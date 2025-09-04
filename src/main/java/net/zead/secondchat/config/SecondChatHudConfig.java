package net.zead.secondchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.zead.secondchat.SecondChat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SecondChatHudConfig {
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("secondchat_hud.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static HudConfig load() {
        try {
            HudConfig hudConfig = new HudConfig();
            if (!Files.exists(CONFIG)) {
                Files.createDirectories(CONFIG.getParent());
                Files.write(CONFIG, GSON.toJson(hudConfig).getBytes());
                SecondChat.LOGGER.info("Created default HUD config file at {}", CONFIG);
                return hudConfig;
            }
            String json = Files.readString(CONFIG);
            SecondChat.LOGGER.debug("Loaded HUD JSON: {}", json);
            HudConfig loadedConfig = GSON.fromJson(json, HudConfig.class);
            if (loadedConfig == null) {
                loadedConfig = new HudConfig();
            }
            SecondChat.LOGGER.info("Loaded HUD config: {}", loadedConfig);
            return loadedConfig;
        } catch (IOException e) {
            SecondChat.LOGGER.error("Failed to load HUD config: {}", e.getMessage(), e);
            return new HudConfig();
        }
    }

    public static void save(HudConfig hudConfig) {
        try {
            Files.createDirectories(CONFIG.getParent());
            String json = GSON.toJson(hudConfig);
            Files.write(CONFIG, json.getBytes());
            SecondChat.LOGGER.debug("Saved HUD JSON: {}", json);
        } catch (IOException e) {
            SecondChat.LOGGER.error("Failed to save HUD config: {}", e.getMessage(), e);
        }
    }
}