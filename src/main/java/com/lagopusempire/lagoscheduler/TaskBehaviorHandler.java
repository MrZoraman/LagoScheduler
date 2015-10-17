package com.lagopusempire.lagoscheduler;

public interface TaskBehaviorHandler
{
    default void onReceive(int[] i) { }
    default void onReceive(double[] d) { }
    default void onReceive(String[] s) { }
    default void onReceive(boolean[] b) { }
    default void onReceive(Object[] o) { }
    default void onReceive(int voids) { }
    
    default void onStart() { }
    default void onStop() { }
}
