package apeha.priceminimizer.item;

import apeha.priceminimizer.item.stone.Modification;

import java.util.List;
import java.util.Map;

public class ModItem extends Item {
    private Modification mod1 = null;
    private Modification mod2 = null;

    public ModItem(Item item) {
        super();
        this.setName(item.getName());
        this.setImageSrc(item.getImageSrc());
        Map<Property, String> propertiesAndValues = item
                .getPropertiesAndValues();
        this.setPropertiesAndValues(propertiesAndValues);
        this.setProperties(item.getProperties());
    }

    public void setMods(List<Modification> mods) {
        if (mods.size() > 0) {
            this.mod1 = mods.get(0);
            if (mods.size() > 1)
                this.mod2 = mods.get(1);
        }

    }

    public Modification getMod1() {
        return mod1;
    }

    public Modification getMod2() {
        return mod2;
    }

    @Override
    public boolean equals(Object obj) {
        ModItem modItem = (ModItem) obj;
        Map<Property, String> thisProps = this.getPropertiesAndValues();
        Map<Property, String> objProps = modItem.getPropertiesAndValues();
        boolean areEqual = this.getName().equals(modItem.getName())
                && thisProps.equals(objProps);
        return areEqual && Modification.areEqual(this.getMod1(), modItem.getMod1())
                && Modification.areEqual(this.getMod2(), modItem.getMod2());
//		if (this.getMod1() == null && modItem.getMod1() == null)
//			return areEqual && (this.getMod2() == null)
//					&& (modItem.getMod2() == null);
//		else if (this.getMod1() != null && modItem.getMod1() != null) {
//			boolean areMod1Equal = this.getMod1().equals(modItem.getMod1());
//			if (this.getMod2() == null && modItem.getMod2() == null)
//				return areEqual && areMod1Equal;
//			else if (this.getMod2() != null && modItem.getMod2() != null) {
//				boolean areMod2Equal = this.getMod2().equals(modItem.getMod2());
//				return areEqual && areMod1Equal && areMod2Equal;
//			} else
//				return false;
//		} else
//			return false;
    }

}
