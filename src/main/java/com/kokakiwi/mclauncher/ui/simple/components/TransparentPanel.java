package com.kokakiwi.mclauncher.ui.simple.components;

import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel
{
    private static final long serialVersionUID = 3818161668902701298L;
    
    private Insets            insets;
    
    public TransparentPanel()
    {
        
    }
    
    public TransparentPanel(LayoutManager layout)
    {
        setLayout(layout);
    }
    
    
    public boolean isOpaque()
    {
        return false;
    }
    
    public void setInsets(int a, int b, int c, int d)
    {
        insets = new Insets(a, b, c, d);
    }
    
    
    public Insets getInsets()
    {
        if (insets == null)
        {
            return super.getInsets();
        }
        return insets;
    }
    
}
