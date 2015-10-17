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
                    Integer[] int_arr = intBuffer.toArray(new Integer[intBuffer.size()]);
                    handler.onReceive(int_arr);
                    break;
                case DOUBLE:
                    Double[] double_arr = doubleBuffer.toArray(new Double[doubleBuffer.size()]);
                    handler.onReceive(double_arr);
                    break;
                case STRING:
                    String[] string_arr = stringBuffer.toArray(new String[stringBuffer.size()]);
                    handler.onReceive(string_arr);
                    break;
                case BOOLEAN:
                    Boolean[] boolean_arr = booleanBuffer.toArray(new Boolean[booleanBuffer.size()]);
                    handler.onReceive(boolean_arr);
                    break;
                case OBJECT:
                    Object[] object_arr = objectBuffer.toArray(new Object[objectBuffer.size()]);
                    handler.onReceive(object_arr);
                    break;
                case VOID:
                    int voids = voidBuffer.get();
                    handler.onReceive(voids);
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
