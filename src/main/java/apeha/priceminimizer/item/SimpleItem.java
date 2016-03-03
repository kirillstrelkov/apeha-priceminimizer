package apeha.priceminimizer.item;

public class SimpleItem implements Comparable<Object> {
    protected String name;
    private String properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object obj) {
        return this.compareTo((SimpleItem) obj) == 0;
    }

    @Override
    public int compareTo(Object o) {
        SimpleItem si = (SimpleItem) o;
        if (this.getName().equals(si.getName()) && this.getProperties().equals(si.getProperties()))
            return 0;
        else
            return this.getName().compareTo(si.getName());
    }

    @Override
    public String toString() {
        return this.getName() + "\n" + this.getProperties();
    }
}
