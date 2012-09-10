package com.kokakiwi.mclauncher.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.kokakiwi.mclauncher.MCLauncher;

public class LauncherFrame extends Frame
{
    private static final long serialVersionUID = 246347330866403390L;
    
    private final MCLauncher  main;
    private JPanel            mainPanel;
    
    public LauncherFrame(MCLauncher main) throws HeadlessException
    {
        super(main.getConfig().getString("window.title"));
        this.main = main;
    }
    
    public void init()
    {
        try
        {
            setIconImage(ImageIO.read(LauncherFrame.class
                    .getResourceAsStream("/"
                            + main.getConfig().getString("window.icon"))));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        
        addWindowListener(new WindowAdapter() {
            
            
            public void windowClosing(WindowEvent e)
            {
                main.stop();
            }
        });
        
        setLayout(new BorderLayout());
        
        reset();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public void reset()
    {
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(main.getTheme().getDimension());
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, "Center");
    }
    
    public MCLauncher getMain()
    {
        return main;
    }
    
    public JPanel getMainPanel()
    {
        return mainPanel;
    }
}
