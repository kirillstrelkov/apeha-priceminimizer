package apeha.priceminimizer.item.spell;

import java.util.Date;

public interface Spellable {
    void spell(int days);

    Date getSpellStartDate();

    boolean isSpellable();

    boolean isLessThen24hAlive();

    boolean isAlive();

    String getTimeLeft();
}
