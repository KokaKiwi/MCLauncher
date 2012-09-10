package com.kokakiwi.mclauncher.ui.simple.components;

import java.awt.Color;

import javax.swing.JCheckBox;

public class TransparentCheckbox extends JCheckBox
{
    private static final long serialVersionUID = -2805257861903167193L;
    
    public TransparentCheckbox(String string)
    {
        super(string);
        setForeground(Color.WHITE);
    }
    
    
    public boolean isOpaque()
    {
        return false;
    }
}