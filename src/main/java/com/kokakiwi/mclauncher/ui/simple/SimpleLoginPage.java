package com.kokakiwi.mclauncher.ui.simple;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.api.ui.Page;
import com.kokakiwi.mclauncher.core.TimeLine.Step;
import com.kokakiwi.mclauncher.ui.simple.components.LogoPanel;
import com.kokakiwi.mclauncher.ui.simple.components.NavigatorPanel;
import com.kokakiwi.mclauncher.ui.simple.components.TexturedPanel;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentButton;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentCheckbox;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentLabel;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentPanel;
import com.kokakiwi.mclauncher.utils.lang.Translater;

public class SimpleLoginPage implements Page
{
    private LauncherAPI            api;
    private JPanel                 panel;
    
    private final TransparentLabel statusText = new TransparentLabel("",
                                                      SwingConstants.CENTER);
    
    private final JPanel           loginBox   = new TransparentPanel();
    
    private final JTextField       userName   = new JTextField(20);
    private final JPasswordField   password   = new JPasswordField(20);
    private final JCheckBox        rememberMe = new TransparentCheckbox(
                                                      Translater
                                                              .getString("login.rememberBox"));
    
    public SimpleLoginPage()
    {
        userName.addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e)
            {
                statusText.setText(null);
            }
        });
        
        password.addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e)
            {
                statusText.setText(null);
            }
        });
    }
    
    public Step getStep()
    {
        return Step.LOGIN;
    }
    
    public void fill(JPanel panel, LauncherAPI api)
    {
        this.panel = panel;
        this.api = api;
        
        userName.setText(api.getConfig().getString("username", ""));
        password.setText(api.getConfig().getString("password", ""));
        
        if (api.getLoginer().getStoredUsername() != null)
        {
            userName.setText(api.getLoginer().getStoredUsername());
            password.setText(api.getLoginer().getStoredPassword());
            rememberMe.setSelected(true);
        }
        
        panel.add(getNewsPanel(api), "Center");
        panel.add(getLoginPanel(api), "South");
    }
    
    private NavigatorPanel getNewsPanel(LauncherAPI api)
    {
        NavigatorPanel panel = null;
        try
        {
            panel = new NavigatorPanel(api, api.getConfig().getString(
                    "news.url"));
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    private JPanel getLoginPanel(LauncherAPI api)
    {
        final JPanel panel = new TexturedPanel(api.getBackground());
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(100, 100));
        
        panel.add(new LogoPanel(), "West");
        panel.add(statusText, "Center");
        
        buildLoginBox(false);
        panel.add(center(loginBox), "East");
        
        return panel;
    }
    
    private void buildLoginBox(boolean offline)
    {
        loginBox.removeAll();
        loginBox.setLayout(new BorderLayout(0, 8));
        
        final TransparentPanel titles = new TransparentPanel();
        titles.setLayout(new GridLayout(0, 1, 0, 2));
        
        titles.add(new TransparentLabel(Translater
                .getString("login.usernameLabel") + " :", 4));
        titles.add(new TransparentLabel(Translater
                .getString("login.passwordLabel") + " :", 4));
        titles.add(new TransparentLabel("", 4));
        
        titles.setInsets(0, 0, 0, 4);
        
        final TransparentPanel fields = new TransparentPanel();
        fields.setLayout(new GridLayout(0, 1, 0, 2));
        
        fields.add(userName);
        fields.add(password);
        fields.add(rememberMe);
        
        final TransparentPanel buttons = new TransparentPanel();
        buttons.setLayout(new GridLayout(0, 1, 0, 2));
        
        if (offline)
        {
            final TransparentButton optionsButton = new TransparentButton(
                    Translater.getString("login.retryButton"));
            optionsButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e)
                {
                    buildLoginBox(false);
                }
            });
            buttons.add(optionsButton);
            
            final TransparentButton loginButton = new TransparentButton(
                    Translater.getString("login.offlineButton"));
            loginButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e)
                {
                    new OptionsDialog(api).setVisible(true);
                }
            });
            buttons.add(loginButton);
        }
        else
        {
            final TransparentButton optionsButton = new TransparentButton(
                    Translater.getString("login.optionsButton"));
            optionsButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e)
                {
                    new OptionsDialog(api).setVisible(true);
                }
            });
            buttons.add(optionsButton);
            
            final TransparentButton loginButton = new TransparentButton(
                    Translater.getString("login.loginButton"));
            loginButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e)
                {
                    statusText.setText(Translater.getString("login.loginning"));
                    
                    new Thread(new Runnable() {
                        
                        public void run()
                        {
                            String result = null;
                            
                            if (!api.getConfig()
                                    .getBoolean("login.offlineMode"))
                            {
                                result = api.getLoginer().doLogin(
                                        userName.getText(),
                                        new String(password.getPassword()),
                                        rememberMe.isSelected());
                            }
                            else
                            {
                                api.getLoginer().getLastLogin()
                                        .setUsername(userName.getText());
                                api.getLoginer().getLastLogin()
                                        .setSessionId("123456");
                                api.getLoginer().getLastLogin().setTimestamp(0);
                                api.getLoginer().getLastLogin()
                                        .setDownloadTicket("12345");
                                if (rememberMe.isSelected())
                                {
                                    try
                                    {
                                        api.getLoginer().storeLogin(userName.getText(), "lolilol");
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            
                            if (result == null)
                            {
                                api.getTimeLine().next();
                            }
                            else
                            {
                                statusText.setText(result);
                                
                                if (!result.equalsIgnoreCase("Bad login"))
                                {
                                    buildLoginBox(true);
                                }
                            }
                        }
                    }).start();
                }
            });
            buttons.add(loginButton);
        }
        
        buttons.add(new TransparentPanel());
        
        buttons.setInsets(0, 10, 0, 10);
        
        loginBox.add(titles, "West");
        loginBox.add(fields, "Center");
        loginBox.add(buttons, "East");
        
        panel.validate();
    }
    
    private Component center(Component c)
    {
        final TransparentPanel tp = new TransparentPanel(new GridBagLayout());
        tp.add(c);
        return tp;
    }
}
