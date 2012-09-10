package com.kokakiwi.mclauncher.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.kokakiwi.mclauncher.MCLauncher;
import com.kokakiwi.mclauncher.core.launcher.LauncherApplet;
import com.kokakiwi.mclauncher.core.launcher.ModsManager;
import com.kokakiwi.mclauncher.core.launcher.Wrapper;
import com.kokakiwi.mclauncher.core.updater.UpdaterWorker;
import com.kokakiwi.mclauncher.core.updater.UpdaterWorker.Type;

public class Launcher implements Runnable
{
    private final MCLauncher  main;
    private final ModsManager mods;
    private URLClassLoader    classLoader;
    private LauncherApplet    applet;
    
    public Launcher(MCLauncher main)
    {
        this.main = main;
        mods = new ModsManager();
    }
    
    public void launch()
    {
        // Load URLs
        final List<URL> urls = new ArrayList<URL>();
        
        mods.fill(urls);
        
        File natives = new File(main.getApi().getMinecraftDirectory(), "bin/");
        for (final UpdaterWorker.GameFile gameFile : main.getUpdater()
                .getGameFiles())
        {
            if (gameFile.getType() == Type.LIBRARY)
            {
                try
                {
                    final URL url = gameFile.getFile().toURI().toURL();
                    urls.add(url);
                }
                catch (final MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
            
            if (gameFile.getType() == Type.NATIVE)
            {
                natives = gameFile.getDest();
            }
        }
        System.out.println("Set natives dir to '" + natives.getAbsolutePath()
                + "'");
        
        // Add System env values
        System.setProperty("org.lwjgl.librarypath", natives.getAbsolutePath());
        System.setProperty("net.java.games.input.librarypath",
                natives.getAbsolutePath());
        
        // Start Minecraft
        classLoader = new URLClassLoader(urls.toArray(new URL[0]));
        
        applet = new LauncherApplet(main.getApi());
        
        applet.init();
        main.getFrame().getMainPanel().add(applet, "Center");
        main.getFrame().validate();
        applet.start();
        
        final Thread t = new Thread(this);
        t.start();
    }
    
    public void run()
    {
        try
        {
            applet.replace(Wrapper.wrap(main.getApi()));
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public MCLauncher getMain()
    {
        return main;
    }
    
    public URLClassLoader getClassLoader()
    {
        return classLoader;
    }
    
    public ModsManager getMods()
    {
        return mods;
    }
}
