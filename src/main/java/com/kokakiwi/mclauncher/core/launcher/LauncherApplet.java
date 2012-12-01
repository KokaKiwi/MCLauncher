package com.kokakiwi.mclauncher.core.launcher;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kokakiwi.mclauncher.api.LauncherAPI;

public class LauncherApplet extends Applet implements AppletStub
{
    private static final long         serialVersionUID = -2866153864231108251L;
    
    private final LauncherAPI         api;
    
    private final Map<String, String> params           = new LinkedHashMap<String, String>();
    
    private Applet                    applet;
    
    private boolean                   active           = false;
    private int                       context          = 0;
    
    public LauncherApplet(LauncherAPI api) throws HeadlessException
    {
        this.api = api;
        
        params.put("stand-alone", "true");
        params.put("username", api.getLoginer().getLastLogin().getUsername());
        params.put("sessionid",
                String.valueOf(api.getLoginer().getLastLogin().getSessionId()));
        
        setBackground(Color.black);
    }
    
    public void appletResize(int width, int height)
    {
        
    }
    
    public void replace(Applet applet)
    {
        this.applet = applet;
        applet.setStub(this);
        applet.setSize(getWidth(), getHeight());
        
        setLayout(new BorderLayout());
        add(applet, "Center");
        
        applet.init();
        active = true;
        applet.start();
        validate();
    }
    
    public boolean isActive()
    {
        if (context == 0)
        {
            context = -1;
            try
            {
                if (getAppletContext() != null)
                {
                    context = 1;
                }
            }
            catch (final Exception localException)
            {
            }
        }
        if (context == -1)
        {
            return active;
        }
        return super.isActive();
    }
    
    public String getParameter(String name)
    {
        String custom = api.getConfig().getString(name);
        if (custom == null)
        {
            custom = params.get(name);
            if (custom == null)
            {
                try
                {
                    custom = super.getParameter(name);
                }
                catch (final Exception e)
                {
                    params.put(name, null);
                }
            }
        }
        
        System.out.println("Minecraft want param '" + name + "' = '" + custom
                + "'");
        
        return custom;
    }
    
    public URL getDocumentBase()
    {
        try
        {
            return new URL(api.getConfig()
                    .getString("launcher.documentBaseURL"));
        }
        catch (final MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public void stop()
    {
        if (applet != null)
        {
            active = false;
            applet.stop();
            return;
        }
    }
    
    public void destroy()
    {
        if (applet != null)
        {
            applet.destroy();
            return;
        }
    }
    
    public Map<String, String> getParams()
    {
        return params;
    }
    
    public Applet getApplet()
    {
        return applet;
    }
    
    public int getContext()
    {
        return context;
    }
    
}
