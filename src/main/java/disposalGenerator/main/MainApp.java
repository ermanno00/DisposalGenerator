package disposalGenerator.main;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import disposalGenerator.gui.MainFrame;

public class MainApp {
    public static void main(String[] args) {

        FlatLaf laf = new FlatDarculaLaf();
        FlatLaf.setup(laf);

        new MainFrame().setVisible(true);


    }
}
