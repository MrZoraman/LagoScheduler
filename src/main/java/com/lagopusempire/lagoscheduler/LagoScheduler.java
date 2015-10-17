package com.lagopusempire.lagoscheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    
    private static final int THREAD_POOL_SIZE = 16;
    
    private final AtomicInteger tids = new AtomicInteger(0);
    private final ConcurrentMap<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<Runnable> runOnceSyncRunnables = new CopyOnWriteArraySet<>();
    private final ExecutorService runOnceAsyncExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    
    private LagoScheduler()
    {
        
    }
    
    public void runSyncTasks()
    {
        runOnceSyncRunnables.forEach(r -> r.run());
        runOnceSyncRunnables.clear();
        
        
    }
    
    public int spwanWaitingSyncTask(TaskBehaviorHandler handler, int ticksPerInterval)
    {
        int tid = tids.getAndIncrement();
        
        WaitingSyncTask task = new WaitingSyncTask(() -> tasks.remove(tid), handler);
        tasks.put(tid, task);
        
        return tid;
    }
    
    public void spawnRunOnceAsyncTask(Runnable r, boolean spawnThread)
    {
        if(spawnThread)
        {
            Thread t = new Thread(r);
            t.setDaemon(false);
            t.start();
        }
        else
        {
            runOnceAsyncExecutor.execute(r);
        }
    }
    
    public void spawnRunOnceTaskSync(Runnable r)
    {
        runOnceSyncRunnables.add(r);
    }
    
    public int spawnWaitingAsyncTask(TaskBehaviorHandler handler)
    {
        final int tid = tids.getAndIncrement();
        
        WaitingAsyncTask task = new WaitingAsyncTask(() -> tasks.remove(tid), handler);
        tasks.put(tid, task);
        
        Thread thread = new Thread(task);
        thread.setDaemon(false);
        thread.start();
        
        return tid;
    }
    
    public boolean stop(int tid)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.stop();
        
        return true;
    }
    
    public boolean send(int tid, int i)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(i);
        
        return true;
    }
    
    public boolean send(int tid, double d)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(d);
        
        return true;
    }
    
    public boolean send(int tid, String str)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(str);
        
        return true;
    }
    
    public boolean send(int tid, boolean b)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(b);
        
        return true;
    }
    
    public boolean send(int tid)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send();
        
        return true;
    }
}
