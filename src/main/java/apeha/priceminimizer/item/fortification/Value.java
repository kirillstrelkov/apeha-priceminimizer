package apeha.priceminimizer.item.fortification;

import apeha.priceminimizer.item.Property;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Value {
    ARMOUR("\\(бронь \\+(\\d+)\\)", null), SHARPENING("\\(заточ. \\+(\\d+)\\)",
            null), POWER("\\(сила \\+(\\d+)\\)", Property.POWER), AGILITY(
            "\\(ловк. \\+(\\d+)\\)", Property.AGILITY), REACTION(
            "\\(реак. \\+(\\d+)\\)", Property.REACTION), FORTUNE(
            "\\(удач. \\+(\\d+)\\)", Property.FOTUNE), SPITE(
            "\\(злость \\+(\\d+)\\)", Property.SPITE), CONSTITUTION(
            "\\(слож. \\+(\\d+)\\)", Property.CONSTITUTION);

    private Pattern pattern;
    private Property property;

    Value(String regexp, Property property) {
        this.pattern = Pattern.compile(regexp);
        this.property = property;
    }

    public static Value getValue(String text) {
        for (Value fort : Value.values()) {
            Matcher matcher = fort.getPattern().matcher(text);
            if (matcher.find())
                return fort;
        }
        return null;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Property getProperty() {
        return property;
    }
}
