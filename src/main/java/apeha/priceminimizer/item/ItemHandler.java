package apeha.priceminimizer.item;

import apeha.priceminimizer.common.Category;

import java.util.List;
import java.util.Map;

public interface ItemHandler {
    List<Item> getItems();

    Map<Category, List<Item>> getCategoryAndItems();
}
