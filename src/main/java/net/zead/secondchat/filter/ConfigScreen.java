package net.zead.secondchat.filter;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.zead.secondchat.SecondChat;
import net.zead.secondchat.config.SecondChatConfig;
import net.zead.secondchat.hud.SecondChatHud;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("secondchat.config.title"))
                .setSavingRunnable(() -> {
                    SecondChatConfig.save(SecondChat.instance().rules());
                    SecondChat.LOGGER.debug("Saved config with {} rules", SecondChat.instance().rules().size());
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("secondchat.config.category"));

        // Rule list
        category.addEntry(entryBuilder.startStrList(Text.translatable("secondchat.config.rules"),
                        SecondChat.instance().rules().stream().map(rule -> rule.getType() + ":" + rule.getValue()).toList())
                .setDefaultValue(() -> new ArrayList<>())
                .setSaveConsumer(newRules -> {
                    List<FilterRule> updatedRules = new ArrayList<>();
                    for (String ruleStr : newRules) {
                        String[] parts = ruleStr.split(":", 2);
                        if (parts.length == 2) {
                            updatedRules.add(new FilterRule(parts[0], parts[1]));
                        }
                    }
                    SecondChat.instance().rules().clear();
                    SecondChat.instance().rules().addAll(updatedRules);
                    SecondChatConfig.save(SecondChat.instance().rules());
                    SecondChat.LOGGER.debug("Updated rules: {}", updatedRules);
                })
                .setAddButtonTooltip(Text.translatable("secondchat.config.rules.add.tooltip"))
                .setRemoveButtonTooltip(Text.translatable("secondchat.config.rules.remove.tooltip"))
                .setTooltip(Text.translatable("secondchat.config.rules.tooltip"))
                .build());

        // Add new rule
        category.addEntry(entryBuilder.startTextField(Text.translatable("secondchat.config.add_rule"), "")
                .setDefaultValue("")
                .setSaveConsumer(value -> {
                    if (!value.isEmpty()) {
                        String type = SecondChat.getLastRuleType();
                        SecondChat.instance().addRule(new FilterRule(type, value));
                        SecondChat.LOGGER.debug("Added rule: type={}, value={}", type, value);
                    }
                })
                .setTooltip(Text.translatable("secondchat.config.add_rule.tooltip"))
                .build());

        // Rule type selector
        category.addEntry(entryBuilder.startEnumSelector(Text.translatable("secondchat.config.rule_type"), RuleType.class, RuleType.valueOf(SecondChat.getLastRuleType()))
                .setDefaultValue(RuleType.REGEX)
                .setSaveConsumer(type -> {
                    SecondChat.setLastRuleType(type.name());
                    SecondChat.LOGGER.debug("Set rule type to: {}", type.name());
                })
                .setTooltip(Text.translatable("secondchat.config.rule_type.tooltip"))
                .build());

        // Message lifetime
        category.addEntry(entryBuilder.startIntField(Text.translatable("secondchat.config.message_lifetime"), (int) (SecondChatHud.getMessageLifetime() / 1000))
                .setDefaultValue(10)
                .setMin(1)
                .setMax(60)
                .setSaveConsumer(seconds -> {
                    SecondChatHud.setMessageLifetime(seconds * 1000);
                    SecondChatConfig.save(SecondChat.instance().rules());
                    SecondChat.LOGGER.debug("Set message lifetime to {} seconds", seconds);
                })
                .setTooltip(Text.translatable("secondchat.config.message_lifetime.tooltip"))
                .build());

        return builder.build();
    }

    public enum RuleType {
        REGEX,
        CONTAINS
    }
}