package com.alibaba.otter.canal.client.adapter.es.test.sync;

import java.util.*;

import org.elasticsearch.action.get.GetResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.otter.canal.client.adapter.es.ESAdapter;
import com.alibaba.otter.canal.client.adapter.support.AdapterConfigs;
import com.alibaba.otter.canal.client.adapter.support.DatasourceConfig;
import com.alibaba.otter.canal.client.adapter.support.Dml;

public class RoleSyncJoinSub2Test {

    private ESAdapter esAdapter;

    @Before
    public void init() {
        AdapterConfigs.put("es", "mytest_user_join_sub2.yml");
        esAdapter = Common.init();
    }

    /**
     * 子查询从表插入 (确保主表记录必须有数据)
     */
    @Test
    public void insertTest01() {
        Dml dml = new Dml();
        dml.setDestination("example");
        dml.setTs(new Date().getTime());
        dml.setType("INSERT");
        dml.setDatabase("mytest");
        dml.setTable("label");
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data = new LinkedHashMap<>();
        dataList.add(data);
        data.put("id", 1L);
        data.put("user_id",1L);
        data.put("label", "a");

        dml.setData(dataList);

        esAdapter.getEsSyncService().sync(dml);

        GetResponse response = esAdapter.getTransportClient().prepareGet("mytest_user", "_doc", "1").get();
        Assert.assertEquals("a;b_", response.getSource().get("_labels"));
    }

    @After
    public void after() {
        esAdapter.destroy();
        DatasourceConfig.DATA_SOURCES.values().forEach(DruidDataSource::close);
    }
}
