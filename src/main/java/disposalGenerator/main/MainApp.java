package disposalGenerator.main;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import disposalGenerator.GUI.MainFrame;
import disposalGenerator.configuration.Constants;

import java.util.Date;

public class MainApp {
    public static void main(String[] args) {

        FlatLaf laf = new FlatDarculaLaf();
        FlatLaf.setup(laf);

        new MainFrame().setVisible(true);



    }
}
