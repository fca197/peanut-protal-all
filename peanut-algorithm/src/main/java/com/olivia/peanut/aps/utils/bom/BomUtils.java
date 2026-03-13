package com.olivia.peanut.aps.utils.bom;

import com.googlecode.aviator.AviatorEvaluator;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * BOM（物料清单）工具类
 * 提供 BOM 表达式的解析和匹配功能
 */
@Slf4j
public class BomUtils {

  /**
   * 操作符集合，包含括号和逻辑运算符
   */
  private final static Set<String> OPERATION_SET = Set.of("(", ")", "&&", "||");

  /**
   * 正则表达式模式映射表，用于替换 BOM 表达式中的特殊字符
   */
  private static final Map<Pattern, String> patternsMap = new LinkedHashMap<>();

  static {
    // 将逻辑运算符替换为带空格的标准格式
    patternsMap.put(Pattern.compile("&&"), " & ");
    patternsMap.put(Pattern.compile("&"), " && ");
    patternsMap.put(Pattern.compile("\\("), " ( ");
    patternsMap.put(Pattern.compile("\\)"), " ) ");
    patternsMap.put(Pattern.compile("\\|"), " || ");
  }

  /**
   * 将 BOM 使用表达式转换为列表
   * @param bomUseExpression BOM 使用表达式，如 "(AA001&&AC002)&&(AB001||AB002)"
   * @return 解析后的表达式元素列表，如果表达式为空则返回空列表，如果表达式为 "." 则返回点号
   */
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

  /**
   * 检查项目配置码是否与 BOM 表达式匹配
   * @param bomUseExpression BOM 使用表达式，可以是字符串 "." 或已解析的列表
   * @param projectConfigCode 项目配置码
   * @param projectSet 项目配置集合
   * @return 如果匹配成功返回 true，否则返回 false
   */
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
            // 检查项目集合中是否包含当前项目码
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