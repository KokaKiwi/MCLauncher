package com.kokakiwi.mclauncher.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MCLogger
{
    private final static Logger    global = Logger.getLogger("MCLauncher");
    
    public final static DebugLevel DEBUG  = new DebugLevel();
    
    public static void register()
    {
        register(new File(getLauncherDir(), "mclauncher.log"));
    }
    
    public static void register(String fileName)
    {
        register(new File(fileName + ".log"));
    }
    
    public static void register(File file)
    {
        try
        {
            // Setup Logger
            for (final Handler handler : global.getHandlers())
            {
                global.removeHandler(handler);
            }
            
            final ConsoleHandler console = new TerminalConsoleHandler();
            global.addHandler(console);
            
            final File logFile = file;
            final FileHandler fileHandler = new FileHandler(
                    logFile.getAbsolutePath(), 500000, 5, true);
            fileHandler.setFormatter(new LogFormatter(true));
            global.addHandler(fileHandler);
            
            System.setOut(new PrintStream(System.out) {
                
                
                public void print(String s)
                {
                    global.info(s);
                }
                
            });
            
            System.setErr(new PrintStream(System.err) {
                
                
                public void print(String s)
                {
                    global.severe(s);
                }
                
            });
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void debug(Object message)
    {
        global.log(DEBUG, message.toString());
    }
    
    public static File getLauncherDir()
    {
        File dir = null;
        
        File root = new File(System.getProperty("user.home", ".") + "/");
        final String appName = "mclauncher";
        
        switch (SystemUtils.getSystemOS())
        {
            case linux:
            case solaris:
                dir = new File(root, "." + appName + "/");
                break;
            
            case macosx:
                dir = new File(root, "Library/Application Support/" + appName);
                break;
            
            case windows:
                final String applicationData = System.getenv("APPDATA");
                if (applicationData != null)
                {
                    root = new File(applicationData);
                }
                
                dir = new File(root, '.' + appName + '/');
                break;
            
            default:
                dir = new File(root, appName + '/');
                break;
        }
        
        if (dir != null && !dir.exists() && !dir.mkdirs())
        {
            throw new RuntimeException(
                    "The working directory could not be created: " + dir);
        }
        
        return dir;
    }
    
    public static class DebugLevel extends Level
    {
        private static final long serialVersionUID = 4164250706426396472L;
        
        protected DebugLevel()
        {
            super("DEBUG", Level.INFO.intValue() + 53);
        }
        
    }
    
    public static class TerminalConsoleHandler extends ConsoleHandler
    {
        public TerminalConsoleHandler()
        {
            super();
            setOutputStream(System.out);
            setFormatter(new LogFormatter(false));
        }
    }
    
    public static class LogFormatter extends Formatter
    {
        private final static String LINE_DELIMITER = System.getProperty("line.separator");
        
        private final boolean       newLine;
        
        public LogFormatter(boolean newLine)
        {
            super();
            this.newLine = newLine;
        }
        
        
        public String format(LogRecord record)
        {
            final StringBuffer sb = new StringBuffer();
            
            sb.append(new SimpleDateFormat("HH:mm:ss").format(new Date(record
                    .getMillis())));
            sb.append(' ');
            sb.append('[');
            sb.append(record.getLevel().toString());
            sb.append("] ");
            sb.append(record.getMessage());
            if (newLine)
            {
                sb.append(LINE_DELIMITER);
            }
            
            return sb.toString();
        }
        
        public String render(CharSequence str)
        {
            String s = "";
            
            for (int i = str.length() - 2; i < str.length(); i++)
            {
                final char c = str.charAt(i);
                s += Character.getName(c);
            }
            
            return s;
        }
        
    }
}
