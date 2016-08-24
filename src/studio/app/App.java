/*
 * Copyright (c) 2010, ReportMill Software. All rights reserved.
 */
package studio.app;
import com.apple.eawt.*;
import com.apple.eawt.AppEvent.*;
import javax.swing.SwingUtilities;
import snap.util.*;
import snap.viewx.ExceptionReporter;

/**
 * SnapCode Application entry point.
 */
public class App {

/**
 * Main method to run panel.
 */
public static void main(final String args[])
{
    // Hook to run as SnapApp instead
    if(args.length>0 && args[0].equals("-snap")) { snap.javafx.SnapApp.main(args); return; }
    
    // Mac specific stuff
    if(SnapUtils.isMac) new AppleAppHandler().init();

    // Config/init JavaFX and invoke real main on event thread
    SwingUtilities.invokeLater(() -> new App(args));
}

/**
 * Main method to run panel.
 */
public App(String args[])
{
    // Set App and App Prefs class
    SnapUtils.isApp = true;
    PrefsUtils.setPrefsClass(App.class);
    
    // Install Exception reporter
    ExceptionReporter er = new ExceptionReporter();
    er.setURL("http://www.reportmill.com/cgi-bin/cgiemail/email/snap-exception.txt");
    er.setInfo("SnapCode Version 1, Build Date: " + SnapUtils.getBuildInfo());
    Thread.setDefaultUncaughtExceptionHandler(er);

    // Show open data source panel
    WelcomePanel.getShared().setOnQuit(() -> quitApp());
    WelcomePanel.getShared().showPanel();
}

/**
 * Exits the application.
 */
public static void quitApp()  { SwingUtilities.invokeLater(() -> quitAppImpl()); }

/**
 * Exits the application (real version).
 */
private static void quitAppImpl()
{
    //if(AppPane.getOpenAppPane()!=null) AppPane.getOpenAppPane().hide();
    PrefsUtils.flush();
    System.exit(0);
}

/**
 * A class to handle apple events.
 */
private static class AppleAppHandler implements PreferencesHandler, QuitHandler {

    /** Initializes Apple Application handling. */
    public void init()
    {
        //System.setProperty("apple.laf.useScreenMenuBar", "true"); // 1.4
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "SnapStudio");
        com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
        app.setPreferencesHandler(this); app.setQuitHandler(this);
        _appHand = this;
    }

    /** Handle Preferences. */
    public void handlePreferences(PreferencesEvent arg0)
    {
        //AppPane appPane = AppPane.getOpenAppPane(); if(appPane==null) return;
        //appPane.getBrowser().setFile(appPane.getSelectedSite().getRootDir());
    }

    /** Handle QuitRequest. */
    public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1)  { App.quitApp(); }
} static AppleAppHandler _appHand;

}