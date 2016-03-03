package apeha.priceminimizer.db;

import apeha.priceminimizer.common.Category;
import apeha.priceminimizer.item.Item;
import apeha.priceminimizer.item.Property;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DbHandler {
    public final static int DB_SIZE;
    public final static String TOP_PRICE;
    public final static int TOP_LEVEL;
    private final static ItemHandler itemHandler;
    private final static Map<Category, List<Item>> categoryAndItems;
    private final static List<Item> items;

    static {
        itemHandler = new XMLHandler();
        categoryAndItems = itemHandler.getCategoryAndItems();
        items = itemHandler.getItems();
        BigDecimal price = BigDecimal.ZERO;
        int topLevel = 0;

        for (Item item : items) {
            String level = item.getPropertiesAndValues().get(
                    Property.REQUIRED_LEVEL);
            String iprice = item.getPropertiesAndValues().get(
                    Property.PRICE_IN_SHOP);

            try {
                int ilevel = Integer.parseInt(level);
                if (ilevel > topLevel)
                    topLevel = ilevel;
            } catch (Exception e) {
            }

            BigDecimal bd = new BigDecimal(iprice);
            if (bd.compareTo(price) > 0)
                price = bd;
        }

        TOP_PRICE = price.toString();
        TOP_LEVEL = topLevel;
        DB_SIZE = items.size();
    }

    public static ItemHandler getHandler() {
        return itemHandler;
    }

    public static Map<Category, List<Item>> getCategoryAndItems() {
        return categoryAndItems;
    }

    public static List<Item> getItems() {
        return items;
    }

    public static Item getItemByName(String name) {
        for (Item item : getItems())
            if (item.getName().equals(name))
                return item;
        return null;
    }

    public static Category getCategoryByItemName(String name) {
        Set<Category> keySet = getCategoryAndItems().keySet();
        for (Category cat : keySet) {
            List<Item> list = getCategoryAndItems().get(cat);
            for (Item item : list)
                if (item.getName().equals(name))
                    return cat;
        }
        return null;
    }

}
