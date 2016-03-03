package apeha.priceminimizer.common;

import com.google.common.collect.Lists;

import java.util.EnumSet;
import java.util.List;

public enum Category {

    HELMETS("Шлемы"), AMULETS("Амулеты"), ARMOURS("Латы"), ARMS("Оружие"), BELTS(
            "Пояса"), BOOTS("Поножи"), GLOVES("Перчатки"), SHIELDS("Щиты"), SHOOTING_ARMS(
            "Стрелковое оружие"), VAMBRACES("Наручи"), RINGS("Кольца"), ALL(
            "Все");

    private static EnumSet<Category> CATEGORIES = EnumSet.of(HELMETS, AMULETS,
            ARMOURS, ARMS, BELTS, BOOTS, GLOVES, SHIELDS, SHOOTING_ARMS,
            VAMBRACES, RINGS);

    private String type = null;

    Category(String name) {
        this.type = name;
    }

    public static Category getCategoryByName(String name) {
        for (Category cat : Category.values()) {
            if (cat.name().equals(name))
                return cat;
        }
        return null;
    }

    public static Category getCategoryByType(String type) {
        for (Category cat : Category.values()) {
            if (cat.getType().equals(type))
                return cat;
        }
        return null;
    }

    public static List<String> getTypes() {
        List<String> categories = Lists.newLinkedList();

        for (Category cat : CATEGORIES)
            categories.add(cat.getType());
        return categories;
    }

    public String getType() {
        return type;
    }

}
