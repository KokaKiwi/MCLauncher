package com.kokakiwi.mclauncher.utils;

public class Version implements Comparable<Version>
{
    private final long[] nums;
    
    public Version(long... is)
    {
        nums = is;
    }
    
    public long[] getVersionNumbers()
    {
        return nums;
    }
    
    public static Version parseString(String version)
    {
        final String[] splitted = version.split("\\.");
        final long[] nums = new long[splitted.length];
        for (int i = 0; i < splitted.length; i++)
        {
            nums[i] = Long.parseLong(splitted[i]);
        }
        
        return new Version(nums);
    }
    
    
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < nums.length; i++)
        {
            sb.append(String.valueOf(nums[i]));
            if (i < nums.length - 1)
            {
                sb.append('.');
            }
        }
        
        return sb.toString();
    }
    
    public int compareTo(Version arg)
    {
        final long minNums = Math.min(nums.length,
                arg.getVersionNumbers().length);
        
        int diff = 0;
        for (int i = 0; i < minNums; i++)
        {
            if (nums[i] > arg.getVersionNumbers()[i])
            {
                diff++;
                break;
            }
            else if (nums[i] < arg.getVersionNumbers()[i])
            {
                diff--;
                break;
            }
            else
            {
                continue;
            }
        }
        
        if (diff == 0)
        {
            if (nums.length > arg.getVersionNumbers().length)
            {
                diff++;
            }
            else if (nums.length < arg.getVersionNumbers().length)
            {
                diff--;
            }
        }
        
        return diff;
    }
    
}
