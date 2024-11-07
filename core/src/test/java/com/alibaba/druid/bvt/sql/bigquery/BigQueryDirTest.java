package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import com.alibaba.druid.sql.SQLUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

public class BigQueryDirTest extends SQLResourceTest {
    public BigQueryDirTest() {
        super(DbType.bigquery);
    }

    protected File dir(String path) {
        return new File(path);
    }

    @Test
    public void dirTest() throws Exception {
//        File dir = new File("/Users/wenshao/Downloads/goto_1894_sql");
//        File dir = new File("/Users/wenshao/Downloads/BigQuery");
//        File[] files = dir.listFiles();
//        Arrays.sort(files, Comparator.comparing(e -> e.getName().toLowerCase()));
//        for (File file : files) {
//            if (file.getName().equals(".DS_Store")) {
//                continue;
//            }
//
//            System.out.println(file.getAbsolutePath());
//            String sql = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//            try {
//                SQLUtils.parseStatements(sql, DbType.bigquery);
//                file.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}