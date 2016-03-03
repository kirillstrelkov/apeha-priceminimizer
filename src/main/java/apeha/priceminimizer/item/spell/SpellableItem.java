package apeha.priceminimizer.item.spell;

import apeha.priceminimizer.common.Utils;
import apeha.priceminimizer.item.Item;
import apeha.priceminimizer.item.Property;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SpellableItem extends Item implements Spellable {
    private Date date = null;

    public SpellableItem(Item item) {
        super();
        this.setName(item.getName());
        this.setImageSrc(item.getImageSrc());
        Map<Property, String> propertiesAndValues = item
                .getPropertiesAndValues();
        if (propertiesAndValues.containsKey(Property.SPELLED)) {
            String value = propertiesAndValues.get(Property.SPELLED);
            this.date = Utils.getDateFrom(value);
            propertiesAndValues.remove(Property.SPELLED);
        }
        this.setPropertiesAndValues(propertiesAndValues);
        this.setProperties(item.getProperties());
    }

    @Override
    public String getProperties() {
        return super.getProperties() + "\n"
                + Property.formatProperty(Property.SPELLED, this.getTimeLeft());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void spell(int days) {
        Calendar cDate = Calendar.getInstance();
        cDate.setTime(new Date());
        cDate.add(Calendar.DAY_OF_YEAR, days);
        this.setDate(cDate.getTime());
    }

    @Override
    public Date getSpellStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.getDate());
        calendar.add(Calendar.DAY_OF_YEAR, -5);
        return calendar.getTime();
    }

    @Override
    public boolean isSpellable() {
        Calendar end = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        end.setTime(this.getDate());
        current.setTime(new Date());
        long difference = end.getTimeInMillis() - current.getTimeInMillis();
        return difference < 432000000l && this.isAlive();
    }

    @Override
    public boolean isLessThen24hAlive() {
        Calendar itemDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        itemDate.setTime(this.getDate());
        long dif = itemDate.getTimeInMillis() - currentDate.getTimeInMillis();
        int days = (int) (dif / 86400000l);
        return days == 0;
    }

    @Override
    public boolean isAlive() {
        Calendar end = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        end.setTime(this.getDate());
        current.setTime(new Date());
        long difference = end.getTimeInMillis() - current.getTimeInMillis();
        return difference > 0l;
    }

    @Override
    public String getTimeLeft() {
        Calendar itemDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        itemDate.setTime(this.getDate());
        long dif = itemDate.getTimeInMillis() - currentDate.getTimeInMillis();
        int days = (int) (dif / 86400000l);
        if (days != 0)
            dif = dif - 86400000l * days;
        int hours = (int) (dif / 3600000l);
        if (hours != 0)
            dif = dif - 3600000l * hours;
        int mins = (int) (dif / 60000l);
        return String.format("%dд %dч %dмин", days, hours, mins);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpellableItem))
            return false;

        SpellableItem item = (SpellableItem) obj;
        return item.toString().equals(obj.toString());
    }

}
