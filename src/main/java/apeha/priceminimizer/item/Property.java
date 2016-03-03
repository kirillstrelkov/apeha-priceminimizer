package apeha.priceminimizer.item;

import apeha.priceminimizer.common.Utils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Property {
    HEAD_ARMOR("Броня головы", "[-+]\\d+"), BODY_ARMOR("Броня корпуса",
            "[-+]\\d+"), LEGS_ARMOR("Броня ног", "[-+]\\d+"), LEFT_HAND_ARMOR(
            "Броня левой руки", "[-+]\\d+"), RIGHT_HAND_ARMOR(
            "Броня правой руки", "[-+]\\d+"), POWER_IMPACT("Влияние силы",
            "\\d+%"), CHARGES("Зарядов", "\\d+"), POWER("Сила", "[-+]\\d+"), AGILITY(
            "Ловкость", "[-+]\\d+"), REACTION("Реакция", "[-+]\\d+"), SPITE(
            "Злость", "[-+]\\d+"), FOTUNE("Удача", "[-+]\\d+"), CONSTITUTION(
            "Сложение", "[-+]\\d+"), MASTERY_OF_FISTICUFFS(
            "Мастерство кулачного боя", "[-+]\\d+%"), MASTERY_OF_DEFENCE(
            "Мастерство защиты", "[-+]\\d+%"), MASTERY_OF_WEAPON(
            "Мастерство владения оружием", "[-+]\\d+%"), MARKSMANSHIP(
            "Меткость", "[-+]\\d+%"), GUARD_OF_DODGE("Оберег уворота",
            "[-+]\\d+%"), GUARD_OF_FORTUNE("Оберег удачи", "[-+]\\d+%"), GUARD_OF_RESPONSE(
            "Оберег ответа", "[-+]\\d+%"), GUARD_OF_CRITICAL_STRIKE(
            "Оберег крита", "[-+]\\d+%"), GUARD_OF_DODGE_ENEMY(
            "Оберег уворота противника", "\\-\\d+%"), GUARD_OF_FORTUNE_ENEMY(
            "Оберег удачи противника", "\\-\\d+%"), GUARD_OF_RESPONSE_ENEMY(
            "Оберег ответа противника", "\\-\\d+%"), GUARD_OF_CRITICAL_STRIKE_ENEMY(
            "Оберег крита противника", "\\-\\d+%"), AREA_OF_EFFECT(
            "Радиус поражения", "\\d+"), ACCURACY("Точность", "\\d+%"), REQUIRED_LEVEL(
            "Требуемый Уровень", "(\\d+\\-\\d+)|(\\d+)"), DAMAGE("Урон",
            "\\d+\\-\\d+"), REQUIRED_RACE("Требуемая Раса", "[А-Яа-я]+"), PRICE_IN_MARKET(
            "Цена", "\\d+\\.\\d+( *ст\\.)*( *\\+ *\\d+\\.\\d+( *ст\\.)*)*"), PRICE_IN_SHOP(
            "Цена в магазине", "\\d+\\.\\d+"), TWO_HANDED("Двуручное", ""), DURABILITY(
            "прочность", "\\d+( *\\/ *\\d+)*"), SPELLED(
            "Наложено заклятие еще", "(\\-*\\d+((д)|(ч)|(мин)) *)+"), LIFE_TIME(
            "Время жизни", "(\\-*\\d+((д)|(ч)|(мин)) *)+"), SHOP("Лавка",
            "\\[.+\\]");

    public static List<Property> STATS = Lists.newLinkedList();
    public static List<Property> MASTERIES = Lists.newLinkedList();
    public static List<Property> GUARDS = Lists.newLinkedList();
    public static List<Property> GUARDS_OF_ENEMY = Lists.newLinkedList();
    public static List<Property> GUARDS_AND_MASTERIES = Lists.newLinkedList();
    public static List<Property> ALL_PARAMETERS = Lists.newLinkedList();
    public static List<Property> ARMOR = Lists.newLinkedList();

    static {
        STATS.add(Property.POWER);
        STATS.add(Property.AGILITY);
        STATS.add(Property.REACTION);
        STATS.add(Property.SPITE);
        STATS.add(Property.FOTUNE);
        STATS.add(Property.CONSTITUTION);

        MASTERIES.add(Property.MASTERY_OF_FISTICUFFS);
        MASTERIES.add(Property.MASTERY_OF_DEFENCE);
        MASTERIES.add(Property.MASTERY_OF_WEAPON);
        MASTERIES.add(Property.MARKSMANSHIP);

        GUARDS.add(Property.GUARD_OF_DODGE);
        GUARDS.add(Property.GUARD_OF_FORTUNE);
        GUARDS.add(Property.GUARD_OF_RESPONSE);
        GUARDS.add(Property.GUARD_OF_CRITICAL_STRIKE);

        GUARDS_OF_ENEMY.add(Property.GUARD_OF_DODGE_ENEMY);
        GUARDS_OF_ENEMY.add(Property.GUARD_OF_FORTUNE_ENEMY);
        GUARDS_OF_ENEMY.add(Property.GUARD_OF_RESPONSE_ENEMY);
        GUARDS_OF_ENEMY.add(Property.GUARD_OF_CRITICAL_STRIKE_ENEMY);

        GUARDS_AND_MASTERIES.addAll(MASTERIES);
        GUARDS_AND_MASTERIES.addAll(GUARDS);
        GUARDS_AND_MASTERIES.addAll(GUARDS_OF_ENEMY);

        ALL_PARAMETERS.addAll(STATS);
        ALL_PARAMETERS.addAll(MASTERIES);
        ALL_PARAMETERS.addAll(GUARDS);
        ALL_PARAMETERS.addAll(GUARDS_OF_ENEMY);

        ARMOR.add(Property.HEAD_ARMOR);
        ARMOR.add(Property.BODY_ARMOR);
        ARMOR.add(Property.LEGS_ARMOR);
        ARMOR.add(Property.RIGHT_HAND_ARMOR);
        ARMOR.add(Property.LEFT_HAND_ARMOR);
    }

    private final String name;
    private final String pattern;

    Property(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public static Property getPropertyFrom(String text) {
        Property[] values = Property.values();
        for (int i = values.length - 1; i >= 0; i--) {
            Property property = values[i];
            if (text.contains(property.getName()))
                if (property.equals(Property.LIFE_TIME))
                    return Property.SPELLED;
                else
                    return property;
        }
        return null;
    }

    public static String getPropertyNameFrom(String text) {
        Property property = getPropertyFrom(text);
        if (property != null)
            return property.getName();
        else
            return null;
    }

    private static String getPropertyValueFrom(Property property, String text) {
        if (property != null) {
            text = text.replaceAll(property.getName(), "").trim();
            Pattern pattern = Pattern.compile(property.getPattern());
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
                if (property.equals(Property.DURABILITY))
                    return matcher.group().replaceAll("\\ ", "");
                else
                    return matcher.group();
            else
                return null;
        } else
            return null;
    }

    public static String getPropertyValueFrom(String text) {
        Property property = getPropertyFrom(text);
        return getPropertyValueFrom(property, text);
    }

    public static String formatProperty(Property property, String value) {
        if (property != null && value != null) {
            if (!value.matches(property.getPattern()))
                return null;
            String name = property.getName();
            if (property.equals(Property.CHARGES)
                    || property.equals(Property.DAMAGE))
                return name + " " + value;
            else if (property.equals(Property.TWO_HANDED))
                return name + value;
            else
                return name + ": " + value;
        } else
            return null;
    }

    public static String formatProperty(String text) {
        Property property = getPropertyFrom(text);
        String value = getPropertyValueFrom(property, text);
        return formatProperty(property, value);
    }

    public static int getScoreFrom(String line) {
        Property property = Property.getPropertyFrom(line);
        String value = Property.getPropertyValueFrom(property, line);

        int val = 0;
        if (property.equals(Property.DAMAGE)) {
            if (value.contains("-")) {
                val = Utils.getInteger("\\d+\\-", value)
                        + Math.abs(Utils.getInteger("\\-\\d+", value));
            }
            return val / 4;
        } else {
            val = Property.getInteger(property, line);

            if (Property.GUARDS_OF_ENEMY.contains(property))
                val = Math.abs(val);
            if (Property.GUARDS_AND_MASTERIES.contains(property)
                    || Property.ARMOR.contains(property))
                val = val / 5;
        }

        return val;
    }

    public static int getInteger(Property property, String text) {
        if (STATS.contains(property) || GUARDS_AND_MASTERIES.contains(property)
                || GUARDS_OF_ENEMY.contains(property)
                || ARMOR.contains(property)) {
            Matcher matcher = Pattern.compile("-*\\d+").matcher(text);
            if (matcher.find())
                return Integer.parseInt(matcher.group());
        }
        return -999;
    }

    public static String formatPropertyByValue(Property property, int value) {
        String format = null;
        String text;
        if (GUARDS_AND_MASTERIES.contains(property))
            format = "%s: %s%%";
        else if (STATS.contains(property))
            format = "%s: %s";
        else if (ARMOR.contains(property))
            format = "%s: %s";

        if (value > 0)
            text = "+" + String.valueOf(value);
        else
            text = String.valueOf(value);
        if (format == null)
            return null;
        else
            return String.format(format, property.getName(), text);
    }

    public static String formatPropertyByScore(Property property, int score) {
        String format = null;
        String value;
        if (GUARDS_AND_MASTERIES.contains(property)) {
            score = score * 5;
            format = "%s: %s%%";
        } else if (STATS.contains(property))
            format = "%s: %s";
        else if (ARMOR.contains(property)) {
            score = score * 5;
            format = "%s: %s";
        }

        if (score > 0)
            value = "+" + String.valueOf(score);
        else
            value = String.valueOf(score);
        if (format == null)
            return null;
        else
            return String.format(format, property.getName(), value);
    }

    public String getName() {
        return this.name;
    }

    public String getPattern() {
        return this.pattern;
    }
}
