package com.lagopusempire.lagoscheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LagoScheduler implements TaskOperation, Runnable
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
    private static final int TICKS_PER_SECOND = 20;
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final AtomicInteger tids = new AtomicInteger(0);
    private final ConcurrentMap<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final ConcurrentMap<Integer, Task> syncTasks = new ConcurrentHashMap<>();
    
    private LagoScheduler()
    {
        
    }
    
    @Override
    public void run()
    {
        TaskOperationRepeater repeater = new TaskOperationRepeater(this, TICKS_PER_SECOND);
        repeater.run();
    }
    
    @Override
    public boolean doTask()
    {
        syncTasks.values().forEach(task -> task.doTask());
        return done.get();
    }
    
    public void runSyncTasks()
    {
        doTask();
    }
    
    public int spawnSyncTask(TaskBehaviorHandler handler, TaskOperation toDo, TaskRepeatInstructions repeatInstructions)
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
    
    public int spawnAsyncTask(boolean threadPool, TaskBehaviorHandler handler, TaskOperation toDo, TaskRepeatInstructions repeatInstructions)
    {
        int tid = tids.getAndIncrement();
        
        Task task = new Task(() -> {
            tasks.remove(tid);
        }, handler, toDo, repeatInstructions);
        
        tasks.put(tid, task);
        
        TaskOperationRepeater taskRepeater = new TaskOperationRepeater(task, TICKS_PER_SECOND);
        
        if(threadPool)
        {
            asyncExecutor.execute(taskRepeater);
        }
        else
        {
            Thread t = new Thread(taskRepeater);
            t.setDaemon(false);
            t.start();
        }
        
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
    
    public boolean send(int tid, Object o)
    {
        Task task = tasks.get(tid);
        if(task == null)
        {
            return false;
        }
        
        task.send(o);
        
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
