package com.jsql.view.swing.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;

public class ActionCheckUpdate implements ActionListener, Runnable {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ActionCheckUpdate.class);

    @Override
    public void run() {
        try {
            LOGGER.info(I18n.get("UPDATE_LOADING"));
            URLConnection con = new URL("https://raw.githubusercontent.com/ron190/jsql-injection/master/.version").openConnection();
            con.setReadTimeout(60000);
            con.setConnectTimeout(60000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line, pageSource = "";
            while ((line = reader.readLine()) != null) {
                pageSource += line + "\n";
            }
            reader.close();

            Float gitVersion = Float.parseFloat(pageSource);
            MediatorModel.model();
            if (gitVersion <= Float.parseFloat(InjectionModel.VERSION_JSQL)) {
                LOGGER.debug(I18n.get("UPDATE_UPTODATE"));
            } else {
                LOGGER.warn(I18n.get("UPDATE_NEW_VERSION_AVAILABLE"));
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn(I18n.get("UPDATE_EXCEPTION"));
            LOGGER.error(e, e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this, "Menubar - Check update").start();
    }
}
