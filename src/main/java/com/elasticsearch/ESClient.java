package com.elasticsearch;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.log.LoggerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.*;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.util.Print.*;

/**
 * Created by zl on 16/1/16.
 */


public class ESClient {
    private static final Logger logger = LoggerFactory.getInstance().getLogger(ESClient.class);
    private static final Config config= ConfigFactory.load().getConfig("es");
    private TransportClient esClient = null;
    private final String dateFormat= "^(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) [0-2][0-3]:[0-5][0-9]:[0-5][0-9]$";


    private String defaultIndexName = "fast_news_all";
    private String defaultType = "document";
    private String defaultStartDate = "2015-01-01 00:00:00";
    private String defaultEndDate = "2015-02-01 00:00:00";
    private String defaultTargetFile= "/Users/a/docids.data";

    /**
     * init es client
     * note:the config file must identify the two fields: cluster.name , cluster.nodes
     * */
    public boolean init(){
        if(!Strings.isNullOrEmpty(config.getString("cluster.nodes"))){
            String clusterName = config.getString("cluster.name");
            if(Strings.isNullOrEmpty(clusterName)){
                logger.error("init es client error: cluster.name is missing");
                return false;
            }
            else{
                Settings settings = ImmutableSettings.settingsBuilder()
                        .put("cluster.name", clusterName)
                        .put("client.transport.sniff", true)
                        .put("client.transport.ignore_cluster_name", true)
                        .build();
                TransportClient tmpClient= new TransportClient(settings);
                String clusternodes = config.getString("cluster.nodes");
                for(String item : clusternodes.split(",")){
                    String[] hostport = item.split(":");
                    String host = hostport[0];
                    int port = Integer.parseInt(hostport[1]);
                    tmpClient.addTransportAddress(new InetSocketTransportAddress(host,port));
                }
                esClient = tmpClient;
                logger.info(esClient.transportAddresses());
                return true;
            }
        }
        else{
            logger.error("init es client error: cluster.nodes is missing");
            return false;
        }
    }

    public List<String> getDocids(){
        String indexName = Strings.isNullOrEmpty(config.getString("index.name"))?defaultIndexName:config.getString("index.name");
        String document = Strings.isNullOrEmpty(config.getString("type"))?defaultType:config.getString("type");
        String start = Strings.isNullOrEmpty(config.getString("date.start"))?defaultStartDate:config.getString("date.start");
        String end = Strings.isNullOrEmpty(config.getString("date.end"))?defaultEndDate:config.getString("date.end");
        List<String> result = Lists.newArrayList();
        if(!checkDate(start) || !checkDate(end)){
            return result;
        }
        else {
            QueryBuilder qb = QueryBuilders.rangeQuery("date")
                    .gte(start)
                    .lte(end);
            Stopwatch watch = new Stopwatch();
            watch.start();
            SearchResponse scrollResp = esClient.prepareSearch(indexName)
                    .setTypes(document)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setScroll(new TimeValue(60000))
                    .setQuery(qb)
                    .setSize(100)
                    .execute()
                    .actionGet();
            watch.stop();
            logger.info("create scroll response latency: " + watch.elapsedTime(TimeUnit.MILLISECONDS) + "ms");
            watch.start();
            while (true) {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    result.add(hit.getId());
                    println(hit.getId());
                }
                scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
                if (scrollResp.getHits().getHits().length == 0) {
                    break;
                }
            }
            watch.stop();
            logger.info("get all hits latency: " + watch.elapsedTime(TimeUnit.MILLISECONDS) + "ms");
            println(result.size());
            return result;
        }
    }

    public List<String> getDocidsV2(){
        String indexName = Strings.isNullOrEmpty(config.getString("index.name"))?defaultIndexName:config.getString("index.name");
        String document = Strings.isNullOrEmpty(config.getString("type"))?defaultType:config.getString("type");
        String start = Strings.isNullOrEmpty(config.getString("date.start"))?defaultStartDate:config.getString("date.start");
        String end = Strings.isNullOrEmpty(config.getString("date.end"))?defaultEndDate:config.getString("date.end");
        List<String> result = Lists.newArrayList();
        if(!checkDate(start) || !checkDate(end)){
            return result;
        }
        else {
            PrintWriter writer = getFileHandler();
            if (writer == null) {
                logger.error("get store file handler error");
                return result;
            }

            SearchRequestBuilder search = esClient.prepareSearch(indexName)
                    .setTypes(document)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setScroll(new TimeValue(60000))
                    .setSize(100);
            Stopwatch watch = new Stopwatch();

            List<String> dateList = splitDate(start,end);
            String tmpS ;
            String tmpE ;
            Long time = 0L;
            for(int i=0;i<dateList.size()-1;++i) {
                tmpS = dateList.get(i);
                tmpE = dateList.get(i + 1);
                logger.info("fetch "+tmpS+"---"+tmpE+" doc ids");
                QueryBuilder qb = QueryBuilders.rangeQuery("date")
                        .gte(tmpS)
                        .lte(tmpE);
                watch.start();
                SearchResponse scrollResp = search.setQuery(qb).execute().actionGet();
                watch.stop();
                logger.info("create "+ (i+1) +"s scroll response latency: " + (watch.elapsedTime(TimeUnit.MILLISECONDS)-time) + "ms");
                time = watch.elapsedTime(TimeUnit.MILLISECONDS);
                watch.start();
                while (true) {
                    for (SearchHit hit : scrollResp.getHits().getHits()) {
                        writer.println(hit.getId());
                    }
                    scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
                    if (scrollResp.getHits().getHits().length == 0) {
                        break;
                    }
                }
                watch.stop();
                logger.info("store date "+tmpS+"---"+tmpE+" doc ids latency:" + (watch.elapsedTime(TimeUnit.MILLISECONDS)-time) + "ms");
                time = watch.elapsedTime(TimeUnit.MILLISECONDS);
            }
            return result;
        }
    }

    private void write2File(List<String> ids){
        if(ids.size() != 0) {
            String targetFile = config.getString("targetFile");
            if (!Strings.isNullOrEmpty(targetFile)) {
                File file = new File(targetFile);
                if (!file.exists()) {
                    logger.error("targetFile " + targetFile + " is not exist");
                    targetFile = defaultTargetFile;
                }
            } else {
                logger.error("paramter targetFile is missing,use default value");
                targetFile = defaultTargetFile;
            }
            try {
                PrintWriter out = new PrintWriter(new File(targetFile));
                for (String id : ids) {
                    out.println(id);
                }
                logger.info("store " + ids.size() + " ids in " + targetFile);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private PrintWriter getFileHandler(){
        PrintWriter out = null;
        String targetFile = config.getString("targetFile");
        if (!Strings.isNullOrEmpty(targetFile)) {
            File file = new File(targetFile);
            if (!file.exists()) {
                logger.error("targetFile " + targetFile + " is not exist");
                targetFile = defaultTargetFile;
            }
        } else {
            logger.error("paramter targetFile is missing,use default value");
            targetFile = defaultTargetFile;
        }
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(targetFile, true)));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
        return out;
    }

    private boolean checkDate(String date){
        Pattern pattern = Pattern.compile(dateFormat);
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches())
            return true;
        else {
            logger.error("date "+date+" is invalid");
            return false;
        }
    }

    private List<String> splitDate(String start,String end){
        List<String> lst = Lists.newArrayList();

        String splitFlag1 = " ";
        String splitFlag2 = "-";
        String splitFlag3 = ":";
        String[] startSplits = start.split(splitFlag1);
        String[] endSplits = end.split(splitFlag1);
        String[] startSplitDate = startSplits[0].split(splitFlag2);
        String[] endSplitDate = endSplits[0].split(splitFlag2);
        String[] startSplitTime = startSplits[1].split(splitFlag3);
        int syear = Integer.parseInt(startSplitDate[0]);
        int smonth = Integer.parseInt(startSplitDate[1]);
        int sday = Integer.parseInt(startSplitDate[2]);
        int shour = Integer.parseInt(startSplitTime[0]);
        int smin = Integer.parseInt(startSplitTime[1]);
        int ssec = Integer.parseInt(startSplitTime[2]);

        int eyear = Integer.parseInt(endSplitDate[0]);
        int emonth = Integer.parseInt(endSplitDate[1]);

        if(syear == eyear && smonth == emonth ){
            lst.add(start);
            lst.add(end);
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cr = Calendar.getInstance();
            cr.roll(Calendar.MONTH,false);
            int year = syear;
            for(int i = smonth; i < (eyear-syear)*12+emonth; ++i){
                if(i!=1 && (i-1)%12==0)
                    year+=1;
                cr.set(year,(i-1)%12,sday,shour,smin,ssec);
                lst.add(sdf.format(cr.getTime()));
            }
            lst.add(end);
        }
        return lst;

    }

    public static void main(String[] args){
        ESClient esClient = new ESClient();
        if(esClient.init()) {
            esClient.getDocidsV2();
        }
//        esClient.checkDate("2015-01-01 00:00:00");
//        esClient.splitDate("2015-05-01 00:00:00","2017-01-10 00:00:00");
    }
}
