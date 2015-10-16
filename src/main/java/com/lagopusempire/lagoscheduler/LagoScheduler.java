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
    private final ConcurrentMap<Integer, AsyncTask> asyncTasks = new ConcurrentHashMap<>();
    
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
    
    public int spawnTaskAsync(AsyncTask task)
    {
        final int tid = tids.getAndIncrement();
        asyncTasks.put(tid, task);
        
        task.setDoneCallback(() -> asyncTasks.remove(tid));
        
        Thread thread = new Thread(task);
        thread.setDaemon(false);
        thread.start();
        return tid;
    }
//    
//    public boolean sendInt(int tid, boolean b)
//    {
//    }
}
