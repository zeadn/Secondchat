package net.zead.secondchat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.zead.secondchat.config.SecondChatConfig;
import net.zead.secondchat.filter.ConfigScreen;
import net.zead.secondchat.filter.FilterRule;
import net.zead.secondchat.hud.SecondChatHud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SecondChat implements ClientModInitializer, ModMenuApi {
    public static final String MOD_ID = "secondchat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static SecondChat INSTANCE;
    private List<FilterRule> rules;
    private static String lastRuleType = "REGEX"; // Store last selected rule type

    public static SecondChat instance() {
        if (INSTANCE == null) {
            LOGGER.error("SecondChat instance is null! Ensure onInitializeClient is called.");
        }
        return INSTANCE;
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting SecondChat initialization");
        try {
            INSTANCE = this;
            rules = SecondChatConfig.load();
            LOGGER.info("Loaded {} filter rules: {}", rules.size(), rules);
            SecondChatHud.init();
            LOGGER.info("SecondChat initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize SecondChat: {}", e.getMessage(), e);
        }
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::create;
    }

    public boolean matches(String input) {
        if (rules == null) {
            rules = SecondChatConfig.load();
            LOGGER.warn("Rules were null, reloaded {} filter rules: {}", rules.size(), rules);
        }
        boolean matches = rules.stream().anyMatch(rule -> {
            boolean result = rule.matches(input);
            LOGGER.debug("Checking rule {} against input '{}': {}", rule, input, result);
            return result;
        });
        return matches;
    }

    public void addRule(FilterRule rule) {
        if (rules == null) {
            rules = new ArrayList<>();
        }
        rules.add(rule);
        SecondChatConfig.save(rules);
        LOGGER.info("Added filter rule: {}", rule);
    }

    public void removeRule(FilterRule rule) {
        if (rules != null) {
            rules.remove(rule);
            SecondChatConfig.save(rules);
            LOGGER.info("Removed filter rule: {}", rule);
        }
    }

    public List<FilterRule> rules() {
        return rules != null ? rules : new ArrayList<>();
    }

    public static String getLastRuleType() {
        return lastRuleType;
    }

    public static void setLastRuleType(String type) {
        lastRuleType = type;
        LOGGER.debug("Set last rule type to: {}", type);
    }
}