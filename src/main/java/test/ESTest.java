package test;

import com.log.LoggerFactory;
import org.apache.log4j.Logger;


/**
 * Created by zl on 16/1/15.
 */


public class ESTest {
    private static final Logger logger = LoggerFactory.getInstance().getLogger(ESTest.class);

    private static String host="http://10.103.16.49:9200";
    private static String indexPrefix="fast_news_all";
    private static String document="document";
    private static String queryjson="/Users/a/querystr.data";
    private static String idsize="50000000";
    private static String targetfile="docid.data";
    private static String configfile="zl.properties";
    private static String startDate="2015-01-01 01:01:10";
    private static String endDate="2015-05-01 02:10:11";



    public static void main(String[] args){

    }
}
