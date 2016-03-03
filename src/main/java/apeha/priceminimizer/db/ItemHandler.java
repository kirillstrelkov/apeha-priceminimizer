package apeha.priceminimizer.db;

import apeha.priceminimizer.common.Category;
import apeha.priceminimizer.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemHandler {
    List<Item> getItems();

    Map<Category, List<Item>> getCategoryAndItems();
}
