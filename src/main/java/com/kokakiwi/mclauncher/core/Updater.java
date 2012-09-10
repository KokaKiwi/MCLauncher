package com.kokakiwi.mclauncher.core;

import java.util.ArrayList;
import java.util.List;

import com.kokakiwi.mclauncher.MCLauncher;
import com.kokakiwi.mclauncher.core.updater.UpdaterWorker;
import com.kokakiwi.mclauncher.core.updater.VersionChecker;
import com.kokakiwi.mclauncher.utils.lang.Translater;

public class Updater implements Runnable
{
    private final MCLauncher                   main;
    private boolean                            running     = false;
    
    private boolean                            askUpdate   = false;
    private boolean                            doUpdate    = false;
    private int                                percentage  = 0;
    private State                              state       = State.INIT;
    private String                             error       = null;
    
    private final List<UpdaterWorker.GameFile> gameFiles   = new ArrayList<UpdaterWorker.GameFile>();
    private UpdaterWorker.GameFile             currentFile = null;
    private long                               downloaded  = 0;
    private long                               totalSize   = 0;
    
    public Updater(MCLauncher main)
    {
        this.main = main;
    }
    
    public MCLauncher getMain()
    {
        return main;
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public void setRunning(boolean running)
    {
        this.running = running;
    }
    
    public void run()
    {
        running = true;
        
        try
        {
            if (!main.getConfig().getBoolean("force-update"))
            {
                VersionChecker.checkVersion(main.getApi());
                
                while (askUpdate)
                {
                    // Wait user.
                    Thread.sleep(10L);
                }
            }
            else
            {
                doUpdate = true;
            }
            
            UpdaterWorker.determinePackages(main.getApi());
            
            if (doUpdate)
            {
                UpdaterWorker.update(main.getApi());
            }
            
            percentage = 90;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        
        running = false;
    }
    
    // Getter / Setter
    
    public boolean isAskUpdate()
    {
        boolean ask = askUpdate;
        
        if (main.getConfig().getBoolean("force-update"))
        {
            ask = false;
        }
        
        return ask;
    }
    
    public void setAskUpdate(boolean askUpdate)
    {
        this.askUpdate = askUpdate;
    }
    
    public boolean isDoUpdate()
    {
        return doUpdate;
    }
    
    public void setDoUpdate(boolean doUpdate)
    {
        this.doUpdate = doUpdate;
    }
    
    public int getPercentage()
    {
        return percentage;
    }
    
    public void setPercentage(int percentage)
    {
        this.percentage = percentage;
    }
    
    public String getError()
    {
        return error;
    }
    
    public void setError(String error)
    {
        this.error = error;
    }
    
    public State getState()
    {
        return state;
    }
    
    public void setState(State state)
    {
        this.state = state;
    }
    
    public UpdaterWorker.GameFile getCurrentFile()
    {
        return currentFile;
    }
    
    public void setCurrentFile(UpdaterWorker.GameFile currentFile)
    {
        this.currentFile = currentFile;
    }
    
    public long getDownloaded()
    {
        return downloaded;
    }
    
    public void setDownloaded(long downloaded)
    {
        this.downloaded = downloaded;
    }
    
    public void incrementDownloaded(long size)
    {
        downloaded += size;
    }
    
    public long getTotalSize()
    {
        return totalSize;
    }
    
    public void setTotalSize(long totalSize)
    {
        this.totalSize = totalSize;
    }
    
    public void incrementTotalSize(long size)
    {
        totalSize += size;
    }
    
    public List<UpdaterWorker.GameFile> getGameFiles()
    {
        return gameFiles;
    }
    
    public static enum State
    {
        INIT(1), DETERMINING_PACKAGES(2), CHECKING_CACHE(3), DOWNLOADING(4), EXTRACTING_PACKAGES(
                5), UPDATING_CLASSPATH(6), SWITCHING_APPLET(7), INITIALIZE_REAL_APPLET(
                8), START_REAL_APPLET(9), DONE(10);
        
        private final int    opcode;
        private final String description;
        
        State()
        {
            this(State.values().length + 1);
        }
        
        State(String description)
        {
            this(State.values().length + 1, description);
        }
        
        State(int opcode)
        {
            this(opcode, null);
        }
        
        State(int opcode, String description)
        {
            this.opcode = opcode;
            if (description == null)
            {
                this.description = Translater.getString("updater.states."
                        + name());
            }
            else
            {
                this.description = description;
            }
        }
        
        public int getOpCode()
        {
            return opcode;
        }
        
        public String getDescription()
        {
            return description;
        }
    }
}
