package com.kokakiwi.mclauncher.ui.simple.components;

import javax.swing.JButton;

public class TransparentButton extends JButton
{
    private static final long serialVersionUID = -7363388629891733925L;
    
    public TransparentButton(String string)
    {
        super(string);
    }
    
    
    public boolean isOpaque()
    {
        return false;
    }
}