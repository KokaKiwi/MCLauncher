package com.kokakiwi.mclauncher.api.ui;

import javax.swing.JPanel;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.core.TimeLine;

public interface Page
{
    public TimeLine.Step getStep();
    
    public void fill(JPanel panel, LauncherAPI api);
}
