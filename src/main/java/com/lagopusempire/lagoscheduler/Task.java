package com.lagopusempire.lagoscheduler;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class Task implements TaskOperation
{
    private enum Types { INT, DOUBLE, STRING, BOOLEAN, OBJECT, VOID };
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final CopyOnWriteArraySet<Integer> intBuffer = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Double> doubleBuffer = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<String> stringBuffer = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Boolean> booleanBuffer = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Object> objectBuffer = new CopyOnWriteArraySet<>();
    private final AtomicInteger voidBuffer = new AtomicInteger(0);
    
    private final AtomicBoolean starting = new AtomicBoolean(true);
    
    private final CopyOnWriteArraySet<Types> typeUpdated = new CopyOnWriteArraySet<>();
    
    private final Runnable doneCallback;
    private final TaskBehaviorHandler handler;
    
    private final TaskOperation task;
    private final TaskRepeatInstructions repeatInstructions;
    
    Task(Runnable doneCallback, TaskBehaviorHandler handler, TaskOperation task, TaskRepeatInstructions repeatInstructions)
    {
        this.doneCallback = doneCallback;
        
        if(handler == null)
        {
            this.handler = new TaskBehaviorHandler(){};
        }
        else
        {
            this.handler = handler;
        }
        
        if(task == null)
        {
            this.task = () -> {return false;};
        }
        else
        {
            this.task = task;
        }
        
        if(repeatInstructions == null)
        {
            this.repeatInstructions = TaskRepeatInstructions.ONCE();
        }
        else
        {
            this.repeatInstructions = repeatInstructions;
        }
    }
    
    public void stop()
    {
        doneCallback.run();
        done.set(true);
    }
    
    @Override
    public boolean doTask()
    {
        if(starting.get())
        {
            handler.onStart();
            starting.set(false);
        }
        
        if(done.get())
        {
            handler.onStop();
            return true;
        }
        
        notifyHandlerMethods();
        
        repeatInstructions.cycle();
        if(repeatInstructions.shouldRun())
        {
            boolean result = task.doTask();
            if(result)
            {
                stop();
                return false;
            }
        }
        
        if(!repeatInstructions.willRunAgain())
        {
            stop();
            return false;
        }
        
        return false;
    }
    
    private void clearBuffers()
    {
        typeUpdated.clear();
        intBuffer.clear();
        doubleBuffer.clear();
        stringBuffer.clear();
        booleanBuffer.clear();
        objectBuffer.clear();
        voidBuffer.set(0);
    }
    
    private void notifyHandlerMethods()
    {
        final Iterator<Types> it = typeUpdated.iterator();
        while(it.hasNext())
        {
            Types type = it.next();
            switch(type)
            {
                case INT:
                    intBuffer.forEach(num -> handler.onReceive(num.intValue()));
                    break;
                case DOUBLE:
                    doubleBuffer.forEach(num -> handler.onReceive(num.doubleValue()));
                    break;
                case STRING:
                    stringBuffer.forEach(str -> handler.onReceive(str));
                    break;
                case BOOLEAN:
                    booleanBuffer.forEach(bool -> handler.onReceive(bool.booleanValue()));
                    break;
                case OBJECT:
                    objectBuffer.forEach(obj -> handler.onReceive(obj));
                    break;
                case VOID:
                    for(int ii = 0; ii < voidBuffer.get(); ii++)
                    {
                        handler.onReceive();
                    }
                    break;
            }
        }
        clearBuffers();
    }
    
    void send(int i)
    {
        intBuffer.add(i);
        typeUpdated.add(Types.INT);
    }
    
    void send(double d)
    {
        doubleBuffer.add(d);
        typeUpdated.add(Types.DOUBLE);
    }
    
    void send(String s)
    {
        stringBuffer.add(s);
        typeUpdated.add(Types.STRING);
    }
    
    void send(Boolean b)
    {
        booleanBuffer.add(b);
        typeUpdated.add(Types.BOOLEAN);
    }
    
    void send(Object o)
    {
        objectBuffer.add(o);
        typeUpdated.add(Types.OBJECT);
    }
    
    void send()
    {
        typeUpdated.add(Types.VOID);
        voidBuffer.incrementAndGet();
    }
}
