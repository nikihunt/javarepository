package com.objenesis;


import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import static com.util.Print.println;
/**
 * Created by dell on 2015/11/24.
 */
public class objenesisExample {
    class Test{
        private String user;
        private String passwd;
        public Test() {
            println("non-arg constructor is called!");
        }
        public void say(){
            println("hello objenesis");
        }
    }
    public static void main(String[] args){
        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator instantiator = objenesis.getInstantiatorOf(Test.class);
        Test t = (Test)instantiator.newInstance();
        t.say();
    }
}
