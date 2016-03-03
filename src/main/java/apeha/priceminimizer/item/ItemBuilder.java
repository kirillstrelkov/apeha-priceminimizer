package apeha.priceminimizer.item;

import apeha.priceminimizer.item.fortification.Fortification;
import apeha.priceminimizer.item.spell.SpellableItem;
import apeha.priceminimizer.item.stone.Modification;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ItemBuilder {

    public static ModItem createModItem(String text) {
        Item item = createItem(text);
        Fortification fortification = Fortification.getFortification(text);
        ModItem modItem = new ModItem(item);
        List<Modification> modifications = Modification.getModifications(item, fortification);
        if (modifications != null)
            modItem.setMods(modifications);
        return modItem;
    }

    public static SpellableItem createSpellableItem(String text) {
        Item item = createItem(text);
        SpellableItem spellableItem = new SpellableItem(item);
        if (spellableItem.getDate() != null)
            return spellableItem;
        else
            return null;
    }

    public static Item createItem(String text) {
        Item item = new Item();
        Map<Property, String> properties = Maps.newTreeMap();
        LineNumberReader reader = new LineNumberReader(new StringReader(text));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String name = TextParser.getName(line);
                Property property = Property.getPropertyFrom(line);
                String propValue = Property.getPropertyValueFrom(line);
                if (name != null)
                    item.setName(name);
                else if (property != null && propValue != null)
                    properties.put(property, propValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        item.setPropertiesAndValues(properties);
        return item;
    }

    public static SimpleItem createSimpleItem(String text) {
        SimpleItem item = new SimpleItem();
        String properties = "";
        LineNumberReader reader = new LineNumberReader(new StringReader(text));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String property = Property.formatProperty(line);
                if (property != null)
                    properties = properties.concat(property + "\n");
                String name = TextParser.getName(line);
                if (name != null)
                    item.setName(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        item.setProperties(properties.trim());
        return item;
    }

    public static SpellableItem createSpellableItem(String itemString, Date date) {
        Item item = createItem(itemString);
        SpellableItem spellableItem = new SpellableItem(item);
        spellableItem.setDate(date);
        return spellableItem;
    }

}
