package com.kokakiwi.mclauncher.api.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.util.List;

import com.kokakiwi.mclauncher.api.LauncherAPI;

public interface Theme
{
    public void onLoad(LauncherAPI api);
    
    public List<Page> getPages();
    
    public Dimension getDimension();
    
    public Image getBackground() throws Exception;
}
