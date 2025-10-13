package com.olivia.peanut.aps.utils.bom;

import com.googlecode.aviator.AviatorEvaluator;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class BomUtils {

  private final static Set<String> OPERATION_SET = Set.of("(", ")", "&&", "||");

  private static final Map<Pattern, String> patternsMap = new LinkedHashMap<>();

  static {

    patternsMap.put(Pattern.compile("&&"), " & ");
    patternsMap.put(Pattern.compile("&"), " && ");
    patternsMap.put(Pattern.compile("\\("), " ( ");
    patternsMap.put(Pattern.compile("\\)"), " ) ");
    patternsMap.put(Pattern.compile("\\|"), " || ");
  }

  public static Object bomExpression2List(String bomUseExpression) {
    if (StringUtils.isBlank(bomUseExpression)) {
      return List.of();
    }
    if (".".equals(bomUseExpression)) {
      return ".";
    }
    AtomicReference<String> result = new AtomicReference<>(bomUseExpression);
    patternsMap.forEach((pattern, expression) -> {
      result.set(pattern.matcher(result.get()).replaceAll(expression));
    });
    return List.of(result.get().split(" "));
  }

  public static boolean isMatch(Object bomUseExpression, String projectConfigCode,
      Set<String> projectSet) {
    StringBuilder sb = new StringBuilder();
    if (bomUseExpression instanceof List<?> kl) {
      for (Object klt : kl) {
        if (klt instanceof String klts && StringUtils.isNotBlank(klts)) {
          if ("!".equals(klts)) {
            sb.append("!");
          } else if (OPERATION_SET.contains(klts)) {
            sb.append(klts);
          } else {
            sb.append(projectSet.contains(klts));
          }
        }
      }
    } else if (".".equals(bomUseExpression)) {
      sb.append("true");
    }
    String bomExp = sb.toString();
    try {
      boolean boolBom = (Boolean) AviatorEvaluator.execute(bomExp);
      if (log.isDebugEnabled()) {
        log.debug(
            "project code {},bomUseExpression:{} ,bom Exp : {}  retValue: {} projectSet : {} ",
            projectConfigCode, bomUseExpression, bomExp, boolBom, projectSet);
      }
      return boolBom;
    } catch (Exception e) {
      log.error("project code {},bomUseExpression :{} ,bom Exp : {}  retValue: false  errMsg:{}",
          projectConfigCode, bomUseExpression, bomExp, e.getMessage(), e);
      return false;
    }
  }
}
