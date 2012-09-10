package com.kokakiwi.mclauncher.ui.simple.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.utils.lang.Translater;

public class NavigatorPanel extends TexturedPanel
{
    private static final long serialVersionUID = -2791605062203929957L;
    
    private final LauncherAPI api;
    private final String      url;
    private final JTextPane   editorPane;
    private final JScrollPane scrollPane;
    
    public NavigatorPanel(LauncherAPI api, String url) throws Exception
    {
        super(api.getConfig().has("news.background") ? api.getConfig()
                .getString("news.background") : "res/stone.png");
        this.api = api;
        this.url = url;
        
        editorPane = new JTextPane();
        editorPane.setContentType("text/html");
        editorPane
                .setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>"
                        + Translater.getString("news.defaultText")
                        + "</center></font></body></html>");
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            
            public void hyperlinkUpdate(HyperlinkEvent he)
            {
                if (he.getEventType() == EventType.ACTIVATED)
                {
                    try
                    {
                        editorPane.setPage(he.getURL());
                    }
                    catch (final IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        editorPane.setBackground(Color.DARK_GRAY);
        editorPane.setEditable(false);
        editorPane.setMargin(null);
        
        scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
        
        setLayout(new BorderLayout());
        add(scrollPane, "Center");
        
        final Thread t = new Thread(new BrowserLoader());
        t.setDaemon(true);
        t.start();
    }
    
    public LauncherAPI getApi()
    {
        return api;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public JTextPane getEditorPane()
    {
        return editorPane;
    }
    
    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }
    
    private class BrowserLoader implements Runnable
    {
        
        public void run()
        {
            try
            {
                editorPane.setPage(url);
            }
            catch (final IOException e)
            {
                editorPane
                        .setText("<html><body>Error during loading page. :(</body></html>");
                e.printStackTrace();
            }
        }
        
    }
}
