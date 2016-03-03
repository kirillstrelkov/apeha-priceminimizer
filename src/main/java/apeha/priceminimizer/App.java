package apeha.priceminimizer;

import apeha.priceminimizer.gui.MainGUI;

public class App {
    public static void main(String[] args) {
        try {
            MainGUI window = new MainGUI();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
