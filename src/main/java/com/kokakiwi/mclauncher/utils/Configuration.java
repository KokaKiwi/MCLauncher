package com.kokakiwi.mclauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

public class Configuration
{
    private final Map<String, Object> config = new HashMap<String, Object>();
    
    public boolean load(File file)
    {
        return load(file, true);
    }
    
    public boolean load(File file, boolean overwrite)
    {
        final String ext = file.getName().substring(
                file.getName().lastIndexOf(".") + 1);
        String type;
        if (ext.equals("yml"))
        {
            type = "yaml";
        }
        else
        {
            type = "";
        }
        try
        {
            return load(new FileInputStream(file), type);
        }
        catch (final FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean load(InputStream inputStream)
    {
        return load(inputStream, true);
    }
    
    public boolean load(InputStream inputStream, boolean overwrite)
    {
        return load(inputStream, "", overwrite);
    }
    
    public boolean load(InputStream inputFile, String type)
    {
        return load(inputFile, type, true);
    }
    
    @SuppressWarnings("unchecked")
    public boolean load(InputStream inputFile, String type, boolean overwrite)
    {
        if (type.equalsIgnoreCase("yaml"))
        {
            final Yaml yamlParser = new Yaml();
            final Map<String, Object> data = (Map<String, Object>) yamlParser
                    .load(inputFile);
            if (data != null)
            {
                merge(data, config, overwrite);
            }
        }
        else
        {
            final Properties props = new Properties();
            
            try
            {
                props.load(inputFile);
            }
            catch (final IOException e)
            {
                e.printStackTrace();
                return false;
            }
            
            for (final Object key : props.stringPropertyNames())
            {
                final String name = key.toString();
                final String value = props.getProperty(name);
                
                config.put(name, value);
            }
        }
        
        return true;
    }
    
    public void load(Configuration config)
    {
        load(config, true);
    }
    
    public void load(Configuration config, boolean overwrite)
    {
        merge(config.getConfig(), this.config, overwrite);
    }
    
    public void merge(Map<String, Object> from, Map<String, Object> to)
    {
        merge(from, to, true);
    }
    
    @SuppressWarnings("unchecked")
    public void merge(Map<String, Object> from, Map<String, Object> to,
            boolean overwrite)
    {
        for (final String key : from.keySet())
        {
            if (to.get(key) == null)
            {
                to.put(key, from.get(key));
            }
            else
            {
                if (to.get(key) instanceof Map)
                {
                    merge((Map<String, Object>) from.get(key),
                            (Map<String, Object>) to.get(key), overwrite);
                }
                else
                {
                    if (overwrite)
                    {
                        to.put(key, from.get(key));
                    }
                }
            }
        }
    }
    
    public void set(String name, Object value)
    {
        config.put(name, value);
    }
    
    /**
     * Return String property. Or null if node doesn't exists.
     * 
     * @param name
     *            Node name
     * @return Node value, null if doesn't exists.
     */
    public String getString(String name)
    {
        return getString(name, null);
    }
    
    public String getString(String name, String def)
    {
        final Object value = get(name);
        
        return value == null ? def : value.toString();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getList(String name)
    {
        return (List<Object>) get(name);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String name)
    {
        return (List<String>) get(name);
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMapList(String name)
    {
        return (List<Map<String, Object>>) get(name);
    }
    
    public Map<String, String> getStringMap(String name)
    {
        final Map<String, String> map = new HashMap<String, String>();
        
        for (final Entry<String, Object> entry : getNode(name).entrySet())
        {
            if (entry.getValue() instanceof String)
            {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        }
        
        return map;
    }
    
    public Map<String, Object> getMap(String name)
    {
        return getNode(name);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getNode(String name)
    {
        return (Map<String, Object>) get(name);
    }
    
    public boolean getBoolean(String name)
    {
        return getBoolean(name, false);
    }
    
    public boolean getBoolean(String name, boolean def)
    {
        return (Boolean) (get(name) == null ? def : get(name));
    }
    
    public int getInteger(String name)
    {
        return (Integer) get(name);
    }
    
    @SuppressWarnings("unchecked")
    public Object get(String nodeName)
    {
        Object result = null;
        
        if (config.containsKey(nodeName))
        {
            result = config.get(nodeName);
        }
        else
        {
            if (nodeName.contains("."))
            {
                final String[] nodes = nodeName.split("\\.");
                Object currentNode = null;
                
                for (final String node : nodes)
                {
                    if (currentNode == null)
                    {
                        currentNode = config.get(node);
                    }
                    else
                    {
                        if (currentNode instanceof Map)
                        {
                            currentNode = ((Map<String, Object>) currentNode)
                                    .get(node);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                
                result = currentNode;
            }
            else
            {
                result = config.get(nodeName);
            }
        }
        
        return result;
    }
    
    public Map<String, Object> getConfig()
    {
        return config;
    }
    
    public boolean has(String node)
    {
        return get(node) != null;
    }
    
    public void clear()
    {
        config.clear();
    }
    
    public void save(File file) throws Exception
    {
        final OutputStream out = new FileOutputStream(file);
        final Yaml yaml = new Yaml();
        final byte[] data = yaml.dump(config).getBytes("UTF-8");
        out.write(data);
        out.close();
    }
    
    public static Configuration getLauncherConfiguration()
    {
        final Configuration config = new Configuration();
        
        // Load JavaConfig class.
        try
        {
            Class<?> clazz = configClass;
            if (clazz == null)
            {
                clazz = Configuration.class.getClassLoader().loadClass(
                        "com.kokakiwi.mclauncher.core.JavaConfig");
            }
            final Method method = clazz.getMethod("config");
            final Configuration javaConfig = (Configuration) method
                    .invoke(null);
            
            if (javaConfig.getBoolean("load.user-config"))
            {
                config.load(Configuration.class
                        .getResourceAsStream("/config/default.yml"), "yaml");
            }
            
            config.load(javaConfig);
        }
        catch (final Exception e)
        {
            config.load(Configuration.class
                    .getResourceAsStream("/config/default.yml"), "yaml");
        }
        
        return config;
    }
    
    private static Class<?> configClass = null;
    
    public static void setConfigClass(Class<?> configClass)
    {
        Configuration.configClass = configClass;
    }
}
