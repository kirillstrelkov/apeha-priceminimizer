package apeha.priceminimizer.item.stone;

import apeha.priceminimizer.item.Property;

import java.util.Arrays;
import java.util.List;

public enum Stone {
    SELENITE("Селенит", Property.GUARD_OF_DODGE_ENEMY), CHRYSOLITE("Хризолит",
            Property.GUARD_OF_RESPONSE_ENEMY), FLUORITE("Флуорит",
            Property.GUARD_OF_CRITICAL_STRIKE_ENEMY), EMERALD("Изумруд",
            Property.GUARD_OF_FORTUNE_ENEMY), ALEXANDRITE("Александрит",
            Property.POWER), DIAMOND("Алмаз", Property.AGILITY), AMAZON(
            "Амазонит", Property.FOTUNE), APATITE("Апатит", Property.REACTION), CALAITE(
            "Бирюза", Property.CONSTITUTION), JASPER("Яшма", Property.SPITE), LAZURITE(
            "Лазурит", Property.MARKSMANSHIP), MALACHITE("Малахит",
            Property.MASTERY_OF_WEAPON), OBSIDIAN("Обсидиан",
            Property.MASTERY_OF_DEFENCE), ONYX("Оникс",
            Property.MASTERY_OF_FISTICUFFS), OPAL("Опал",
            Property.GUARD_OF_FORTUNE), PYRITE("Пирит", Property.GUARD_OF_DODGE), RUBY(
            "Рубин", Property.GUARD_OF_RESPONSE), SAPPHIRE("Сапфир",
            Property.GUARD_OF_CRITICAL_STRIKE);

    private final String name;
    private final Property property;

    Stone(String name, Property property) {
        this.name = name;
        this.property = property;
    }

    public static Stone getStoneFrom(String text) {
        Stone[] values = Stone.values();
        for (Stone value : values) {
            List<String> words = Arrays.asList(text.split("\\s"));
            if (words.contains(value.getName())
                    || text.contains(value.getProperty().getName()))
                return value;
        }
        return null;
    }

    public static String[] getAllStoneNames() {
        Stone[] values = Stone.values();
        String[] names = new String[values.length];
        for (int i = 0; i < names.length; i++)
            names[i] = values[i].getName();
        return names;
    }

    public String getName() {
        return this.name;
    }

    public Property getProperty() {
        return this.property;
    }
}
