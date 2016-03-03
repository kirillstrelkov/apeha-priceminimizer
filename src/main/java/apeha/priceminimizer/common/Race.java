package apeha.priceminimizer.common;

public enum Race {
    ELF("Эльф"),
    ORC("Орк"),
    DWARF("Гном"),
    HOBBIT("Хоббит"),
    DRAGON("Дракон"),
    HUMAN("Человек");

    private String name;

    Race(String name) {
        this.name = name;
    }

    public static Race getRaceFrom(String text) {
        Race[] values = Race.values();
        for (Race race : values) {
            if (text.contains(race.toString()))
                return race;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
