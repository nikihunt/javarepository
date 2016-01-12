package test;

import com.Log.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 * Created by zl on 16/1/12.
 */


public class LogTest {
    public static void main(String[] args){
        Logger logger = LogFactory.getInstance().getLogger(LogTest.class);
        logger.info("first log!!!");
    }
}
