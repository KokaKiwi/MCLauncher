package com.kokakiwi.mclauncher.ui.simple.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LogoPanel extends JPanel
{
    private static final long serialVersionUID = -6001125578594995289L;
    private Image             bgImage;
    
    public LogoPanel()
    {
        setOpaque(true);
        try
        {
            final BufferedImage src = ImageIO.read(LogoPanel.class
                    .getResourceAsStream("/res/logo.png"));
            final int w = src.getWidth();
            final int h = src.getHeight();
            bgImage = src.getScaledInstance(w, h, 16);
            setPreferredSize(new Dimension(w + 32, h + 32));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
    public void update(Graphics g)
    {
        paint(g);
    }
    
    
    public void paintComponent(Graphics g)
    {
        g.drawImage(bgImage, 24, 24, null);
    }
}