package net.zead.secondchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.zead.secondchat.SecondChat;
import net.zead.secondchat.filter.FilterRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SecondChatConfig {
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("secondchat.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<FilterRule> load() {
        try {
            if (!Files.exists(CONFIG)) {
                Files.createDirectories(CONFIG.getParent());
                List<FilterRule> defaultRules = new ArrayList<>();
                Files.write(CONFIG, GSON.toJson(defaultRules).getBytes());
                SecondChat.LOGGER.info("Created default config file at {}", CONFIG);
                return defaultRules;
            }
            String json = Files.readString(CONFIG);
            SecondChat.LOGGER.debug("Loaded JSON: {}", json);
            List<FilterRule> rules = GSON.fromJson(json, new TypeToken<List<FilterRule>>(){}.getType());
            if (rules == null) {
                rules = new ArrayList<>();
            }
            SecondChat.LOGGER.info("Loaded {} filter rules: {}", rules.size(), rules);
            return rules;
        } catch (IOException e) {
            SecondChat.LOGGER.error("Failed to load config: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static void save(List<FilterRule> rules) {
        try {
            Files.createDirectories(CONFIG.getParent());
            String json = GSON.toJson(rules);
            Files.write(CONFIG, json.getBytes());
            SecondChat.LOGGER.debug("Saved JSON: {}", json);
        } catch (IOException e) {
            SecondChat.LOGGER.error("Failed to save config: {}", e.getMessage(), e);
        }
    }
}