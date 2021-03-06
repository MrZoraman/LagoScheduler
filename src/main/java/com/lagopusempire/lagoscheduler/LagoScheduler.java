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
    
    public void stop()
    {
        done.set(true);
        tasks.values().forEach(task -> task.stop());
    }
    
    public int spawnSyncTask(TaskBehaviorHandler handler, TaskOperation toDo, TaskRepeatInstructions repeatInstructions)
    {
        int tid = tids.getAndIncrement();
        
        Task task = new Task(() -> {
            tasks.remove(tid);
            syncTasks.remove(tid);
        }, handler, toDo, repeatInstructions);
        
        if(handler != null)
        {
            handler.setTid(tid);
        }
        
        tasks.put(tid, task);
        syncTasks.put(tid, task);
        
        return tid;
    }
    
    public int spawnSyncTask(TaskBehaviorHandler handler, TaskOperation toDo)
    {
        return spawnSyncTask(handler, toDo, null);
    }
    
    public int spawnSyncTask(TaskOperation toDo, TaskRepeatInstructions repeatInstructions)
    {
        return spawnSyncTask(null, toDo, repeatInstructions);
    }
    
    public int spawnSyncTask(TaskBehaviorHandler handler, TaskRepeatInstructions repeatInstructions)
    {
        return spawnSyncTask(handler, null, repeatInstructions);
    }
    
    public int spawnSyncTask(TaskOperation toDo)
    {
        return spawnSyncTask(null, toDo, null);
    }
    
    public int spawnSyncTask(TaskBehaviorHandler handler, Runnable runnable)
    {
        return spawnSyncTask(handler, new TaskOperationWrapper(runnable), TaskRepeatInstructions.FOREVER());
    }
    
    public int spawnSyncTask(Runnable runnable)
    {
        return spawnSyncTask(null, new TaskOperationWrapper(runnable), TaskRepeatInstructions.FOREVER());
    }
    
    public int spawnAsyncTask(boolean threadPool, TaskBehaviorHandler handler, TaskOperation toDo, TaskRepeatInstructions repeatInstructions)
    {
        int tid = tids.getAndIncrement();
        
        Task task = new Task(() -> {
            tasks.remove(tid);
        }, handler, toDo, repeatInstructions);
        
        if(handler != null)
        {
            handler.setTid(tid);
        }
        
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
    
    public int spawnAsyncTask(boolean threadPool, TaskBehaviorHandler handler, TaskOperation toDo)
    {
        return spawnAsyncTask(threadPool, handler, toDo, null);
    }
    
    public int spawnAsyncTask(boolean threadPool, TaskOperation toDo, TaskRepeatInstructions repeatInstructions)
    {
        return spawnAsyncTask(threadPool, null, toDo, repeatInstructions);
    }
    
    public int spawnAsyncTask(boolean threadPool, TaskBehaviorHandler handler, TaskRepeatInstructions repeatInstructions)
    {
        return spawnAsyncTask(threadPool, handler, null, repeatInstructions);
    }
    
    public int spawnAsyncTask(boolean threadPool, TaskOperation toDo)
    {
        return spawnAsyncTask(threadPool, null, toDo, null);
    }
    
    public int spawnAsyncTask(boolean threadPool, TaskBehaviorHandler handler, Runnable runnable)
    {
        return spawnAsyncTask(threadPool, handler, new TaskOperationWrapper(runnable), TaskRepeatInstructions.FOREVER());
    }
    
    public int spawnAsyncTask(boolean threadPool, Runnable runnable)
    {
        return spawnAsyncTask(threadPool, null, new TaskOperationWrapper(runnable), TaskRepeatInstructions.FOREVER());
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
