package com.olivia.sdk.utils.deserializer;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.olivia.sdk.ann.FieldExt;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class PeanutStringDeserializer extends StringDeserializer {

  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

    // 1. 获取原始字符串并做基础的 trim 处理
    String originalValue;
    if (p.hasToken(JsonToken.VALUE_STRING)) {
      originalValue = (p.getText());
    } else {
      originalValue = p.hasToken(JsonToken.START_ARRAY) ? this._deserializeFromArray(p, ctxt) : this._parseString(p, ctxt, this);
    }

    String fieldName = p.getParsingContext().getCurrentName();
    try {
      Boolean bool = Optional.ofNullable(ctxt.getParser()).map(JsonParser::currentValue).map(t -> ReflectUtil.getField(t.getClass(), fieldName))
          .map(t -> t.getAnnotation(FieldExt.class)).map(FieldExt::autoTrim).orElse(true);
      String trimToEmpty = trimToEmpty(originalValue);
      if (bool) {
        if (!StringUtils.equals(trimToEmpty, originalValue)) {
          log.debug("PeanutStringDeserializer:originalValue:{},trimToEmpty:{},class:{}.{}", originalValue, trimToEmpty, p.currentValue().getClass(), fieldName);
        }
        return trimToEmpty;
      }
    } catch (Exception e) {
      String trimToEmpty = trimToEmpty(originalValue);
      log.warn("json 生成对象错误 trimToEmpty: {}, class: {} ,fieldName: {}",trimToEmpty,
          Optional.ofNullable(ctxt.getParser()).map(JsonParser::currentValue).map(Object::getClass).map(String::valueOf).orElse("类空"), fieldName);
      return trimToEmpty;
    }
//    ctxt.getParser()

    return originalValue;
  }

  public String deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
    return this.deserialize(p, ctxt);
  }
}
