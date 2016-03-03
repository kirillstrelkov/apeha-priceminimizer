package apeha.priceminimizer.db;

import apeha.priceminimizer.common.Category;
import apeha.priceminimizer.item.Item;
import apeha.priceminimizer.item.Property;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class XMLHandler implements ItemHandler {
    protected String dbStream = null;
    protected Document dom;

    public XMLHandler() {
        this.createDOMDoc();
        dbStream = "apehadb.xml";
    }

    public XMLHandler(String pathToDb) {
        this.createDOMDoc();
        dbStream = pathToDb;
    }

    private void createDOMDoc() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
            dom = db.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private TreeMap<Category, List<Item>> getCategoryAndItemsFromDb() {
        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(dbStream);
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }

        if (is == null)
            try {
                is = new FileInputStream(dbStream);
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(is);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Element root = dom.getDocumentElement();
        TreeMap<Category, List<Item>> map = Maps.newTreeMap();

        for (Node typeNode = root.getFirstChild(); typeNode != null; typeNode = typeNode
                .getNextSibling()) {
            String type = typeNode.getNodeName().replaceAll("_", " ");
            List<Item> list = Lists.newLinkedList();

            for (Node nameNode = typeNode.getFirstChild(); nameNode != null; nameNode = nameNode
                    .getNextSibling()) {
                Item item = new Item();
                String name = nameNode.getNodeName();
                name = name.replaceAll("Q", "\"");
                name = name.replaceAll("\\_", "\\ ");
                item.setName(name);
                Map<Property, String> properties = Maps.newTreeMap();

                for (Node statNode = nameNode.getFirstChild(); statNode != null; statNode = statNode
                        .getNextSibling()) {
                    String stat = statNode.getNodeName();
                    stat = stat.replaceAll("\\_", "\\ ");
                    String context = statNode.getTextContent();

                    if (stat.contains("Изображение"))
                        item.setImageSrc(context);
                    else
                        properties.put(Property.getPropertyFrom(stat), context);
                }

                item.setPropertiesAndValues(properties);
                list.add(item);
            }

            map.put(Category.getCategoryByType(type), list);
        }

        return map;
    }

    @Override
    public List<Item> getItems() {
        Map<Category, List<Item>> categoryAndItems = getCategoryAndItems();
        List<Item> items = Lists.newLinkedList();

        for (Category next : categoryAndItems.keySet())
            items.addAll(categoryAndItems.get(next));

        return items;
    }

    @Override
    public Map<Category, List<Item>> getCategoryAndItems() {
        return getCategoryAndItemsFromDb();
    }

}
