package apeha.priceminimizer.market;

import apeha.priceminimizer.item.Property;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketPrice implements Comparable<MarketPrice> {
    private String mainPrice = null;
    private String bluePrice = null;

    public static MarketPrice format(String text) {
        if (text == null)
            return null;

        Property property = Property.getPropertyFrom(text);
        String value = Property.getPropertyValueFrom(text);

        if (property != null) {
            MarketPrice price = new MarketPrice();
            Matcher matcher = Pattern.compile("\\d+\\.\\d+").matcher(value);
            if (matcher.find())
                price.setMainPrice(matcher.group());
            if (matcher.find())
                price.setBluePrice(matcher.group());
            return price;
        } else
            return null;
    }

    public static MarketPrice formatFromValue(String value) {
        MarketPrice price = new MarketPrice();
        Matcher matcher = Pattern.compile("\\d+\\.\\d+").matcher(value);
        if (matcher.find())
            price.setMainPrice(matcher.group());
        if (matcher.find())
            price.setBluePrice(matcher.group());
        if (price.getMainPrice() != null)
            return price;
        else
            return null;
    }

    public String getMainPrice() {
        return this.mainPrice;
    }

    private void setMainPrice(String price) {
        this.mainPrice = price;
    }

    public String getBluePrice() {
        return this.bluePrice;
    }

    private void setBluePrice(String price) {
        this.bluePrice = price;
    }

    @Override
    public String toString() {
        String blue = this.getBluePrice();
        if (blue != null)
            return String.format("%s ст. + %s", this.getMainPrice(), blue);
        else
            return String.format("%s ст.", this.getMainPrice());
    }

    @Override
    public int compareTo(MarketPrice price) {
        BigDecimal zero = new BigDecimal("0.00");
        BigDecimal mainInBlues = new BigDecimal("5.00");
        BigDecimal thismain = new BigDecimal(this.getMainPrice());
        BigDecimal pricemain = new BigDecimal(price.getMainPrice());
        BigDecimal thisBlues;
        BigDecimal priceBlues;

        if (this.getBluePrice() != null)
            thisBlues = new BigDecimal(this.getBluePrice());
        else
            thisBlues = zero;
        if (price.getBluePrice() != null)
            priceBlues = new BigDecimal(price.getBluePrice());
        else
            priceBlues = zero;

        return thismain.add(thisBlues.multiply(mainInBlues)).compareTo(
                pricemain.add(priceBlues.multiply(mainInBlues)));
    }
}
