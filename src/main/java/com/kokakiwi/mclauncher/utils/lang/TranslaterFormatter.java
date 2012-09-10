package com.kokakiwi.mclauncher.utils.lang;

import java.util.HashMap;
import java.util.Map;

public class TranslaterFormatter
{
    public static String format(String str, String[]... keys)
    {
        final Map<String, String> keyMap = new HashMap<String, String>();
        for (final String[] key : keys)
        {
            if (key.length == 2)
            {
                keyMap.put(key[0], key[1]);
            }
        }
        
        return format(str, keyMap);
    }
    
    public static String format(String str, Map<String, String> keys)
    {
        String result = str;
        
        if (keys != null)
        {
            for (final String key : keys.keySet())
            {
                result = result.replaceAll("\\{" + key.toUpperCase() + "\\}",
                        keys.get(key));
            }
        }
        
        return result;
    }
}
