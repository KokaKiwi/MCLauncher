package com.kokakiwi.mclauncher.core.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.core.Updater.State;
import com.kokakiwi.mclauncher.utils.SystemUtils;

public class UpdaterWorker
{
    public static void update(LauncherAPI api)
    {
        for (final GameFile file : api.getUpdater().getGameFiles())
        {
            try
            {
                file.init();
                api.getUpdater().incrementTotalSize(file.getSize());
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
        
        // Downloading packages
        api.getUpdater().setState(State.DOWNLOADING);
        api.getUpdater().setPercentage(10);
        for (final GameFile file : api.getUpdater().getGameFiles())
        {
            api.getUpdater().setCurrentFile(file);
            try
            {
                file.download(api);
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
        api.getUpdater().setCurrentFile(null);
        
        // Extracting packages
        api.getUpdater().setState(State.EXTRACTING_PACKAGES);
        api.getUpdater().setPercentage(55);
        
        final int percentageStep = 20 / api.getUpdater().getGameFiles().size();
        
        for (int i = 0; i < api.getUpdater().getGameFiles().size(); i++)
        {
            final GameFile file = api.getUpdater().getGameFiles().get(i);
            api.getUpdater().setCurrentFile(file);
            final int min = 55 + i * percentageStep;
            final int max = 55 + (i + 1) * percentageStep;
            file.extract(api, min, max);
        }
        api.getUpdater().setCurrentFile(null);
    }
    
    public static void determinePackages(LauncherAPI api)
    {
        api.getUpdater().setState(State.DETERMINING_PACKAGES);
        api.getUpdater().setPercentage(5);
        
        api.getUpdater().getGameFiles().clear();
        File dest = null;
        Type type = null;
        
        try
        {
            // Load libraries URLs
            dest = new File(api.getMinecraftDirectory(), "bin");
            type = Type.LIBRARY;
            for (final String librariesFile : api.getConfig().getStringList(
                    "updater.libraries"))
            {
                final URL url = new URL(librariesFile);
                
                final GameFile file = new GameFile(url, dest, type);
                api.getUpdater().getGameFiles().add(file);
            }
            
            // Load native URL
            dest = new File(dest, "natives");
            {
                final URL url = new URL(api.getConfig().getString(
                        "updater.natives." + SystemUtils.getSystemOS().name()));
                type = Type.NATIVE;
                final GameFile file = new GameFile(url, dest, type);
                api.getUpdater().getGameFiles().add(file);
            }
            
            // Load additionnals URLs
            dest = new File(api.getMinecraftDirectory(), "bin");
            type = Type.ADDITIONNAL;
            for (final String additionnalFile : api.getConfig().getStringList(
                    "updater.additionnals"))
            {
                final URL url = new URL(additionnalFile);
                
                final GameFile file = new GameFile(url, dest, type);
                api.getUpdater().getGameFiles().add(file);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static class GameFile
    {
        private final URL         url;
        private final File        dest;
        private final Type        type;
        
        private HttpURLConnection connection = null;
        
        private float             speed      = 0;
        private long              downloaded = 0;
        private long              total      = 0;
        
        private int               percentage = 0;
        
        public GameFile(URL url, File dest, Type type) throws Exception
        {
            this.url = url;
            this.dest = dest;
            this.type = type;
            
            dest.mkdirs();
        }
        
        public void init() throws Exception
        {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDefaultUseCaches(false);
            
            total = connection.getContentLength();
        }
        
        public void download(LauncherAPI api) throws Exception
        {
            System.out.println("Downloading '" + getFileName() + "'...");
            
            long lastTime = System.currentTimeMillis();
            
            final InputStream in = connection.getInputStream();
            final OutputStream out = new FileOutputStream(new File(dest,
                    getFileName()));
            
            int instantDownload = 0;
            int bufferSize;
            final byte[] buffer = new byte[65536];
            while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, bufferSize);
                
                downloaded += bufferSize;
                api.getUpdater().incrementDownloaded(bufferSize);
                instantDownload += bufferSize;
                
                final long diff = System.currentTimeMillis() - lastTime;
                
                if (diff >= 1000L)
                {
                    speed = (int) (instantDownload / diff * 100.0f) / 100.0f;
                    
                    instantDownload = 0;
                    lastTime += diff;
                }
                
                percentage = (int) (downloaded * 100 / total);
                api.getUpdater().setPercentage(
                        (int) (10 + api.getUpdater().getDownloaded() * 45
                                / api.getUpdater().getTotalSize()));
            }
            in.close();
            out.close();
        }
        
        public void extract(LauncherAPI api, int min, int max)
        {
            System.out.println("Extracting '" + getFileName() + "'...");
            
            if (type == Type.NATIVE || type == Type.ADDITIONNAL)
            {
                File dest = this.dest;
                boolean recursive = true;
                
                if (type == Type.ADDITIONNAL)
                {
                    dest = api.getMinecraftDirectory();
                    recursive = false;
                }
                
                try
                {
                    if (FileExtractor.extract(api, this, dest, min, max,
                            recursive))
                    {
                        
                    }
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        public URL getUrl()
        {
            return url;
        }
        
        public File getDest()
        {
            return dest;
        }
        
        public Type getType()
        {
            return type;
        }
        
        public String getFileName()
        {
            return url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
        }
        
        public float getSpeed()
        {
            return speed;
        }
        
        public long getSize()
        {
            return total;
        }
        
        public HttpURLConnection getConnection()
        {
            return connection;
        }
        
        public long getDownloaded()
        {
            return downloaded;
        }
        
        public long getTotal()
        {
            return total;
        }
        
        public int getPercentage()
        {
            return percentage;
        }
        
        public void setPercentage(int percentage)
        {
            this.percentage = percentage;
        }
        
        public File getFile()
        {
            return new File(dest, getFileName());
        }
        
        
        public String toString()
        {
            return getFileName();
        }
    }
    
    public static enum Type
    {
        LIBRARY, NATIVE, ADDITIONNAL;
    }
}
