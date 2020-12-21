package cn.pandacoder.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * PUT /new_bank
 * {
 * "mappings": {
 * "properties": {
 * "account_number": {
 * "type": "integer"
 * },
 * "address": {
 * "type": "text"
 * },
 * "age": {
 * "type": "integer"
 * },
 * "balance": {
 * "type": "long"
 * },
 * "city": {
 * "type": "keyword"
 * },
 * "email": {
 * "type": "keyword"
 * },
 * "employer": {
 * "type": "text",
 * "fields": {
 * "keyword": {
 * "type": "keyword",
 * "ignore_above": 256
 * }
 * }
 * },
 * "firstname": {
 * "type": "text",
 * "fields": {
 * "keyword": {
 * "type": "keyword",
 * "ignore_above": 256
 * }
 * }
 * },
 * "gender": {
 * "type": "keyword"
 * },
 * "lastname": {
 * "type": "text",
 * "fields": {
 * "keyword": {
 * "type": "keyword",
 * "ignore_above": 256
 * }
 * }
 * },
 * "state": {
 * "type": "keyword"
 * }
 * }
 * }
 * }
 * <p>
 * POST _reindex
 * {
 * "source": {
 * "index": "bank",
 * "type": "external"
 * },
 * "dest": {
 * "index": "new_bank"
 * }
 * }
 * <p>
 * GET /new_bank/_search
 * <p>
 * POST _analyze
 * {
 * "tokenizer": "standard",
 * "text": "this is a new day that i will finish the work!!"
 * }
 * <p>
 * GET /users/_search
 * {
 * "query": {
 * "match": {
 * "address": {
 * "query": "mill",
 * "operator": "OR",
 * "prefix_length": 0,
 * "max_expansions": 50,
 * "fuzzy_transpositions": true,
 * "lenient": false,
 * "zero_terms_query": "NONE",
 * "auto_generate_synonyms_phrase_query": true,
 * "boost": 1
 * }
 * }
 * }
 * }
 * <p>
 * PUT product
 * {
 * "mappings":{
 * "properties": {
 * "skuId":{
 * "type": "long"
 * },
 * "spuId":{
 * "type": "keyword"
 * },
 * "skuTitle": {
 * "type": "text",
 * "analyzer": "ik_smart"
 * },
 * "skuPrice": {
 * "type": "keyword"
 * },
 * "skuImg":{
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "saleCount":{
 * "type":"long"
 * },
 * "hasStock": {
 * "type": "boolean"
 * },
 * "hotScore": {
 * "type": "long"
 * },
 * "brandId": {
 * "type": "long"
 * },
 * "catalogId": {
 * "type": "long"
 * },
 * "brandName": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "brandImg":{
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "catalogName": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "attrs": {
 * "type": "nested",
 * "properties": {
 * "attrId": {
 * "type": "long"
 * },
 * "attrName": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "attrValue": {
 * "type": "keyword"
 * }
 * }
 * }
 * }
 * }
 * }
 */
@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;

    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;

    private Long brandId;
    private String brandName;
    private String brandImg;

    private Long catalogId;
    private String catalogName;

    private List<Attrs> attrs;

    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
