package com.kokakiwi.mclauncher.ui.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentLabel;
import com.kokakiwi.mclauncher.utils.lang.Translater;

public class OptionsDialog extends JDialog
{
    private static final long serialVersionUID = -2663368148236524858L;
    
    private final LauncherAPI api;
    
    public OptionsDialog(final LauncherAPI api)
    {
        super(api.getFrame());
        this.api = api;
        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent event)
            {
                setVisible(false);
            }
        });
        
        setTitle(Translater.getString("options.windowTitle"));
        setModalityType(ModalityType.TOOLKIT_MODAL);
        
        final JPanel panel = new JPanel(new BorderLayout());
        
        final JLabel label = new JLabel("Launcher options", 0);
        label.setBorder(new EmptyBorder(0, 0, 16, 0));
        label.setFont(new Font("Default", 1, 16));
        panel.add(label, "North");
        
        final JPanel optionsPanel = new JPanel(new BorderLayout());
        final JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        final JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        optionsPanel.add(labelPanel, "West");
        optionsPanel.add(fieldPanel, "Center");
        
        // FORCE UPDATE
        final JCheckBox forceUpdate = new JCheckBox(
                Translater.getString("options.forceUpdateFalse"));
        if (api.getConfig().has("force-update"))
        {
            forceUpdate.setSelected(true);
            forceUpdate
                    .setText(Translater.getString("options.forceUpdateTrue"));
        }
        forceUpdate.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                if (!forceUpdate.isSelected())
                {
                    api.getConfig().set("force-update", false);
                    forceUpdate.setText(Translater
                            .getString("options.forceUpdateFalse"));
                }
                else
                {
                    api.getConfig().set("force-update", true);
                    forceUpdate.setText(Translater
                            .getString("options.forceUpdateTrue"));
                }
            }
        });
        labelPanel.add(new JLabel(Translater
                .getString("options.forceUpdateFalse") + " :"));
        fieldPanel.add(forceUpdate);
        
        // OFFLINE MODE
        final JCheckBox offlineModeToggle = new JCheckBox(
                Translater.getString("options.onlineMode"));
        final boolean offlineMode = api.getConfig().getBoolean(
                "login.offlineMode");
        if (offlineMode)
        {
            offlineModeToggle.setSelected(true);
            offlineModeToggle.setText(Translater
                    .getString("options.offlineMode"));
        }
        offlineModeToggle.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                if (offlineModeToggle.isSelected())
                {
                    offlineModeToggle.setText(Translater
                            .getString("options.offlineMode"));
                    api.getConfig().set("login.offlineMode", true);
                }
                else
                {
                    offlineModeToggle.setText(Translater
                            .getString("options.onlineMode"));
                    api.getConfig().set("login.offlineMode", false);
                }
            }
        });
        labelPanel.add(new JLabel(Translater.getString("options.offlineMode")
                + " :"));
        fieldPanel.add(offlineModeToggle);
        
        // GAME LOCATION
        final TransparentLabel dirLink = new TransparentLabel(api
                .getMinecraftDirectory().toString()) {
            private static final long serialVersionUID = 0L;
            
            public void paint(Graphics g)
            {
                super.paint(g);
                
                int x = 0;
                int y = 0;
                
                final FontMetrics fm = g.getFontMetrics();
                final int width = fm.stringWidth(getText());
                final int height = fm.getHeight();
                
                if (getAlignmentX() == 2.0F)
                {
                    x = 0;
                }
                else if (getAlignmentX() == 0.0F)
                {
                    x = getBounds().width / 2 - width / 2;
                }
                else if (getAlignmentX() == 4.0F)
                {
                    x = getBounds().width - width;
                }
                y = getBounds().height / 2 + height / 2 - 1;
                
                g.drawLine(x + 2, y, x + width - 2, y);
            }
            
            public void update(Graphics g)
            {
                paint(g);
            }
        };
        dirLink.setCursor(Cursor.getPredefinedCursor(12));
        dirLink.addMouseListener(new MouseAdapter() {
            
            public void mousePressed(MouseEvent arg0)
            {
                try
                {
                    if (Desktop.isDesktopSupported())
                    {
                        Desktop.getDesktop().open(api.getMinecraftDirectory());
                    }
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        dirLink.setForeground(new Color(2105599));
        
        labelPanel.add(new JLabel(Translater
                .getString("options.gameLocationLabel") + " : "));
        fieldPanel.add(dirLink);
        
        // MODS DIALOG
        //@formatter:off
        /* labelPanel.add(new JLabel(Translater.getString("options.mods.title")
                + " : "));
        JButton modsButton = new JButton(
                Translater.getString("options.mods.title"));
        modsButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                new ModsDialog(OptionsDialog.this).setVisible(true);
            }
        });
        fieldPanel.add(modsButton); */
        //@formatter:on
        
        panel.add(optionsPanel, "Center");
        
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(new JPanel(), "Center");
        final JButton doneButton = new JButton(
                Translater.getString("options.done"));
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae)
            {
                setVisible(false);
            }
        });
        buttonsPanel.add(doneButton, "East");
        buttonsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        
        panel.add(buttonsPanel, "South");
        
        getContentPane().add(panel);
        panel.setBorder(new EmptyBorder(16, 24, 24, 24));
        
        pack();
        setLocationRelativeTo(api.getFrame());
    }
    
    public LauncherAPI getApi()
    {
        return api;
    }
    
}
