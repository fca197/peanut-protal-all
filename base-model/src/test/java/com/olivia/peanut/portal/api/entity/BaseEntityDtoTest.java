package com.olivia.peanut.portal.api.entity;


import com.olivia.sdk.utils.JSON;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;

public class BaseEntityDtoTest {


  @Test
  public void checkStoreShoppingMall() {


    Map<String, Object> map = new HashMap<>();

//    map.put("orderId", "123");
//    map.put("orderStatus", "1");
    map.put("xxx", null);
    Function<Entry<String, Object>, Object> getValue = Entry::getValue;
    Function<Entry<String, Object>, String> getKey = Entry::getKey;
    Map<String, Object> collect = map.entrySet().parallelStream().collect(Collectors.toMap(getKey, getValue));
    System.out.println(JSON.toJSONString(collect));

  }
}