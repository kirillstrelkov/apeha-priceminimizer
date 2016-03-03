package apeha.priceminimizer.gui;

import apeha.priceminimizer.item.ModItem;
import apeha.priceminimizer.item.Property;
import apeha.priceminimizer.item.stone.Modification;
import apeha.priceminimizer.market.MarketPrice;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

public class GuiHelp {
    public static final String ITEMS_INFO = "Всего вещей: %s\t"
            + "Прибыль после продажи: %s + %s / %s + %s";
    public static final String INITIAL_HELP = "Программа не поддерживает "
            + "Флеш пароль.\n" + "Программа использует браузер Firefox.\n" + "";
    protected List<Button> checkBoxes = Lists.newArrayList();
    protected List<Text> texts = Lists.newArrayList();
    private MainGUI gui;
    private ApehaPage page;

    public GuiHelp(MainGUI mainGUI) {
        page = new ApehaPage(mainGUI);
        gui = mainGUI;
    }

    public void fillTableWithItems() {
        try {
            checkBoxes = Lists.newArrayList();
            texts = Lists.newArrayList();
            gui.itemsAndModsInfo = page.getItemAndMods();
            if (gui.itemsAndModsInfo != null) {
                int itemIndex = 1;
                Set<ModItem> modItems = gui.itemsAndModsInfo.keySet();
                for (ModItem modItem : modItems) {
                    fillTableRow(itemIndex, modItem);
                    itemIndex++;
                }
            }
        } catch (Exception e) {
            setException(e, "Ошибка при получении вещей");
        }
    }

    private void setException(Exception e, String header) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        String exception = header + "\n" + e + "\n";
        for (StackTraceElement stack : stackTrace) {
            exception = exception.concat(stack.toString() + "\n");
        }
        gui.txtInfo.setText(exception);
    }

    private void fillTableRow(final int index, final ModItem item) {
        TableItem tableItem = new TableItem(gui.table, SWT.NONE);
        tableItem.setText(0, String.valueOf(index));
        tableItem.setText(1, item.getName());
        tableItem.setText(2, item.getPropertiesAndValues().get(Property.PRICE_IN_MARKET));
        TableEditor editor = new TableEditor(gui.table);
        editor.grabHorizontal = true;
        editor.minimumWidth = 10;
        Button btn = new Button(gui.table, SWT.CHECK);
        btn.setText("Изменить");
        btn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Iterator<Button> iterator = checkBoxes.iterator();
                boolean selection = gui.btnSetMinimalPrices.getEnabled();
                int checkedWidgets = 0;
                while (iterator.hasNext()) {
                    Button next = iterator.next();
                    if (!selection && next.getSelection()) {
                        gui.btnSetMinimalPrices.setEnabled(true);
                        break;
                    } else if (selection && !next.getSelection()) {
                        checkedWidgets++;
                    }
                }
                if (checkedWidgets == checkBoxes.size())
                    gui.btnSetMinimalPrices.setEnabled(false);

                int index = checkBoxes.indexOf(e.widget);
                if (index >= 0 && gui.table.getSelectionIndex() != index) {
                    // gui.table.deselectAll();
                    gui.table.setSelection(index);
                    gui.table.notifyListeners(SWT.Selection, new Event());
                }
            }

        });
        checkBoxes.add(btn);
        btn.pack();
        editor.setEditor(btn, tableItem, 4);

        editor = new TableEditor(gui.table);
        editor.grabHorizontal = true;
        final Text text = new Text(gui.table, SWT.SINGLE);
        texts.add(text);
        text.pack();
        editor.setEditor(text, tableItem, 3);
    }

    public void searchForPricesAndFill() {
        List<ModItem> items = Lists.newArrayList(gui.itemsAndModsInfo.keySet());
        Map<ModItem, String[]> map = Maps.newLinkedHashMap();

        for (ModItem next : getEniqueItemList()) {
            String[] found = searchForLowestPrice(next);
            map.put(next, found);
        }

        for (int i = 0; i < items.size(); i++) {
            ModItem next = items.get(i);
            String[] found = map.get(next);
            if (found == null) {
                for (ModItem item : map.keySet()) {
                    if (next.equals(item)) {
                        found = map.get(item);
                        break;
                    }
                }
            }
            if (found != null) {
                try {
                    setLowestPice(next, i, found[0]);
                    gui.itemsAndModsInfo.put(next, found[1]);
                } catch (Exception e) {
                    setException(e, "Ошибка при получении минимальных цен.");
                }
            }

            if (texts.get(i).getText().equals("")) {
                texts.get(i).setText("Данные не найдены");
            }
        }
    }

    private void setLowestPice(ModItem item, int i, String newValue) {
        Property priceInMarket = Property.PRICE_IN_MARKET;
        String value = item.getPropertiesAndValues().get(priceInMarket);
        MarketPrice price = MarketPrice.format(Property.formatProperty(
                priceInMarket, value));
        MarketPrice newPrice = MarketPrice.format(Property.formatProperty(
                priceInMarket, newValue));
        if (newPrice == null) {
            newPrice = MarketPrice.format(Property.formatProperty(
                    priceInMarket, value));
        }

        if (price.compareTo(newPrice) != 0)
            texts.get(i).setForeground(
                    Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        else
            texts.get(i).setForeground(
                    Display.getCurrent().getSystemColor(
                            SWT.COLOR_WIDGET_FOREGROUND));
        texts.get(i).setText(newValue);
    }

    private List<ModItem> getEniqueItemList() {
        List<ModItem> items = new ArrayList<ModItem>(
                gui.itemsAndModsInfo.keySet());

        List<ModItem> noDuplicate = new ArrayList<ModItem>();
        Iterator<ModItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            ModItem next = iterator.next();
            if (!noDuplicate.contains(next))
                noDuplicate.add(next);
        }
        items = noDuplicate;
        return items;
    }

    private String[] searchForLowestPrice(ModItem next) {
        Modification mod1 = next.getMod1();
        String[] found = searchFor(next, mod1);
        Modification mod2 = next.getMod2();
        if (mod1 != null && mod2 != null && !mod1.equals(mod2)) {
            String addInfoMod2 = searchFor(next, mod2)[1];
            if (!addInfoMod2.equals("")) {
                found[1] = found[1].concat("\n" + addInfoMod2);
            }
        }
        return found;
    }

    private String[] searchFor(ModItem next, Modification mod) {
        String facet = null;
        String stone = null;
        if (mod != null) {
            stone = mod.getStone().getName() + " ограненный";
            facet = String.valueOf(mod.getFacet().getValue());
        } else
            stone = "Нет камня";
        return page.searchFor(next, stone, facet);
    }

    public void closeBrowser() {
        page.utils.closeDriver();
    }

    public void setMinimalPrices() {
        try {
            List<ModItem> items = new ArrayList<ModItem>(
                    gui.itemsAndModsInfo.keySet());
            // System.out.println(items.size() + " " +checkBoxes.size());
            for (int i = 0; i < items.size(); i++) {
                boolean toChange = checkBoxes.get(i).getSelection();
                if (toChange) {
                    // ModItem item = items.get(i);
                    String minPrice = texts.get(i).getText();
                    // System.out.println(item.getName() + "\t" + toChange +
                    // "\t" +
                    // minPrice);
                    page.changePrice(i, minPrice);
                }
            }
        } catch (Exception e) {
            setException(e, "Ошибка при изменении цен.");
        }
    }
}
