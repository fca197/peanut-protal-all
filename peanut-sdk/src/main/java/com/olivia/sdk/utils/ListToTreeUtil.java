package com.olivia.sdk.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List转Tree工具类，支持JDK 21特性 用于将扁平结构的列表数据转换为层级化的树形结构
 *
 * @param <T>  数据类型
 * @param <ID> 主键和父键的数据类型
 */
public final class ListToTreeUtil<T, ID> {

  // 获取节点ID的函数
  private final Function<T, ID> idGetter;
  // 获取父节点ID的函数
  private final Function<T, ID> parentIdGetter;
  // 获取子节点列表的函数
  private final Function<T, List<T>> childrenGetter;
  // 设置子节点的函数
  private final BiConsumer<T, List<T>> childrenSetter;
  // 根节点的父ID值
  private final ID rootParentId;

  /**
   * 私有构造函数，通过Builder创建实例
   */
  private ListToTreeUtil(Builder<T, ID> builder) {
    this.idGetter = Objects.requireNonNull(builder.idGetter, "idGetter must not be null");
    this.parentIdGetter = Objects.requireNonNull(builder.parentIdGetter, "parentIdGetter must not be null");
    this.childrenGetter = Objects.requireNonNull(builder.childrenGetter, "childrenGetter must not be null");
    this.childrenSetter = Objects.requireNonNull(builder.childrenSetter, "childrenSetter must not be null");
    this.rootParentId = builder.rootParentId;
  }

  /**
   * 创建Builder
   */
  public static <T, ID> ListToTreeUtil<T, ID> builder(Function<T, ID> idGetter, Function<T, ID> parentIdGetter, Function<T, List<T>> childrenGetter,
      BiConsumer<T, List<T>> childrenSetter, ID rootParentId) {
    return new ListToTreeUtil<>(new Builder<>(idGetter, parentIdGetter, childrenGetter, childrenSetter, rootParentId));
  }

  /**
   * 将列表转换为树形结构
   *
   * @param list 扁平结构的列表
   * @return 树形结构的根节点列表
   */
  public List<T> convert(List<T> list) {
    // 使用JDK 21的List.isEmpty()增强判断
    if (list == null || list.isEmpty()) {
      return List.of();
    }

    // 构建节点ID到节点的映射，使用HashMap的工厂方法
    Map<ID, T> nodeMap = list.stream().collect(Collectors.toMap(idGetter, Function.identity(),
        // 处理重复ID的情况（保留第一个）
        (existing, replacement) -> existing, HashMap::new));

    // 存储根节点，使用可序列化的ArrayList
    List<T> rootNodes = new ArrayList<>(list.size() / 4); // 预估初始容量

    for (T node : list) {
      ID parentId = parentIdGetter.apply(node);

      // 使用模式匹配判断是否为根节点
      if (isRootNode(parentId)) {
        rootNodes.add(node);
      } else {
        // 使用Optional处理可能的空值，结合ifPresent简化逻辑
        Optional.ofNullable(nodeMap.get(parentId)).ifPresent(parentNode -> {
          // 获取父节点当前的子节点列表，若为null则初始化
          List<T> children = Optional.ofNullable(childrenGetter.apply(parentNode)).orElseGet(ArrayList::new);

          // 如果是新创建的列表，需要设置回父节点
          if (childrenGetter.apply(parentNode) == null) {
            childrenSetter.accept(parentNode, children);
          }

          // 添加当前节点到父节点的子节点列表
          children.add(node);
        });
      }
    }

    return rootNodes;
  }

  /**
   * 判断是否为根节点
   */
  private boolean isRootNode(ID parentId) {
    // 使用Objects.equals处理可能的null值比较
    return parentId == null || Objects.equals(rootParentId, parentId);
  }

  /**
   * 建造者类，用于配置工具类
   */
  public record Builder<T, ID>(Function<T, ID> idGetter, Function<T, ID> parentIdGetter, Function<T, List<T>> childrenGetter, BiConsumer<T, List<T>> childrenSetter,
                               ID rootParentId) {

  }
}
