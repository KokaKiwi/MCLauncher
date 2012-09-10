package com.kokakiwi.mclauncher.core.launcher;

import java.applet.Applet;
import java.io.File;
import java.lang.reflect.Field;

import com.kokakiwi.mclauncher.api.LauncherAPI;

public class Wrapper
{
    public static Applet wrap(LauncherAPI api) throws Exception
    {
        final Class<?> clazz = api.getLauncher().getClassLoader()
                .loadClass("net.minecraft.client.MinecraftApplet");
        
        doChanges(api, clazz);
        
        final Applet applet = (Applet) clazz.newInstance();
        
        return applet;
    }
    
    public static void doChanges(LauncherAPI api, Class<?> clazz)
    {
        try
        {
            final Class<?> c = api.getLauncher().getClassLoader()
                    .loadClass("net.minecraft.client.Minecraft");
            
            for (final Field field : c.getDeclaredFields())
            {
                if (field.getType() == File.class)
                {
                    field.setAccessible(true);
                    try
                    {
                        field.get(c);
                        field.set(null, api.getMinecraftDirectory());
                    }
                    catch (final IllegalArgumentException e)
                    {
                        
                    }
                    catch (final IllegalAccessException e)
                    {
                        
                    }
                }
            }
        }
        catch (final ClassNotFoundException e)
        {
            
        }
    }
}
