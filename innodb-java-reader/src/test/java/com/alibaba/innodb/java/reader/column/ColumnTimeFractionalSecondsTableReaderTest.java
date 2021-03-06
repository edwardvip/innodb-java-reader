package com.alibaba.innodb.java.reader.column;

import com.alibaba.innodb.java.reader.AbstractTest;
import com.alibaba.innodb.java.reader.TableReader;
import com.alibaba.innodb.java.reader.page.index.GenericRecord;
import com.alibaba.innodb.java.reader.schema.Column;
import com.alibaba.innodb.java.reader.schema.Schema;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * <pre>
 * insert into tb17 values(null, 100, '2019-10-02 10:59:59.123', '2019-10-02 10:59:59.456389', '10:59:59.45638', '2019-10-02 10:59:59');
 * insert into tb17 values(null, 101, '1970-01-01 08:00:01.550', '1970-01-01 08:00:01.000001', '08:00:01.00000', '1970-01-01 08:00:01');
 * insert into tb17 values(null, 102, '2008-11-23 09:23:00.808', '2008-11-23 09:23:00.294000', '09:23:00.29400', '2008-11-23 09:23:00');
 * </pre>
 *
 * @author xu.zx
 */
public class ColumnTimeFractionalSecondsTableReaderTest extends AbstractTest {

  public Schema getSchema() {
    return new Schema()
        .addColumn(new Column().setName("id").setType("int(11)").setNullable(false).setPrimaryKey(true))
        .addColumn(new Column().setName("a").setType("int(11)").setNullable(false))
        .addColumn(new Column().setName("b").setType("datetime(3)").setNullable(false))
        .addColumn(new Column().setName("c").setType("timestamp(6)").setNullable(false))
        .addColumn(new Column().setName("d").setType("time(5)").setNullable(false))
        .addColumn(new Column().setName("e").setType("datetime(0)").setNullable(false));
  }

  @Test
  public void testTimeFractionalSecondsColumnMysql56() {
    testTimeFractionalSecondsColumn(IBD_FILE_BASE_PATH_MYSQL56 + "column/time/tb17.ibd");
  }

  @Test
  public void testTimeFractionalSecondsColumnMysql57() {
    testTimeFractionalSecondsColumn(IBD_FILE_BASE_PATH_MYSQL57 + "column/time/tb17.ibd");
  }

  @Test
  public void testTimeFractionalSecondsColumnMysql80() {
    testTimeFractionalSecondsColumn(IBD_FILE_BASE_PATH_MYSQL80 + "column/time/tb17.ibd");
  }

  public void testTimeFractionalSecondsColumn(String path) {
    try (TableReader reader = new TableReader(path, getSchema())) {
      reader.open();

      // check queryByPageNumber
      List<GenericRecord> recordList = reader.queryByPageNumber(3);

      assertThat(recordList.size(), is(3));

      GenericRecord r1 = recordList.get(0);
      Object[] v1 = r1.getValues();
      System.out.println(Arrays.asList(v1));
      assertThat(r1.getPrimaryKey(), is(1));
      assertThat(r1.get("a"), is(100));
      assertThat(r1.get("b"), is("2019-10-02 10:59:59.123"));
      assertThat(r1.get("c"), is("1569985199.456389"));
      assertThat(r1.get("d"), is("10:59:59.45638"));
      assertThat(r1.get("e"), is("2019-10-02 10:59:59"));

      GenericRecord r2 = recordList.get(1);
      Object[] v2 = r2.getValues();
      System.out.println(Arrays.asList(v2));
      assertThat(r2.getPrimaryKey(), is(2));
      assertThat(r2.get("a"), is(101));
      assertThat(r2.get("b"), is("1970-01-01 08:00:01.550"));
      assertThat(r2.get("c"), is("1.000001"));
      assertThat(r2.get("d"), is("08:00:01.00000"));
      assertThat(r2.get("e"), is("1970-01-01 08:00:01"));

      GenericRecord r3 = recordList.get(2);
      Object[] v3 = r3.getValues();
      System.out.println(Arrays.asList(v3));
      assertThat(r3.getPrimaryKey(), is(3));
      assertThat(r3.get("a"), is(102));
      assertThat(r3.get("b"), is("2008-11-23 09:23:00.808"));
      assertThat(r3.get("c"), is("1227403380.294000"));
      assertThat(r3.get("d"), is("09:23:00.29400"));
      assertThat(r3.get("e"), is("2008-11-23 09:23:00"));
    }
  }
}
