package com.kokakiwi.mclauncher.utils.lang;

import java.util.Map;

import com.kokakiwi.mclauncher.utils.Configuration;

public class Translater
{
    private final static Configuration main = new Configuration();
    private final static Configuration user = new Configuration();
    
    private static String              lang = null;
    
    static
    {
        setLang("fr_FR");
    }
    
    public static String getString(String name, String[]... keys)
    {
        String str = getString(name);
        
        if (str != null)
        {
            str = TranslaterFormatter.format(str, keys);
        }
        
        return str;
    }
    
    public static String getString(String name, Map<String, String> keys)
    {
        String str = getString(name);
        
        if (str != null)
        {
            str = TranslaterFormatter.format(str, keys);
        }
        
        return str;
    }
    
    public static String getString(String name)
    {
        String str = user.getString(name);
        
        if (str == null)
        {
            str = main.getString(name);
        }
        
        return str;
    }
    
    public static String getLang()
    {
        return lang;
    }
    
    public static void setLang(String lang)
    {
        Translater.lang = lang;
        main.clear();
        main.load(
                Translater.class.getResourceAsStream("/lang/" + Translater.lang
                        + ".yml"), "yaml");
    }
    
    public static void set(String name, Object value)
    {
        user.set(name, value);
    }
    
    public static Configuration getMain()
    {
        return main;
    }
    
    public static Configuration getUser()
    {
        return user;
    }
}
