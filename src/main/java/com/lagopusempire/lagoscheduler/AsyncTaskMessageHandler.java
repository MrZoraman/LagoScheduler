package com.lagopusempire.lagoscheduler;

public interface AsyncTaskMessageHandler
{
    default void onIntReceive(int i) { };
    default void onDoubleReceive(double d) { };
    default void onStringReceive(String s) { };
    default void onBooleanReceive(boolean b) { };
    default void onVoidReceive() { };
    
    default void onStart() { };
    default void onStop() { };
}
