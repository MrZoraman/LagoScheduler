package com.lagopusempire.lagoscheduler;

public interface DataReceiveHandler
{
    default void onReceive(int i) { }
    default void onReceive(double d) { }
    default void onReceive(String s) { }
    default void onReceive(boolean b) { }
    default void onReceive() { }
}
