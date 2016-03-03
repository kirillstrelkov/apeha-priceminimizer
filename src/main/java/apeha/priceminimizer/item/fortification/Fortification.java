package apeha.priceminimizer.item.fortification;

import apeha.priceminimizer.item.Item;

import java.util.regex.Matcher;

public class Fortification {
    public static int MULTIPLICAND = 5;
    private Value value;
    private int times;

    private Fortification(Value value, int times) {
        this.value = value;
        this.times = times;
    }

    public static Fortification getFortification(String text) {
        Value value = Value.getValue(text);
        if (value == null)
            return null;
        Matcher matcher = value.getPattern().matcher(text);
        if (matcher.find()) {
            int val = Integer.parseInt(matcher.group(1));
            return new Fortification(value, val);
        }
        return null;
    }

    public static Fortification getFortification(Item item) {
        String text = item.toString();
        return getFortification(text);
    }

    public Value getValue() {
        return value;
    }

    public int getTimes() {
        return times;
    }

}
