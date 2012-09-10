package com.kokakiwi.mclauncher.ui.simple;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.core.launcher.ModsManager;
import com.kokakiwi.mclauncher.core.launcher.ModsManager.Mod;
import com.kokakiwi.mclauncher.utils.lang.Translater;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.JList;
import javax.swing.JButton;

public class ModsDialog extends JDialog
{
    private static final long                 serialVersionUID = 292853342724397034L;
    
    private final LauncherAPI                 api;
    
    private JPanel                            actionPanel;
    
    private JList<Mod>                        modsList;
    
    private DefaultListModel<ModsManager.Mod> model;
    
    public ModsDialog(OptionsDialog parent)
    {
        super(parent);
        api = parent.getApi();
        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent event)
            {
                setVisible(false);
            }
        });
        
        setTitle(Translater.getString("options.mods.title"));
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setMinimumSize(new Dimension(420, 250));
        
        JPanel panel = new JPanel(new BorderLayout());
        
        panel.setBorder(new EmptyBorder(16, 24, 24, 24));
        getContentPane().add(panel);
        
        JLabel label = new JLabel(Translater.getString("options.mods.title"));
        label.setBorder(new EmptyBorder(0, 0, 16, 0));
        label.setFont(new Font("Default", 1, 16));
        panel.add(label, BorderLayout.NORTH);
        
        JPanel modsPanel = new JPanel();
        panel.add(modsPanel, BorderLayout.CENTER);
        modsPanel.setLayout(new BoxLayout(modsPanel, BoxLayout.X_AXIS));
        
        JPanel modsListPanel = new JPanel();
        modsListPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        modsPanel.add(modsListPanel);
        modsListPanel.setLayout(new BorderLayout(0, 0));
        
        actionPanel = new JPanel();
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        final ModsManager mods = parent.getApi().getLauncher().getMods();
        model = new DefaultListModel<ModsManager.Mod>();
        for (ModsManager.Mod mod : mods.getMods())
        {
            model.addElement(mod);
        }
        
        modsList = new JList<ModsManager.Mod>(model);
        modsList.setAutoscrolls(true);
        modsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(modsList);
        modsListPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel actionsPanel = new JPanel();
        modsPanel.add(actionsPanel);
        actionsPanel.setLayout(new GridLayout(0, 1, 5, 5));
        
        JButton addModButton = new JButton("Add mod");
        addModButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                actionPanel.removeAll();
                fillAddMod(actionPanel);
                
                validate();
            }
        });
        actionsPanel.add(addModButton);
        
        JButton editModButton = new JButton("Edit mod");
        editModButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                actionPanel.removeAll();
                fillEditMod(actionPanel);
                
                validate();
            }
        });
        actionsPanel.add(editModButton);
        
        JButton removeModButton = new JButton("Remove mod");
        removeModButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                actionPanel.removeAll();
                
                if (modsList.getSelectedValue() != null)
                {
                    ModsManager.Mod selected = modsList.getSelectedValue();
                    model.removeElement(selected);
                    mods.getMods().remove(selected);
                }
                
                validate();
            }
        });
        actionsPanel.add(removeModButton);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void fillAddMod(JPanel panel)
    {
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout(5, 5));
        
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(0, 1, 5, 5));
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0, 1, 5, 5));
        
        ModsManager.Mod mod = new ModsManager.Mod();
        api.getLauncher().getMods().addMod(mod);
        fillEditModForm(left, right, mod);
        
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);
    }
    
    private void fillEditMod(JPanel panel)
    {
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout(5, 5));
        
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(0, 1, 5, 5));
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0, 1, 5, 5));
        
        ModsManager.Mod mod = modsList.getSelectedValue();
        if (mod != null)
        {
            fillEditModForm(left, right, mod);
        }
        
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);
    }
    
    private void fillEditModForm(JPanel left, JPanel right,
            final ModsManager.Mod mod)
    {
        left.add(new JLabel("Name :"));
        left.add(new JLabel("URL :"));
        left.add(new JLabel(""));
        
        final JTextField nameField = new JTextField(mod.getName());
        right.add(nameField);
        
        final JTextField urlField = new JTextField(mod.getUrl() == null ? ""
                : mod.getUrl().toString());
        right.add(urlField);
        
        JButton valid = new JButton("Save");
        valid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    mod.setName(nameField.getText());
                    mod.setUrl(new URL(urlField.getText()));
                    actionPanel.removeAll();
                    
                    if (!model.contains(mod))
                    {
                        model.addElement(mod);
                    }
                    
                    modsList.revalidate();
                    modsList.repaint();
                    
                    validate();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        right.add(valid);
    }
}
