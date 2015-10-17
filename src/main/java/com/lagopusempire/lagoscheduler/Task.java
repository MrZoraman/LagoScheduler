package com.lagopusempire.lagoscheduler;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

class Task implements TaskOperation
{
    private enum Types { INT, DOUBLE, STRING, BOOLEAN, OBJECT, VOID };
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final AtomicInteger intBuffer = new AtomicInteger(0);
    private final AtomicLong doubleBuffer = new AtomicLong(0);
    private final AtomicReference<String> stringBuffer = new AtomicReference<>();
    private final AtomicBoolean booleanBuffer = new AtomicBoolean(false);
    private final AtomicReference<Object> objectBuffer = new AtomicReference<>();
    
    private final CopyOnWriteArraySet<Types> typeUpdated = new CopyOnWriteArraySet<>();
    
    private final Runnable doneCallback;
    private final TaskBehaviorHandler handler;
    
    private final TaskOperation task;
    private final TaskRepeatInstructions repeatInstructions;
    
    Task(Runnable doneCallback, TaskBehaviorHandler handler, TaskOperation task, TaskRepeatInstructions repeatInstructions)
    {
        this.doneCallback = doneCallback;
        this.handler = handler;
        
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
            this.repeatInstructions = new TaskRepeatInstructions(0, 1, 1);
        }
        else
        {
            this.repeatInstructions = repeatInstructions;
        }
        
        handler.onStart();
    }
    
    public void stop()
    {
        handler.onStop();
        doneCallback.run();
        done.set(true);
    }
    
    @Override
    public boolean doTask()
    {
        if(done.get())
        {
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
                return true;
            }
        }
        
        if(!repeatInstructions.willRunAgain())
        {
            stop();
            return true;
        }
        
        return false;
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
                    handler.onReceive(intBuffer.get());
                    break;
                case DOUBLE:
                    double d = Double.longBitsToDouble(doubleBuffer.get());
                    handler.onReceive(d);
                    break;
                case STRING:
                    handler.onReceive(stringBuffer.get());
                    break;
                case BOOLEAN:
                    handler.onReceive(booleanBuffer.get());
                    break;
                case VOID:
                    handler.onReceive();
                    break;
            }
        }
        typeUpdated.clear();
    }
    
    void send(int i)
    {
        intBuffer.set(i);
        typeUpdated.add(Types.INT);
    }
    
    void send(double d)
    {
        long bits = Double.doubleToLongBits(d);
        doubleBuffer.set(bits);
        typeUpdated.add(Types.DOUBLE);
    }
    
    void send(String s)
    {
        stringBuffer.set(s);
        typeUpdated.add(Types.STRING);
    }
    
    void send(Boolean b)
    {
        booleanBuffer.set(b);
        typeUpdated.add(Types.BOOLEAN);
    }
    
    void send(Object o)
    {
        objectBuffer.set(o);
        typeUpdated.add(Types.OBJECT);
    }
    
    void send()
    {
        typeUpdated.add(Types.VOID);
    }
}
