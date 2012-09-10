package com.kokakiwi.mclauncher.ui.simple;

import javax.swing.JPanel;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.api.ui.Page;
import com.kokakiwi.mclauncher.core.TimeLine.Step;

public class SimpleLoadingPage implements Page
{
    private LoadingApplet applet;
    
    public Step getStep()
    {
        return Step.LOADING;
    }
    
    public void fill(JPanel panel, LauncherAPI api)
    {
        applet = new LoadingApplet(api);
        applet.init();
        
        panel.add(applet, "Center");
        
        final Thread t = new Thread(api.getUpdater());
        t.setDaemon(true);
        t.start();
        
        applet.start();
    }
    
}
