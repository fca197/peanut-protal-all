package com.olivia.sdk.exception;

public class NotenantException extends RuntimeException {


  public NotenantException(String msg) {
    throw new RunException(msg);
  }

  public static RuntimeException of() {

    throw new RuntimeException("没有租户信息");
  }
}
