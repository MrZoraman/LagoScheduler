package com.lagopusempire.lagoscheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    //private final CopyOnWriteArraySet<Runnable> runOnceSyncRunnables = new CopyOnWriteArraySet<>();
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final ConcurrentMap<Integer, Task> syncTasks = new ConcurrentHashMap<>();
    
    private LagoScheduler()
    {
        
    }
    
    public void runSyncTasks()
    {
        //runOnceSyncRunnables.forEach(r -> r.run());
        //runOnceSyncRunnables.clear();
        
        syncTasks.values().forEach(task -> task.tick());
        
        
    }
    
    public int spawnSyncTask(TaskBehaviorHandler handler, Runnable toDo, TaskRepeatInstructions repeatInstructions)
    {
        int tid = tids.getAndIncrement();
        
        Task task = new Task(() -> {
            tasks.remove(tid);
            syncTasks.remove(tid);
        }, handler, toDo, repeatInstructions);
        
        tasks.put(tid, task);
        syncTasks.put(tid, task);
        
        return tid;
    }
    
    public int spawnAsyncTask(boolean threadPool, TaskBehaviorHandler handler, Runnable toDo, TaskRepeatInstructions repeatInstructions)
    {
        int tid = tids.getAndIncrement();
        
        Task task = new Task(() -> {
            tasks.remove(tid);
        }, handler, toDo, repeatInstructions);
        
        tasks.put(tid, task);
        
//        if(threadPool)
//        {
//            asyncExecutor.execute(task);
//        }
//        else
//        {
//            Thread t = new Thread(task);
//            t.setDaemon(false);
//            t.start();
//        }
        
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
            asyncExecutor.execute(r);
        }
    }
    
    public void spawnRunOnceSyncTask(Runnable r)
    {
        //runOnceSyncRunnables.add(r);
    }
    
    public int spawnWaitingAsyncTask(TaskBehaviorHandler handler)
    {
        final int tid = tids.getAndIncrement();
        
        Task task = new Task(() -> tasks.remove(tid), handler, null, null);
        tasks.put(tid, task);
        
//        Thread thread = new Thread(task);
//        thread.setDaemon(false);
//        thread.start();
        
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
