package apeha.priceminimizer.gui;

import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import apeha.priceminimizer.item.ModItem;
import apeha.priceminimizer.item.Property;
import apeha.priceminimizer.item.TextParser;
import apeha.priceminimizer.item.stone.Modification;
import apeha.priceminimizer.market.MarketPrice;
import apeha.priceminimizer.webdriver.CommonUtils;

public class ApehaPage {
    private static final String PROBLEMS_WITH_GETTING_MOD_INFO = "Проблемы с получением данных";
    private static final String LINING = "Подкладка";

    private static final String APEHA_URL = "http://www.apeha.ru";

    protected CommonUtils utils;
    private MainGUI gui;

    private String cityUrl;

    public ApehaPage(MainGUI mainGui) {
        utils = new CommonUtils();
        gui = mainGui;
    }

    private void login() {
        String nick = gui.txtNick.getText();
        String passwd = gui.txtPasswrd.getText();
        By nickField = By.cssSelector("#loginform [name=\"login\"]");
        By passField = By.cssSelector("#loginform [name=\"pwd\"]");

        if (utils.isVisible(nickField) && utils.isVisible(passField)) {
            utils.type(nickField, nick);
            utils.type(passField, passwd);
            ((JavascriptExecutor) utils.getDriver()).executeScript("jQuery('#loginform').submit();");
            openWindowInCurrentTab();
        }
    }

    public String[] searchFor(ModItem item, String stone, String facet) {
        String url = cityUrl.concat("itemsearch_stp_3.html");
        if (!utils.getUrl().contains(url))
            utils.goTo(url);
        By eName = By.cssSelector("input[name=\"iname\"]");
        By search = By.cssSelector("input[value=\"Найти\"]");

        String[] found = new String[2];
        found[0] = null;
        found[1] = null;

        String name = item.getName();
        boolean isLining = name.contains(LINING);
        if (isLining) {
            stone = "Любой";
            facet = "0";
        }

        name = name.replaceAll("\\( *мод\\. *\\)", "").replaceAll("\\( *закл\\. *\\)", "").trim();
        if (name.contains("(") && !isLining) {
            int index = name.indexOf("(");
            name = name.substring(0, index).trim();
        }
        utils.type(eName, name);

        if (stone != null) {
            utils.selectByVisibleTextFromDropDown(By.cssSelector("select[name=\"gem\"]"), stone);
        }
        if (facet != null) {
            utils.selectByValueFromDropDown(By.cssSelector("select[name=\"mod\"]"), facet);
        }
        utils.click(search);
        List<ModItem> itemsFromSearch = getItemsFromSearch();
        return getItemWithLowestPrice(item, itemsFromSearch);
    }

    private boolean areItemModsEqual(ModItem item1, ModItem item2) {
        List<Modification> mods1 = Lists.newArrayList();
        List<Modification> mods2 = Lists.newArrayList();

        Modification item1mod1 = item1.getMod1();
        if (item1mod1 != null)
            mods1.add(item1mod1);
        Modification item1mod2 = item1.getMod2();
        if (item1mod2 != null)
            mods1.add(item1mod2);

        Modification item2mod1 = item2.getMod1();
        if (item2mod1 != null)
            mods2.add(item2mod1);
        Modification item2mod2 = item2.getMod2();
        if (item2mod2 != null)
            mods2.add(item2mod2);

        if (mods1.size() != mods2.size())
            return false;

        if (mods1.size() == 0 && mods2.size() == 0)
            return true;

        for (Modification mod : mods1)
            if (!mods2.contains(mod))
                return false;

        for (Modification mod : mods2)
            if (!mods1.contains(mod))
                return false;

        return true;
    }

    private String[] getItemWithLowestPrice(ModItem item, List<ModItem> itemsFound) {
        Map<MarketPrice, ModItem> map = Maps.newLinkedHashMap();
        List<String> foundItems = Lists.newLinkedList();
        // String allItems = "";
        Property priceInMarket = Property.PRICE_IN_MARKET;

        boolean isLining = item.getName().contains(LINING);

        for (ModItem next : itemsFound) {
            Map<Property, String> properties = next.getPropertiesAndValues();
            String priceString = Property.formatProperty(priceInMarket, properties.get(priceInMarket));
            MarketPrice marketPrice = MarketPrice.format(priceString);

            if (areItemModsEqual(item, next))
                map.put(marketPrice, next);

            StringBuilder str = new StringBuilder();
            str.append("\n");
            str.append(next.getName() + "\n");
            str.append(properties.get(priceInMarket) + "\t" + properties.get(Property.SHOP) + "\n");

            if (next.getMod1() != null)
                str.append(next.getMod1() + "\n");
            if (next.getMod2() != null)
                str.append(next.getMod2() + "\n");

            if (isLining) {
                for (Property p : properties.keySet()) {
                    if (p != Property.REQUIRED_LEVEL && p != Property.PRICE_IN_MARKET && p != Property.SHOP) {
                        str.append(Property.formatProperty(p, properties.get(p)) + "\n");
                    }
                }
            }
            String string = str.toString();
            if (!foundItems.contains(string))
                foundItems.add(string);
        }
        List<MarketPrice> priceList = Lists.newLinkedList(map.keySet());
        Collections.sort(priceList);

        String[] found = new String[2];
        String nick = gui.txtNick.getText();
        if (priceList.size() > 0) {
            found[0] = priceList.get(0).toString();
            // System.out.println(item.getName());
            // System.out.println(priceList.toString());
            for (MarketPrice mprice : priceList) {
                // System.out.println(mprice.toString());
                if (!map.get(mprice).getPropertiesAndValues().get(Property.SHOP).contains(nick)) {
                    found[0] = mprice.toString();
                    break;
                }
            }
        } else
            found[0] = PROBLEMS_WITH_GETTING_MOD_INFO;
        Collections.sort(foundItems, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return MarketPrice.formatFromValue(o1).compareTo(MarketPrice.formatFromValue(o2));
            }
        });
        StringBuilder builder = new StringBuilder();
        for (String s : foundItems)
            builder.append(s.toString());
        found[1] = builder.toString();

        // System.out.println(found[0] + found[1]);
        return found;
    }

    private void openWindowInCurrentTab() {
        By link = By.cssSelector("#after_login div[onclick*=newWin]");
        String attribute = utils.getAttribute(utils.findElement(link), "onclick");
        attribute = attribute.replaceAll("newWin\\(\"", "").replaceAll("\",\"main\"\\);", "");
        utils.goTo(attribute);
        cityUrl = attribute.substring(0, attribute.lastIndexOf("/") + 1);
    }

    private void goToMyShop() {
        By enterShop = By.cssSelector("[value=\"Войти\"]");
        By all = By.cssSelector("[value=\"Все\"]");

        if (!utils.isVisible(all)) {
            utils.goTo(cityUrl.concat("market_mode_2.html"));
            utils.click(enterShop);
            utils.click(all);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<ModItem, String> getItemAndMods() {
        List<ModItem> itemsFromShop = getItemsFromShop();
        if (itemsFromShop.size() > 0) {
            Iterator<ModItem> iterator = itemsFromShop.iterator();
            Map<ModItem, String> map = new LinkedHashMap<ModItem, String>();
            while (iterator.hasNext()) {
                ModItem next = iterator.next();
                map.put(next, "");
            }
            return map;
        } else
            return null;
    }

    private List<ModItem> getItemsFromShop() {
        utils.goTo(APEHA_URL);
        login();
        goToMyShop();
        return getItems();
    }

    private List<ModItem> getItemsFromSearch() {
        By items = By.cssSelector("table[class=\"item\"]");
        By nextPage = By.cssSelector("input[value=\"Дальше\"]");
        List<ModItem> items2 = new LinkedList<ModItem>();
        do {
            boolean isVisible = utils.waitForIsVisible(items);
            if (!isVisible)
                return items2;
            WebElement element = utils.findElement(items);
            String itemString = utils.getText(element);
            itemString = itemString.replaceAll("Лавка", "\nЛавка");
            // System.out.println("---------------------\n" + itemString +
            // "---------------------\n");
            items2.addAll(TextParser.getModItems(new StringReader(itemString)));
            if (utils.isVisible(nextPage))
                utils.click(nextPage);
        } while (utils.isVisible(nextPage));
        return items2;
    }

    private List<ModItem> getItems() {
        String itemString = "";
        Iterator<WebElement[]> iterator = getWebItems().iterator();
        while (iterator.hasNext()) {
            WebElement[] next = iterator.next();
            itemString = itemString.concat(utils.getText(next[0]) + "\n" + utils.getText(next[1]) + "\n\n");
        }
        return TextParser.getModItems(new StringReader(itemString));
    }

    private List<WebElement[]> getWebItems() {
        By itemTr = By.cssSelector("table.bordo>tbody>tr:nth-child(2)>td:nth-child(2)>table>tbody>tr");
        List<WebElement[]> webItems = new LinkedList<WebElement[]>();
        utils.waitForVisible(itemTr);
        List<WebElement> elements = utils.findElements(itemTr);
        WebElement[] webItem = new WebElement[2];
        for (int i = 0; i < elements.size(); i++) {
            WebElement element = elements.get(i);
            if (i % 2 == 0) {
                webItem = new WebElement[2];
                webItem[0] = element;
            } else {
                webItem[1] = element;
                webItems.add(webItem);
            }
        }
        return webItems;
    }

    public void changePrice(int index, String minPrice) {
        By changeButton = By.cssSelector("input[value=\"Изменить цену\"]");
        By price = By.cssSelector("input[name=\"price\"]");
        By priceBlues = By.cssSelector("input[name=\"eprice\"]");
        By ok = By.cssSelector("input[value=\"OK\"]");

        String formatedPrice = Property.PRICE_IN_MARKET.getName() + ":" + minPrice;
        MarketPrice newPrice = MarketPrice.format(formatedPrice);

        goToMyShop();
        WebElement webItem = getWebItems().get(index)[1];
        utils.click(utils.findElement(webItem, changeButton));

        WebElement priceElement = utils.findElement(webItem, price);
        utils.type(priceElement, newPrice.getMainPrice());

        if (newPrice.getBluePrice() != null && newPrice.getBluePrice() != "0.00") {
            try {
                WebElement priceBluesElement = utils.findElement(webItem, priceBlues);
                utils.type(priceBluesElement, newPrice.getBluePrice());
            } catch (NoSuchElementException e) {

            }
        }
        utils.click(utils.findElement(webItem, ok, 1));
    }
}
