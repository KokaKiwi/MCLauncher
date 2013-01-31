package com.kokakiwi.mclauncher.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.kokakiwi.mclauncher.MCLauncher;

public class Loginer
{
    private final MCLauncher main;
    private Login            lastLogin      = new Login();
    
    private String           storedUsername = null;
    private String           storedPassword = null;
    
    public Loginer(MCLauncher main)
    {
        this.main = main;
    }
    
    public void init()
    {
        try
        {
            readLogin();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // Actions
    
    public String doLogin(String username, String password, boolean rememberMe)
    {
        String result = null;
        
        final String url = main.getConfig().getString("login.url");
        
        final Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("user", username);
        params.put("password", password);
        params.put("version",
                main.getConfig().getString("login.launcherVersion"));
        
        final String request = main.getApi().postUrl(url, params);
        
        if (request.contains(":"))
        {
            final String[] parts = request.split(":");
            lastLogin = new Login();
            lastLogin.setTimestamp(Long.parseLong(parts[0]));
            lastLogin.setDownloadTicket(parts[1]);
            lastLogin.setUsername(parts[2]);
            lastLogin.setSessionId(parts[3]);
        }
        else
        {
            result = request;
        }
        
        if (result == null)
        {
            if (rememberMe)
            {
                try
                {
                    storedUsername = username;
                    storedPassword = password;
                    storeLogin(username, password);
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                deleteLogin();
            }
        }
        
        return result;
    }
    
    // Utils
    
    public void storeLogin(String username, String password) throws Exception
    {
        final File file = new File(main.getApi().getMinecraftDirectory(),
                "lastlogin");
        
        final Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, main.getConfig()
                .getString("login.encryptionKey"));
        final DataOutputStream out = new DataOutputStream(
                new CipherOutputStream(new FileOutputStream(file), cipher));
        out.writeUTF(username);
        out.writeUTF(password);
        out.close();
    }
    
    public void readLogin() throws Exception
    {
        final File file = new File(main.getApi().getMinecraftDirectory(),
                "lastlogin");
        
        if (file.exists())
        {
            final Cipher cipher = getCipher(Cipher.DECRYPT_MODE, main
                    .getConfig().getString("login.encryptionKey"));
            final DataInputStream in = new DataInputStream(
                    new CipherInputStream(new FileInputStream(file), cipher));
            storedUsername = in.readUTF();
            storedPassword = in.readUTF();
            
            in.close();
        }
    }
    
    private void deleteLogin()
    {
        final File file = new File(main.getApi().getMinecraftDirectory(),
                "bin/lastlogin");
        file.delete();
    }
    
    private Cipher getCipher(int mode, String password) throws Exception
    {
        final Random random = new Random(43287234L);
        final byte[] salt = new byte[8];
        random.nextBytes(salt);
        final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);
        
        final SecretKey pbeKey = SecretKeyFactory.getInstance(
                "PBEWithMD5AndDES").generateSecret(
                new PBEKeySpec(password.toCharArray()));
        final Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, pbeKey, pbeParamSpec);
        return cipher;
    }
    
    // Getter / Setter
    
    public MCLauncher getMain()
    {
        return main;
    }
    
    public Login getLastLogin()
    {
        return lastLogin;
    }
    
    public String getStoredUsername()
    {
        return storedUsername;
    }
    
    public String getStoredPassword()
    {
        return storedPassword;
    }
    
    public static class Login
    {
        private long   timestamp;
        private String downloadTicket;
        private String username;
        private String sessionId;
        
        public long getTimestamp()
        {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp)
        {
            this.timestamp = timestamp;
        }
        
        public String getDownloadTicket()
        {
            return downloadTicket;
        }
        
        public void setDownloadTicket(String downloadTicket)
        {
            this.downloadTicket = downloadTicket;
        }
        
        public String getUsername()
        {
            return username;
        }
        
        public void setUsername(String username)
        {
            this.username = username;
        }
        
        public String getSessionId()
        {
            return sessionId;
        }
        
        public void setSessionId(String sessionId)
        {
            this.sessionId = sessionId;
        }
    }
}
