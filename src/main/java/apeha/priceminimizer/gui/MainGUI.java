package apeha.priceminimizer.gui;

import apeha.priceminimizer.item.ModItem;
import apeha.priceminimizer.item.Property;
import apeha.priceminimizer.item.stone.Modification;
import apeha.priceminimizer.market.MarketPrice;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class MainGUI {
    protected Text txtNick;
    protected Text txtPasswrd;
    protected Button btnSetMinimalPrices;
    protected Table table;
    protected Text txtInfo;
    protected Text txtFooter;
    protected Text txtModInfo;
    protected Map<ModItem, String> itemsAndModsInfo = new LinkedHashMap<ModItem, String>();
    private Shell shell;
    private Label lNick;
    private Label lPassword;
    private Button btnGetItems;
    private Button btnGetMinimalPrices;
    private GuiHelp help;

    public MainGUI() {
        help = new GuiHelp(this);
    }

    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    protected void createContents() {
        if (shell == null)
            shell = new Shell();
        shell.setText("Помощник лавочника");
        shell.addListener(SWT.Close, new Listener() {

            @Override
            public void handleEvent(Event event) {
                help.closeBrowser();
            }
        });

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        shell.setLayout(layout);

        GridData gdGrabAll = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 6);

        table = new Table(shell, SWT.SINGLE | SWT.BORDER
                | SWT.FULL_SELECTION);
        String[] titles = {"#", "Название предмета", "Моя цена",
                "Минимальная цена", "Изменить?"};

        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
            table.getColumn(i).pack();
            if (i == titles.length - 1)
                column.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        if (help.checkBoxes.size() > 0) {
                            boolean selectionToSet = !help.checkBoxes.get(0)
                                    .getSelection();
                            Iterator<Button> iterator = help.checkBoxes
                                    .iterator();
                            while (iterator.hasNext()) {
                                iterator.next().setSelection(selectionToSet);
                            }
                            btnSetMinimalPrices.setSelection(selectionToSet);
                        }
                    }
                });
        }
        table.pack();
        table.setHeaderVisible(true);
        table.setLayoutData(gdGrabAll);
        table.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                int index = table.getSelectionIndex();
                ArrayList<ModItem> items = new ArrayList<ModItem>(
                        itemsAndModsInfo.keySet());
                ModItem modItem = items.get(index);
                txtInfo.setText(modItem.toString());

                Modification mod1 = modItem.getMod1();
                Modification mod2 = modItem.getMod2();
                String info = "";
                if (mod1 == null && mod2 == null)
                    info = info.concat("Нет модов\n");
                else {
                    info = info.concat("Моды:\n");
                    if (mod1 != null)
                        info = info.concat(mod1.toString() + "\n");

                    if (mod2 != null)
                        info = info.concat(mod2.toString() + "\n");

                }

                String addInfo = itemsAndModsInfo.get(modItem);
                // System.out.println(addInfo);
                if (addInfo != null && addInfo.length() > 0) {
                    info = info.concat("\nНайденные моды и цены на них:\n");
                    info = info.concat(addInfo);
                }
                txtModInfo.setText(info);
            }
        });

        createGroupInfo();
        createGroupModInfo();
        createGroupPlayer();

        btnGetItems = new Button(shell, SWT.PUSH);
        btnGetItems.setText("Получить вещи из своей лавки");
        GridData gdFillCenter = new GridData(SWT.FILL, SWT.CENTER, false, false);
        btnGetItems.setLayoutData(gdFillCenter);
        btnGetItems.setEnabled(false);
        btnGetItems.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (table.getItemCount() > 0) {
                    for (Text text : help.texts) {
                        text.dispose();
                    }
                    for (Button btn : help.checkBoxes) {
                        btn.dispose();
                    }
                    table.removeAll();
                }

                help.fillTableWithItems();

                if (table.getItemCount() > 0) {
                    btnGetMinimalPrices.setEnabled(true);
                } else {
                    btnGetMinimalPrices.setEnabled(false);
                }

                updateProfit();
            }

        });

        btnGetMinimalPrices = new Button(shell, SWT.PUSH);
        btnGetMinimalPrices.setText("Получить минимальные цены на рынке");
        btnGetMinimalPrices.setLayoutData(gdFillCenter);
        btnGetMinimalPrices.setEnabled(false);
        btnGetMinimalPrices.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                help.searchForPricesAndFill();
            }

        });

        btnSetMinimalPrices = new Button(shell, SWT.PUSH);
        btnSetMinimalPrices.setText("Изменить цены в моей лавке");
        btnSetMinimalPrices.setLayoutData(gdFillCenter);
        btnSetMinimalPrices.setEnabled(false);
        btnSetMinimalPrices.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                help.setMinimalPrices();
            }

        });

        txtFooter = new Text(shell, SWT.READ_ONLY | SWT.SINGLE | SWT.LEFT);
        txtFooter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                2, 1));
        txtFooter.setText(String.format(GuiHelp.ITEMS_INFO, "0", "0.00",
                "0.00", "0.00", "0.00"));

        shell.pack();
        shell.setSize(1000, 600);
    }

    private void updateProfit() {
        BigDecimal profit90 = new BigDecimal("0.00");
        BigDecimal profit85 = new BigDecimal("0.00");
        BigDecimal profitBlues90 = new BigDecimal("0.00");
        BigDecimal profitBlues85 = new BigDecimal("0.00");
        BigDecimal multy90 = new BigDecimal("0.90");
        BigDecimal multy85 = new BigDecimal("0.85");

        Property priceInMarket = Property.PRICE_IN_MARKET;
        List<ModItem> items = new ArrayList<ModItem>(itemsAndModsInfo.keySet());
        int count = items.size();
        for (int i = 0; i < count; i++) {
            try {
                String string = Property.formatProperty(priceInMarket, items
                        .get(i).getPropertiesAndValues().get(priceInMarket));
                MarketPrice price = MarketPrice.format(string);
                if (price != null) {
                    String mainPrice = price.getMainPrice();
                    String bluePrice = price.getBluePrice();
                    if (mainPrice != null) {
                        profit90 = profit90.add(new BigDecimal(mainPrice).multiply(
                                multy90).setScale(2, BigDecimal.ROUND_HALF_UP));
                        profit85 = profit85.add(new BigDecimal(mainPrice).multiply(
                                multy85).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                    if (bluePrice != null) {
                        profitBlues90 = profitBlues90.add(new BigDecimal(bluePrice)
                                .multiply(multy90).setScale(2,
                                        BigDecimal.ROUND_HALF_UP));
                        profitBlues85 = profitBlues85.add(new BigDecimal(bluePrice)
                                .multiply(multy85).setScale(2,
                                        BigDecimal.ROUND_HALF_UP));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        txtFooter.setText(String.format(GuiHelp.ITEMS_INFO,
                String.valueOf(count), profit90.toString(),
                profitBlues90.toString(), profit85.toString(),
                profitBlues85.toString()));
    }

    private void createGroupModInfo() {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);

        Group groupModInfo = new Group(shell, SWT.SHADOW_IN);
        groupModInfo.setLayoutData(gridData);
        groupModInfo.setLayout(new FillLayout());
        groupModInfo.setText("Информация о модах:");
        txtModInfo = new Text(groupModInfo, SWT.MULTI | SWT.READ_ONLY
                | SWT.H_SCROLL | SWT.V_SCROLL);
    }

    private void createGroupInfo() {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);

        Group groupInfo = new Group(shell, SWT.SHADOW_IN);
        groupInfo.setLayoutData(gridData);
        groupInfo.setLayout(new FillLayout());
        groupInfo.setText("Информация о предмете:");
        txtInfo = new Text(groupInfo, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                | SWT.V_SCROLL);
        txtInfo.setText(GuiHelp.INITIAL_HELP);
    }

    private void createGroupPlayer() {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);

        Group groupPlayer = new Group(shell, SWT.SHADOW_IN);
        groupPlayer.setLayout(new GridLayout(2, true));
        groupPlayer.setLayoutData(gridData);
        groupPlayer.setText("Информация об игроке:");
        lNick = new Label(groupPlayer, SWT.RIGHT);
        lNick.setText("Ник:");
        lNick.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        txtNick = new Text(groupPlayer, SWT.SINGLE);
        txtNick.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtNick.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String nick = txtNick.getText();
                String passwd = txtPasswrd.getText();
                if (nick.length() > 0 && passwd.length() > 0)
                    btnGetItems.setEnabled(true);
                else
                    btnGetItems.setEnabled(false);
            }

        });

        lPassword = new Label(groupPlayer, SWT.RIGHT);
        lPassword.setText("Пароль:");
        lPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        txtPasswrd = new Text(groupPlayer, SWT.SINGLE | SWT.PASSWORD);
        txtPasswrd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtPasswrd.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String nick = txtNick.getText();
                String passwd = txtPasswrd.getText();
                if (nick.length() > 0 && passwd.length() > 0)
                    btnGetItems.setEnabled(true);
                else
                    btnGetItems.setEnabled(false);
            }
        });
    }

}
