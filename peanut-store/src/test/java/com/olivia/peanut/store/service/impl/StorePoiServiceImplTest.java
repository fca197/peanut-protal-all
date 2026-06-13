package com.olivia.peanut.store.service.impl;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.olivia.peanut.store.model.StorePoi;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

@Slf4j
public class StorePoiServiceImplTest extends TestCase {

  @Test
  @SneakyThrows
  public void test() {
    @Cleanup InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("poi.txt");

    ArrayList<String> arrayList = IoUtil.readUtf8Lines(resourceAsStream, Lists.newArrayList());

    List<StorePoi> saveList = Lists.newArrayList();

    AtomicReference<String> parentCode = new AtomicReference<>();

    arrayList.forEach(t -> {
      if (StringUtils.isBlank(t)) {
        return;
      }
      ArrayList<String> tl = Lists.newArrayList(t.split(","));
      if (tl.size() != 4) {
        log.info("t {}", t);
        return;
      }

      String first = tl.getFirst();
      if (first.endsWith("0000")) {
        parentCode.set(first);
        saveList.add(new StorePoi().setPoiCode(first).setPoiLevel(1).setPoiName(tl.get(3)).setPoiParentCode(StorePoi.MAX_PARENT_CODE));
      } else if (first.endsWith("00")) {
        parentCode.set(first);
        saveList.add(new StorePoi().setPoiCode(first).setPoiLevel(2).setPoiName(tl.get(2)).setPoiParentCode(parentCode.get()));
      } else {
        saveList.add(new StorePoi().setPoiCode(first).setPoiLevel(3).setPoiName(tl.get(1)).setPoiParentCode(parentCode.get()));
      }
    });
    saveList.forEach(t -> {
      t.setId(IdWorker.getId());
      //select id, poi_parent_code,poi_code, poi_name, poi_level, poi_path, tenant_id, is_delete, create_time, create_by, update_time, update_by, trace_id, version_num, create_user_name, update_user_name from store_poi
      log.info("{},{},{},{},{}", t.getId(), t.getPoiParentCode(), t.getPoiCode(), t.getPoiName(), t.getPoiLevel());
    });

  }
}