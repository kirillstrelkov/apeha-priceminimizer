package apeha.priceminimizer.common;

import apeha.priceminimizer.item.Item;
import apeha.priceminimizer.item.SimpleItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd.MM.yyyy hh:mm");

    public static <T extends SimpleItem> T getItemFromList(String name,
                                                           List<T> list) {
        for (T next : list) {
            if (next.getName().equals(name))
                return next;
        }
        return null;
    }

    public static int getInteger(String pattern, String text) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);

        if (matcher.find()) {
            String group = matcher.group();
            matcher = Pattern.compile("\\-*\\d+").matcher(group);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            } else
                return -1;
        } else
            return -1;
    }

    public static Date getDateFrom(String text) {
        int days = getInteger("\\d+д", text);
        int hours = getInteger("\\d+ч", text);
        int mins = getInteger("\\d+мин", text);

        if (days == -1)
            days = 0;
        if (hours == -1)
            hours = 0;
        if (mins == -1)
            mins = 0;
        if (days == 0 && hours == 0 && mins == 0)
            return null;
        else {
            Calendar cDate = Calendar.getInstance();

            cDate.setTime(new Date());
            cDate.add(Calendar.DAY_OF_YEAR, days);
            cDate.add(Calendar.HOUR, hours);
            cDate.add(Calendar.MINUTE, mins);

            return cDate.getTime();
        }
    }

    public static String formatStringFrom(Date date) {
        return dateFormat.format(date);
    }

    public static Date formatDateFrom(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Category, List<Item>> getSortedCategoryAndItem(
            Map<Category, List<Item>> map) {
        Map<Category, List<Item>> sortedMap = Maps.newTreeMap();
        List<String> listCat = Lists.newLinkedList();

        for (Category key : map.keySet())
            listCat.add(key.getType());

        Collections.sort(listCat);

        for (String next : listCat)
            for (Category key : map.keySet()) {
                if (key.getType().equals(next)) {
                    List<Item> list = map.get(key);

                    Collections.sort(list);

                    sortedMap.put(key, list);
                    break;
                }
            }

        return sortedMap;
    }

}
