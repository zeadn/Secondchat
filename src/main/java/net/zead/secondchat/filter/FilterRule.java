package net.zead.secondchat.filter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterRule {
    private final String type;
    private final String value;

    public FilterRule(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String input) {
        if (type == null || value == null || input == null) {
            return false;
        }
        switch (type.toUpperCase()) {
            case "REGEX":
                try {
                    return Pattern.compile(value).matcher(input).find();
                } catch (PatternSyntaxException e) {
                    return false;
                }
            case "CONTAINS":
                return input.contains(value);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "FilterRule[type=" + type + ", value=" + value + "]";
    }
}