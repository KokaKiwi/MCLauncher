package com.kokakiwi.mclauncher.core.launcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModsManager
{
    private final List<Mod> mods = new ArrayList<Mod>();
    
    public void addMod(Mod mod)
    {
        mods.add(mod);
    }
    
    public List<Mod> getMods()
    {
        return mods;
    }
    
    public void fill(List<URL> urls)
    {
        for (Mod mod : mods)
        {
            urls.add(mod.getUrl());
        }
    }
    
    public static class Mod
    {
        private String name;
        private String description;
        private URL    url;
        
        public Mod()
        {
            this("", null);
        }
        
        public Mod(String name, URL url)
        {
            this(name, "", url);
        }
        
        public Mod(String name, String description, URL url)
        {
            this.name = name;
            this.description = description;
            this.url = url;
        }
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public URL getUrl()
        {
            return url;
        }
        
        public void setUrl(URL url)
        {
            this.url = url;
        }
        
        public String toString()
        {
            return name;
        }
    }
}
