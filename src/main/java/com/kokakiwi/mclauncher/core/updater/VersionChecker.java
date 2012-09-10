package com.kokakiwi.mclauncher.core.updater;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.utils.Version;

public class VersionChecker
{
    public static void checkVersion(LauncherAPI api) throws Exception
    {
        boolean update = false;
        
        final List<Map<String, Object>> versions = api.getConfig().getMapList(
                "updater.versions");
        
        for (final Map<String, Object> version : versions)
        {
            if (!update)
            {
                final File file = new File(api.getMinecraftDirectory(),
                        (String) version.get("file"));
                final String lastVersion = getLastVersion(api,
                        (String) version.get("source"));
                
                if (lastVersion != null)
                {
                    if (!file.exists())
                    {
                        update = true;
                        api.getUpdater().setAskUpdate(false);
                        api.getUpdater().setDoUpdate(true);
                    }
                    else
                    {
                        final String current = readVersionFile(api, file);
                        final Version v1 = Version.parseString(current);
                        final Version v2 = Version.parseString(lastVersion);
                        
                        if (v2.compareTo(v1) > 0)
                        {
                            update = true;
                            boolean force = (Boolean) version.get("force");
                            if (!force)
                            {
                                force = api.getConfig().getBoolean(
                                        "updater.askUpdateIfAvailable", true);
                            }
                            
                            if (!force)
                            {
                                api.getUpdater().setAskUpdate(true);
                            }
                            api.getUpdater().setDoUpdate(true);
                        }
                    }
                }
                
                if (update)
                {
                    updateVersionFile(file, lastVersion);
                }
            }
        }
    }
    
    public static String readVersionFile(LauncherAPI api, File file)
            throws Exception
    {
        if (!file.exists())
        {
            file.createNewFile();
            updateVersionFile(file, "-1");
        }
        
        String version = null;
        
        final DataInputStream in = new DataInputStream(
                new FileInputStream(file));
        version = in.readUTF();
        in.close();
        
        return version;
    }
    
    public static void updateVersionFile(LauncherAPI api, File file,
            Map<String, Object> version) throws Exception
    {
        updateVersionFile(file,
                getLastVersion(api, (String) version.get("source")));
    }
    
    public static void updateVersionFile(File file, String lastVersion)
            throws Exception
    {
        if (!file.exists())
        {
            file.createNewFile();
        }
        
        final DataOutputStream out = new DataOutputStream(new FileOutputStream(
                file));
        out.writeUTF(lastVersion);
        out.close();
    }
    
    public static String getLastVersion(LauncherAPI api, String source)
    {
        String version = null;
        
        if (source == null)
        {
            if (api.getLoginer().getLastLogin() != null)
            {
                version = String.valueOf(api.getLoginer().getLastLogin()
                        .getTimestamp());
            }
        }
        else
        {
            version = api.getUrl(source);
        }
        
        return version;
    }
}
