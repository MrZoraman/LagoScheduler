package com.lagopusempire.lagoscheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LagoScheduler
{
    private static class InstanceHolder
    {
        private static final LagoScheduler INSTANCE = new LagoScheduler();
    }
    
    public static LagoScheduler getInstance()
    {
        return InstanceHolder.INSTANCE;
    }
    
    private final AtomicInteger tids = new AtomicInteger(0);
    private final ConcurrentMap<Integer, WaitingAsyncTask> asyncTasks = new ConcurrentHashMap<>();
    
    private LagoScheduler()
    {
        
    }
    
    public void runSyncTasks()
    {
        
    }
    
    public int spawnTaskSync(Runnable r)
    {
        return 0;
    }
    
    public int spawnWaitingAsyncTask(DataReceiveHandler handler)
    {
        final int tid = tids.getAndIncrement();
        
        WaitingAsyncTask task = new WaitingAsyncTask(() -> asyncTasks.remove(tid), handler);
        asyncTasks.put(tid, task);
        
        Thread thread = new Thread(task);
        thread.setDaemon(false);
        thread.start();
        
        return tid;
    }
    
    public boolean stop(int tid)
    {
        WaitingAsyncTask task = asyncTasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.stop();
        
        return true;
    }
    
    public boolean send(int tid, int i)
    {
        WaitingAsyncTask task = asyncTasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(i);
        
        return true;
    }
    
    public boolean send(int tid, double d)
    {
        WaitingAsyncTask task = asyncTasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(d);
        
        return true;
    }
    
    public boolean send(int tid, String str)
    {
        WaitingAsyncTask task = asyncTasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(str);
        
        return true;
    }
    
    public boolean send(int tid, boolean b)
    {
        WaitingAsyncTask task = asyncTasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(b);
        
        return true;
    }
    
    public boolean send(int tid)
    {
        WaitingAsyncTask task = asyncTasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send();
        
        return true;
    }
}
