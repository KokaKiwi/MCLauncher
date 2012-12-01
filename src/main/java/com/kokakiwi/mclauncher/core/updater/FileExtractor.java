package com.kokakiwi.mclauncher.core.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.contrapunctus.lzma.LzmaInputStream;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.core.updater.UpdaterWorker.GameFile;

public class FileExtractor
{
    public static boolean extract(LauncherAPI api, GameFile source, File dest,
            int min, int max, boolean recursive) throws Exception
    {
        final String[] exts = source.getFileName()
                .substring(source.getFileName().indexOf('.', 0) + 1)
                .split("\\.");
        
        File file = source.getFile();
        
        for (int i = exts.length - 1; i > -1; i--)
        {
            final String ext = exts[i];
            boolean pass = false;
            
            if (ext.equalsIgnoreCase("lzma"))
            {
                file = extractLZMA(api, source, file, dest, min, max);
            }
            
            if (ext.equalsIgnoreCase("zip"))
            {
                extractZIP(api, source, file, dest, min, max);
                pass = true;
            }
            
            if (ext.equalsIgnoreCase("jar"))
            {
                extractJAR(api, source, file, dest, min, max);
                pass = true;
            }
            
            if (!recursive || pass)
            {
                i = -1;
            }
        }
        
        return false;
    }
    
    public static File extractLZMA(LauncherAPI api, GameFile source, File file,
            File dest, int min, int max) throws Exception
    {
        final File uncompressed = new File(dest, file.getName().substring(0,
                file.getName().lastIndexOf('.')));
        
        final FileInputStream fileInput = new FileInputStream(file);
        
        InputStream in = new LzmaInputStream(fileInput);
        
        OutputStream out = new FileOutputStream(uncompressed);
        
        final byte[] buffer = new byte[16384];
        
        int ret = in.read(buffer);
        while (ret >= 1)
        {
            out.write(buffer, 0, ret);
            ret = in.read(buffer);
        }
        
        in.close();
        out.close();
        
        in = null;
        out = null;
        
        return uncompressed;
    }
    
    @SuppressWarnings("unchecked")
    public static void extractZIP(LauncherAPI api, GameFile source, File file,
            File dest, int min, int max) throws Exception
    {
        final ZipFile zip = new ZipFile(file);
        
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        
        int total = 0;
        while (entries.hasMoreElements())
        {
            final ZipEntry entry = entries.nextElement();
            
            if (!entry.isDirectory())
            {
                total += entry.getSize();
            }
        }
        
        entries = (Enumeration<ZipEntry>) zip.entries();
        final int current = 0;
        
        while (entries.hasMoreElements())
        {
            final ZipEntry entry = entries.nextElement();
            
            if (entry.isDirectory())
            {
                final File dir = new File(dest, entry.getName());
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
            }
            else
            {
                final File e = new File(dest, entry.getName());
                copyStream(api, source, zip.getInputStream(entry),
                        new FileOutputStream(e), current, total, min, max);
            }
        }
        
        zip.close();
    }
    
    public static void extractJAR(LauncherAPI api, GameFile source, File file,
            File dest, int min, int max) throws Exception
    {
        JarFile jar = null;
        try
        {
            jar = new JarFile(file);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        
        Enumeration<JarEntry> entries = jar.entries();
        
        int total = 0;
        while (entries.hasMoreElements())
        {
            final JarEntry entry = entries.nextElement();
            
            if (!entry.isDirectory()
                    && !entry.getName().equalsIgnoreCase("MANIFEST.MF"))
            {
                total += entry.getSize();
            }
        }
        
        entries = jar.entries();
        final int current = 0;
        
        while (entries.hasMoreElements())
        {
            final JarEntry entry = entries.nextElement();
            
            if (entry.isDirectory())
            {
                final File dir = new File(dest, entry.getName());
                if (!dir.exists()
                        && !dir.getName().equalsIgnoreCase("META-INF"))
                {
                    dir.mkdirs();
                }
            }
            else
            {
                final File e = new File(dest, entry.getName());
                if (!e.getParent().contains("META-INF"))
                {
                    copyStream(api, source, jar.getInputStream(entry),
                            new FileOutputStream(e), current, total, min, max);
                }
            }
        }
    }
    
    private static void copyStream(LauncherAPI api, GameFile source,
            InputStream in, OutputStream out, int current, int total, int min,
            int max) throws Exception
    {
        copyStream(api, source, in, out, current, total, min, max, 65536);
    }
    
    private static void copyStream(LauncherAPI api, GameFile source,
            InputStream in, OutputStream out, int current, int total, int min,
            int max, int bufferLength) throws Exception
    {
        final byte[] buffer = new byte[bufferLength];
        int bufferSize;
        
        while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1)
        {
            out.write(buffer, 0, bufferSize);
            current += bufferSize;
            
            source.setPercentage(current * 100 / total);
            api.getUpdater().setPercentage(min + current * (max - min) / total);
        }
        
        in.close();
        out.close();
    }
}
