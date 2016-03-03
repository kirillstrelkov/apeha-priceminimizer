package apeha.priceminimizer.item;

import apeha.priceminimizer.item.stone.Stone;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    // private static final String NAME_REGEXP = "[А-Я]{1}[а-яёА-Я\"\\ -]+";
    // original
    private static final String MOD_OR_SPEL_REGEXP = "(\\(мод\\.\\))|(\\(закл\\.\\))";
    private static final String NAME_REGEXP = "[а-яёА-Я\"\\ \\-]+";
    public static final Pattern NAME_LINE_PATTERN = Pattern.compile("^"
            + NAME_REGEXP + "$");
    public static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEXP);
    private static final String COMPLEX_NAME_REGEXP = "([~!\\@#$%^&\\*\\(\\)\\?]+)|(\\w+)";
    private static final Pattern MOD_OR_SPELL_PATTERN = Pattern
            .compile(MOD_OR_SPEL_REGEXP);
    private static final Pattern COMPLEX_NAME_PATTERN = Pattern
            .compile(COMPLEX_NAME_REGEXP);

    public static String getName(String line) {
        line = line.trim();
        Matcher words = NAME_LINE_PATTERN.matcher(line);
        Matcher modOrSpell = MOD_OR_SPELL_PATTERN.matcher(line);
        Matcher complexName = COMPLEX_NAME_PATTERN.matcher(line);
        if (areForbiddenWordsIn(line))
            return null;
        else if (Stone.getStoneFrom(line) != null)
            return null;
        else if (modOrSpell.find())
            return line;
        else if (words.find())
            return words.group();
        else if (complexName.find() && Property.getPropertyFrom(line) == null)
            return line;
        else
            return null;
    }

    public static List<String> getListOfItemStrings(Reader reader) {
        List<String> list = new LinkedList<String>();
        LineNumberReader lineReader = new LineNumberReader(reader);
        String itemString = null;
        try {
            String line = null;
            while ((line = lineReader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    String name = TextParser.getName(line);
                    if (name != null) {
                        if (itemString != null)
                            list.add(itemString);
                        itemString = name + "\n";
                        // System.out.println("new item: " + line);
                    } else if (Property.getPropertyFrom(line) != null) {
                        // System.out.println("adding " + line);
                        itemString = itemString.concat(line + "\n");
                    } else
                        ;
                    // System.out.println("Skipping: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                lineReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (itemString != null)
            list.add(itemString);
        return list;
    }

    private static boolean areForbiddenWordsIn(String line) {
        return line.equals("Количество") || line.contains("количество:")
                || line.contains("Количество:")
                || line.contains(Property.TWO_HANDED.getName())
                || line.contains("[") || line.contains("]")
                || line.equals("Игрок")
                || line.contains("Государственная вставка камней")
                || line.contains("Удаление модификаций")
                || line.contains("урон:")
                || line.contains("Стоимость удаления мода")
                || line.contains("Кузница") || line.contains("Ваша наличность")
                || line.contains("Дней между передачами")
                // || line.contains("Государственная вставка камней")
                || line.contains("син.ст.")
                // || line.contains("ограненный")
                || line.contains("Время жизни");

    }

    // TODO use TextFilesHandler
    public static List<ModItem> getModItems(Reader reader) {
        Iterator<String> iterator = getListOfItemStrings(reader).iterator();
        List<ModItem> items = new LinkedList<ModItem>();
        while (iterator.hasNext()) {
            String itemString = iterator.next();
            ModItem item = ItemBuilder.createModItem(itemString);
            if (item != null)
                items.add(item);
        }
        return items;
    }

    public static List<Item> getItems(Reader reader) {
        Iterator<String> iterator = getListOfItemStrings(reader).iterator();
        List<Item> items = new LinkedList<Item>();
        while (iterator.hasNext()) {
            String itemString = iterator.next();
            Item item = ItemBuilder.createItem(itemString);
            if (item != null)
                items.add(item);
        }
        return items;
    }
}
