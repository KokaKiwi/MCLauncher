package com.kokakiwi.mclauncher.ui.simple;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.VolatileImage;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.core.Updater.State;
import com.kokakiwi.mclauncher.utils.lang.Translater;

public class LoadingApplet extends Applet implements MouseListener
{
    private static final long serialVersionUID = 1827646901328187548L;
    
    private final LauncherAPI api;
    private VolatileImage     img              = null;
    
    public LoadingApplet(LauncherAPI api) throws HeadlessException
    {
        super();
        this.api = api;
    }
    
    
    public void init()
    {
        addMouseListener(this);
    }
    
    
    public void paint(Graphics g2)
    {
        final int w = getWidth() / 2;
        final int h = getHeight() / 2;
        if (img == null || img.getWidth() != w || img.getHeight() != h)
        {
            img = createVolatileImage(w, h);
        }
        
        final Graphics g = img.getGraphics();
        
        // Draw background
        for (int x = 0; x <= w / 32; x++)
        {
            for (int y = 0; y <= h / 32; y++)
            {
                g.drawImage(api.getBackground(), x * 32, y * 32, null);
            }
        }
        
        if (api.getUpdater().isAskUpdate())
        {
            g.setColor(Color.LIGHT_GRAY);
            String msg = Translater.getString("updater.newUpdateAvailable");
            g.setFont(new Font(null, 1, 20));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2,
                    h / 2 - fm.getHeight() * 2);
            
            g.setFont(new Font(null, 0, 12));
            fm = g.getFontMetrics();
            
            g.fill3DRect(w / 2 - 56 - 8, h / 2, 56, 20, true);
            g.fill3DRect(w / 2 + 8, h / 2, 56, 20, true);
            
            msg = Translater.getString("updater.askUpdate");
            g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2, h / 2 - 8);
            
            g.setColor(Color.BLACK);
            msg = Translater.getString("global.yesStr");
            g.drawString(msg, w / 2 - 56 - 8 - fm.stringWidth(msg) / 2 + 28,
                    h / 2 + 14);
            msg = Translater.getString("global.noStr");
            g.drawString(msg, w / 2 + 8 - fm.stringWidth(msg) / 2 + 28,
                    h / 2 + 14);
        }
        else
        {
            g.setColor(Color.LIGHT_GRAY);
            
            String msg = Translater.getString("updater.title", new String[] {
                    "GAMENAME", api.getConfig().getString("window.title") });
            String subtaskMessage = "";
            
            if (api.getUpdater().getState() == State.DOWNLOADING
                    && api.getUpdater().getCurrentFile() != null)
            {
                subtaskMessage = Translater.getString("updater.retrieving")
                        + ": "
                        + api.getUpdater().getCurrentFile().getFileName() + " ";
                subtaskMessage += api.getUpdater().getCurrentFile()
                        .getPercentage()
                        + "%";
                
                if (api.getUpdater().getCurrentFile().getSpeed() > 0)
                {
                    subtaskMessage += " @ "
                            + api.getUpdater().getCurrentFile().getSpeed()
                            + " Kb/sec";
                }
            }
            else if (api.getUpdater().getState() == State.EXTRACTING_PACKAGES
                    && api.getUpdater().getCurrentFile() != null)
            {
                subtaskMessage = Translater.getString("updater.extracting")
                        + ": "
                        + api.getUpdater().getCurrentFile().getFileName() + " ";
                subtaskMessage += api.getUpdater().getCurrentFile()
                        .getPercentage()
                        + "%";
            }
            
            if (api.getUpdater().getError() != null)
            {
                msg = "Failed to launch";
            }
            
            g.setFont(new Font(null, 1, 20));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2,
                    h / 2 - fm.getHeight() * 2);
            
            g.setFont(new Font(null, 0, 12));
            fm = g.getFontMetrics();
            msg = api.getUpdater().getState().getDescription();
            if (api.getUpdater().getError() != null)
            {
                msg = api.getUpdater().getError();
                subtaskMessage = "";
            }
            
            g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2,
                    h / 2 + fm.getHeight() * 1);
            msg = subtaskMessage;
            g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2,
                    h / 2 + fm.getHeight() * 2);
            
            if (api.getUpdater().getError() == null)
            {
                g.setColor(Color.black);
                g.fillRect(64, h - 64, w - 128 + 1, 5);
                g.setColor(new Color(32768));
                g.fillRect(64, h - 64, api.getUpdater().getPercentage()
                        * (w - 128) / 100, 4);
                g.setColor(new Color(2138144));
                g.fillRect(65, h - 64 + 1, api.getUpdater().getPercentage()
                        * (w - 128) / 100 - 2, 1);
                
                g.setColor(Color.LIGHT_GRAY);
                g.setFont(new Font(null, 0, 12));
                msg = api.getUpdater().getPercentage() + "%";
                g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2, h - 47);
            }
        }
        
        g.dispose();
        g2.drawImage(img, 0, 0, w * 2, h * 2, null);
    }
    
    
    public void start()
    {
        final Thread t = new Thread(new Runnable() {
            
            public void run()
            {
                while (api.getUpdater().isRunning())
                {
                    repaint();
                    try
                    {
                        Thread.sleep(10L);
                    }
                    catch (final InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                api.getTimeLine().next();
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    
    public void update(Graphics g)
    {
        paint(g);
    }
    
    private boolean contains(int x, int y, int xx, int yy, int w, int h)
    {
        return x >= xx && y >= yy && x < xx + w && y < yy + h;
    }
    
    public void mouseClicked(MouseEvent e)
    {
        
    }
    
    //@formatter:off
    public void mousePressed(MouseEvent e)
    {
        if(api.getUpdater().isAskUpdate())
        {
            final int x = e.getX() / 2;
            final int y = e.getY() / 2;
            final int w = getWidth() / 2;
            final int h = getHeight() / 2;
            
            if (contains(x, y, w / 2 - 56 - 8, h / 2, 56, 20))
            {
                api.getUpdater().setDoUpdate(true);
                api.getUpdater().setAskUpdate(false);
            }
            if (contains(x, y, w / 2 + 8, h / 2, 56, 20))
            {
                api.getUpdater().setDoUpdate(false);
                api.getUpdater().setAskUpdate(false);
            }
        }
    }
    //@formatter:on
    
    public void mouseReleased(MouseEvent e)
    {
        
    }
    
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    public void mouseExited(MouseEvent e)
    {
        
    }
}
