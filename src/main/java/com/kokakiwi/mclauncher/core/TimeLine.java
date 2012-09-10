package com.kokakiwi.mclauncher.core;

import java.util.ArrayList;
import java.util.List;

import com.kokakiwi.mclauncher.MCLauncher;
import com.kokakiwi.mclauncher.api.ui.Page;

public class TimeLine
{
    private final MCLauncher main;
    
    private final List<Page> pages       = new ArrayList<Page>();
    private int              currentPage = -1;
    
    public TimeLine(MCLauncher main)
    {
        this.main = main;
    }
    
    public void init()
    {
        final List<Page> pages = main.getTheme().getPages();
        if (pages != null)
        {
            this.pages.addAll(pages);
        }
    }
    
    public void start()
    {
        next();
    }
    
    // Main section
    
    public void next()
    {
        currentPage++;
        
        if (currentPage < pages.size())
        {
            main.getFrame().removeAll();
            main.getFrame().reset();
            
            final Page page = getCurrentPage();
            page.fill(main.getFrame().getMainPanel(), main.getApi());
            
            main.getFrame().validate();
        }
        else
        {
            // Launch Minecraft
            main.getFrame().removeAll();
            main.getFrame().reset();
            
            main.getLauncher().launch();
            
            main.getFrame().validate();
        }
    }
    
    // Getter / Setter
    
    public MCLauncher getMain()
    {
        return main;
    }
    
    // Some utils...
    
    public int getCurrentPageId()
    {
        return currentPage;
    }
    
    public Page getCurrentPage()
    {
        return pages.get(currentPage);
    }
    
    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }
    
    public List<Page> getPages()
    {
        return pages;
    }
    
    public static enum Step
    {
        INIT, LOGIN, LOADING, LAUNCHING, DONE;
    }
}
