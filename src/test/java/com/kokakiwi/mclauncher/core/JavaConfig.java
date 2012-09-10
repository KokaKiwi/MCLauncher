package com.kokakiwi.mclauncher.core;

import com.kokakiwi.mclauncher.utils.Configuration;

/**
 * This is a Class that will be fetched (if exists) by the launcher to fill
 * Configuration instead of others files.
 * 
 * If you don't want to use a config file, compile this class (with MCLauncher
 * in Classpath), and put it in package "com.kokakiwi.mclauncher.core" with same
 * name (JavaConfig).
 * 
 * @author Koka El Kiwi (http://kokaelkiwi.eu)
 * 
 */
public class JavaConfig
{
    public static Configuration config()
    {
        final Configuration config = new Configuration();
        
        config.set("load.user-config", true);
        
        return config;
    }
}
