package cn.pandacoder.gulimall.search;

import cn.pandacoder.gulimall.search.config.GulimallElasticSearchConfig;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);

    }

    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    @ToString
    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

    @Test
    void indexData() throws IOException {
        IndexRequest userIndexRequest = new IndexRequest("users");
        User user = new User();
        user.setAge(18);
        user.setGender("F");
        user.setUserName("lily");
        String jsonString = JSON.toJSONString(user);

        userIndexRequest.id("1");
        userIndexRequest.source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = client.index(userIndexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(indexResponse);
    }
    @Test
    void queryData() throws IOException {
        //构建查询请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("new_bank");

        //构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //构建queryBody
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
//        System.out.println(searchSourceBuilder.toString());

        //构建aggBody
        TermsAggregationBuilder groupByAge = AggregationBuilders.terms("group_by_age").field("age");
        AvgAggregationBuilder avgAge = AggregationBuilders.avg("avg_age").field("age");

        searchSourceBuilder.aggregation(groupByAge);
        searchSourceBuilder.aggregation(avgAge);
//        System.out.println(searchSourceBuilder.toString());

        //
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//        System.out.println(response);
//        JSON.parseObject(response.toString(), Map.class);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String sourceAsString = searchHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);
        }

        Aggregations aggregations = response.getAggregations();
        Terms groupByaAgeVal = aggregations.get("group_by_age");
        for (Terms.Bucket bucket : groupByaAgeVal.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+keyAsString);
        }

        Avg avgAgeVal = aggregations.get("avg_age");
        double value = avgAgeVal.getValue();
        System.out.println("平均年龄："+value);


    }


}
