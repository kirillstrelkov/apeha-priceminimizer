package apeha.priceminimizer.common;

import java.util.List;

public interface CommonTransmissible<E> {
    List<E> getList();

    void updateTable(E list);
}
