package com.olivia.sdk.utils;

import java.util.function.BinaryOperator;

public class BiFunctionImpl {

  public static <T> BinaryOperator<T> getFist() {
    return  BiFunctionImpl.getFist();
  }

  public static <T> BinaryOperator<T> getNew() {
    return (a, b) -> b;
  }
}
