package apeha.priceminimizer.item.stone;

import apeha.priceminimizer.common.Utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Facet {
    FACET_1(1), FACET_3(3), FACET_5(5), FACET_7(7), FACET_9(9), FACET_11(11), FACET_15(
            15), FACET_19(19), FACET_23(23);

    public static String MOD_PERCENT = "[\\+\\-]\\d+%";
    public static String MOD_PLUS_INT = "\\+\\d+";
    private int value = 0;

    Facet(int value) {
        this.value = value;
    }

    public static Facet[] getFacet(String text) {
        Matcher matcher = Pattern.compile(MOD_PERCENT).matcher(text);
        int val = -1;
        if (matcher.find())
            val = Utils.getInteger("\\d+", matcher.group()) / 5;
        else {
            matcher = Pattern.compile(MOD_PLUS_INT).matcher(text);
            if (matcher.find()) {
                val = Utils.getInteger("\\d+", matcher.group());
            }
        }
        if (val != -1) {
            for (Facet facet : Facet.values()) {
                if (facet.getValue() == val)
                    return new Facet[]{facet};
            }
            return getFacetWithCombinations(val);
        }
        return null;
    }

    private static Facet[] getFacetWithCombinations(int val) {
        Facet[] values = Facet.values();
        List<Facet[]> combinations = new LinkedList<Facet[]>();
        for (int i = 0; i < values.length; i++)
            for (int j = 0; j < values.length; j++)
                if (values[i].getValue() + values[j].getValue() == val)
                    combinations.add(new Facet[]{values[i], values[j]});
        if (combinations.size() == 0)
            return null;
        else
            return getCombinationsWithLowestDiff(combinations);
    }

    private static Facet[] getCombinationsWithLowestDiff(
            List<Facet[]> combinations) {
        Facet[] lowest = null;
        Iterator<Facet[]> iterator = combinations.iterator();
        while (iterator.hasNext()) {
            Facet[] next = iterator.next();
            if (lowest == null)
                lowest = next;
            else {
                int nextVal = next[0].getValue();
                int nextVal2 = next[1].getValue();
                int lowVal = lowest[0].getValue();
                int lowVal2 = lowest[1].getValue();
                if (Math.abs(nextVal - nextVal2) < Math.abs(lowVal - lowVal2))
                    lowest = next;
            }
        }
        return lowest;
    }

    public int getValue() {
        return value;
    }
}
