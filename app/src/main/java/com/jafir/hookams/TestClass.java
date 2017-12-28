package com.jafir.hookams;

/**
 * Created by jafir on 2017/12/28.
 */

public class TestClass {


    public static void main(String[] a){

        Class<Integer> c1 = (Class<Integer>) new Object().getClass();
        Class<? extends Class> cc1 = Object.class.getClass();
        Class<? extends Class> ccc1 = Integer.class.getClass();
    }
}
