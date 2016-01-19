package com.log;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import lombok.Getter;

import java.io.File;

import static com.util.Print.*;

/**
 * Created by zl on 16/1/12.
 */


public class LoggerFactory {
    private volatile static LoggerFactory instance;
    @Getter
    private String propertyPath = "";

    public static LoggerFactory getInstance(){
        if(instance == null){
            synchronized (LoggerFactory.class){
                if(instance == null)
                    instance = new LoggerFactory();
            }
        }
        return instance;
    }


    private LoggerFactory(){
        Config config = ConfigFactory.load();
        Config pathConf = config.getConfig("log4j");
        if(pathConf != null){
            if(pathConf.getString("confpath") != null)
                propertyPath = pathConf.getString("confpath");
        }

        File file = new File("log4j.properties");
        if( !Strings.isNullOrEmpty(propertyPath) ) {
            PropertyConfigurator.configure(LoggerFactory.class.getResource(propertyPath));
        } else if ( file.exists() ){
            PropertyConfigurator.configure(file.toString());
        } else {
            PropertyConfigurator.configure(LoggerFactory.class.getResource("/log4j.properties"));
        }
    }

    public <T> Logger getLogger(Class<T> cls){
        return Logger.getLogger(cls);
    }

    public static void main(String[] args){
        LoggerFactory logFactory = LoggerFactory.getInstance();
        println(logFactory.getPropertyPath());
    }
}
