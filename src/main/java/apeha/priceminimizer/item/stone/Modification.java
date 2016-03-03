package apeha.priceminimizer.item.stone;

import apeha.priceminimizer.common.Utils;
import apeha.priceminimizer.db.DbHandler;
import apeha.priceminimizer.item.Item;
import apeha.priceminimizer.item.Property;
import apeha.priceminimizer.item.fortification.Fortification;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Modification {
    private static int MAX_ALLOWED_STONES_IN_ITEM = 2;

    private Stone stone = null;
    private Facet facet = null;

    public Modification(Stone stone, Facet facet) {
        this.stone = stone;
        this.facet = facet;
    }

    public static List<Modification> getModifications(Item item) {
        return getModifications(item, null);
    }

    private static String getValueDifference(String valOrig, String valMod) {
        Matcher matcher = Pattern.compile(Facet.MOD_PERCENT).matcher(valMod);

        if (valOrig == null) {
            Property property = Property.getPropertyFrom(valMod);
            if (Property.GUARDS_AND_MASTERIES.contains(property))
                valOrig = "+0%";
            else if (Property.GUARDS_OF_ENEMY.contains(property))
                valOrig = "-0%";
            else
                valOrig = "+0";
        }
        int original = Utils.getInteger("\\-*\\d+", valOrig);
        int mod = Utils.getInteger("\\-*\\d+", valMod);
        int diff = Math.abs(mod - original);

        if (matcher.find()) {
            String val = matcher.group();
            return val.substring(0, 1) + diff
                    + val.substring(val.length() - 1, val.length());
        } else {
            matcher = Pattern.compile(Facet.MOD_PLUS_INT).matcher(valMod);
            if (matcher.find()) {
                String val = matcher.group();
                return val.substring(0, 1) + diff;
            }
        }
        return null;
    }

    public static List<String[]> getPropertyDifference(Item original,
                                                       Item modified) {
        return getPropertyDifference(original, modified, null);
    }

    private static String formatMod(Stone stone, Facet facet) {
        Property property = stone.getProperty();
        if (Property.STATS.contains(property))
            return property.getName() + ": +" + facet.getValue();
        else if (Property.GUARDS_OF_ENEMY.contains(property))
            return property.getName() + ": -" + facet.getValue() * 5 + "%";
        else if (Property.GUARDS_AND_MASTERIES.contains(property))
            return property.getName() + ": +" + facet.getValue() * 5 + "%";
        else
            return null;
    }

    public static boolean areEqual(Modification mod1, Modification mod2) {
        if (mod1 == null && mod2 == null)
            return true;
        else if (mod1 != null && mod2 != null)
            return mod1.equals(mod2);
        else
            return false;
    }

    public static List<Modification> getModifications(Item item,
                                                      Fortification fortification) {
        List<Modification> mods = Lists.newLinkedList();
        Item originalItem = null;

        for (Item nextItem : DbHandler.getItems())
            if ((item.getName().contains(nextItem.getName()))) {
                originalItem = nextItem;
                break;
            }

        if (originalItem == null)
            return null;

        List<String[]> propertyDifference = getPropertyDifference(originalItem,
                item, fortification);
        for (String[] next : propertyDifference) {
            if (next[0] == null && next[1] == null)
                continue;

            Stone stone = Stone.getStoneFrom(next[1]);
            String valDiff = getValueDifference(next[0], next[1]);

            if (valDiff == null)
                continue;

            Facet[] facets = Facet.getFacet(valDiff);
            if (facets == null)
                continue;

            for (Facet facet : facets)
                if (stone != null && facet != null)
                    if (mods.size() == MAX_ALLOWED_STONES_IN_ITEM)
                        return mods;
                    else
                        mods.add(new Modification(stone, facet));
        }
        return mods;
    }

    private static List<String[]> getPropertyDifference(Item original,
                                                        Item modified, Fortification fortification) {
        List<String[]> diffProperties = Lists.newLinkedList();
        Map<Property, String> properties = original.getPropertiesAndValues();
        Map<Property, String> propertiesMod = modified.getPropertiesAndValues();

        for (Property next : propertiesMod.keySet()) {
            if ((!next.equals(Property.DURABILITY)
                    && !next.equals(Property.PRICE_IN_SHOP)
                    && !next.equals(Property.PRICE_IN_MARKET) && !next
                    .equals(Property.SHOP))) {
                String val = properties.get(next);
                String valMod = propertiesMod.get(next);

                if (fortification != null) {
                    Property property = fortification.getValue().getProperty();
                    if (property != null && property.equals(next)) {
                        int ivalMod = Property.getInteger(next, valMod)
                                - fortification.getTimes()
                                * Fortification.MULTIPLICAND;

                        valMod = Property.getPropertyValueFrom(Property
                                .formatPropertyByValue(next, ivalMod));
                    }
                }

                if (!properties.containsKey(next)) {
                    String[] origAndMod = {null,
                            Property.formatProperty(next, valMod)};
                    diffProperties.add(origAndMod);
                } else if (!val.equals(valMod)) {
                    String[] origAndMod = {Property.formatProperty(next, val),
                            Property.formatProperty(next, valMod)};
                    diffProperties.add(origAndMod);
                }
            }
        }
        return diffProperties;
    }

    public Stone getStone() {
        return stone;
    }

    public Facet getFacet() {
        return facet;
    }

    @Override
    public String toString() {
        return formatMod(this.stone, this.facet);
    }

    @Override
    public boolean equals(Object obj) {
        Modification mod = (Modification) obj;
        return this.getFacet().equals(mod.getFacet())
                && this.getStone().equals(mod.getStone());
    }
}
