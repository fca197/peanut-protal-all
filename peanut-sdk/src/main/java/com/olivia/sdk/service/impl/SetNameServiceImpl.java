package com.olivia.sdk.service.impl;

import static com.olivia.sdk.utils.FieldUtils.getField;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.olivia.sdk.service.SetNameService;
import com.olivia.sdk.service.pojo.NameConfig;
import com.olivia.sdk.service.pojo.SetNamePojo;
import com.olivia.sdk.utils.JSONUtils;
import com.olivia.sdk.utils.RunUtils;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 名称设置服务实现类 用于批量设置对象中的名称字段，通过ID关联从对应的服务中查询名称并填充
 */
@Slf4j
@Service
public class SetNameServiceImpl implements SetNameService {

  /**
   * 字段缓存，减少反射获取字段的性能开销 键格式: 类名+字段名
   */
  private final Cache<String, Field> fieldCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();

  @Override
  public void setName(List<?> list, List<SetNamePojo> setNamePojoList) {
    // 入参校验
    if (CollUtil.isEmpty(list) || CollUtil.isEmpty(setNamePojoList)) {
      log.debug("列表或配置为空，无需设置名称");
      return;
    }

    List<Runnable> tasks = new ArrayList<>(setNamePojoList.size());

    // 为每个配置创建一个异步任务
    for (SetNamePojo namePojo : setNamePojoList) {
      tasks.add(() -> processNameSetting(list, namePojo));
    }

    // 执行所有任务
    RunUtils.run("批量设置名称", tasks);
  }

  /**
   * 处理单个配置的名称设置
   *
   * @param list     要设置名称的对象列表
   * @param namePojo 名称设置配置
   */
  private void processNameSetting(List<?> list, SetNamePojo namePojo) {
    try {
      // 获取对应的服务Bean
      IService<?> service = getServiceBean(namePojo);
      if (service == null) {
        return;
      }

      // 获取ID字段和名称配置
      List<NameConfig> nameConfigs = namePojo.getNameConfigList();
      if (CollUtil.isEmpty(nameConfigs)) {
        log.warn("名称配置列表为空，服务: {}", namePojo.getServiceName());
        return;
      }

      // 收集所有需要查询的ID
      Set<Serializable> idSet = collectIds(list, nameConfigs);
      if (CollUtil.isEmpty(idSet)) {
        log.debug("没有需要查询的ID，服务: {}", namePojo.getServiceName());
        return;
      }

      // 批量查询ID对应的名称
      Map<Object, Object> idNameMap = queryIdToNameMap(service, idSet, namePojo);

      // 为列表中的对象设置名称
      setNamesToObjects(list, nameConfigs, idNameMap);

    } catch (Exception e) {
      log.error("处理名称设置失败，配置: {}", JSONUtils.toJSONString(namePojo), e);
    }
  }

  /**
   * 获取服务Bean
   *
   * @param namePojo 名称设置配置
   * @return 服务Bean，如果获取失败返回null
   */
  private IService<?> getServiceBean(SetNamePojo namePojo) {
    Class<? extends IService<?>> serviceName = namePojo.getServiceName();
    try {
      IService<?> service = SpringUtil.getBean(serviceName);
      if (service == null) {
        log.warn("未找到服务Bean: {}", serviceName);
      }
      return service;
    } catch (Exception e) {
      log.error("获取服务Bean失败: {}", serviceName, e);
      return null;
    }
  }

  /**
   * 收集所有需要查询的ID
   *
   * @param list        要设置名称的对象列表
   * @param nameConfigs 名称配置列表
   * @return 收集到的ID集合
   */
  private Set<Serializable> collectIds(List<?> list, List<NameConfig> nameConfigs) {
    Set<Serializable> idSet = CollUtil.newHashSet();

    for (NameConfig config : nameConfigs) {
      String idFieldName = config.getIdField();
      Field idField = getCachedField(list.getFirst().getClass(), idFieldName);

      if (idField == null) {
        log.warn("未找到ID字段: {} 在类: {}", idFieldName, list.getFirst().getClass().getName());
        continue;
      }

      // 提取该字段的所有非空值
      for (Object obj : list) {
        Serializable id = (Serializable) ReflectUtil.getFieldValue(obj, idField);
        if (id != null) {
          idSet.add(id);
        }
      }
    }

    return idSet;
  }

  /**
   * 查询ID到名称的映射关系
   *
   * @param service  服务Bean
   * @param idSet    ID集合
   * @param namePojo 名称设置配置
   * @return ID到名称的映射
   */
  private Map<Object, Object> queryIdToNameMap(IService<?> service, Set<Serializable> idSet, SetNamePojo namePojo) {
    try {
      // 批量查询对象
      List<?> entities = service.listByIds(idSet);
      if (CollUtil.isEmpty(entities)) {
        return Map.of();
      }

      // 获取名称字段
      String nameFieldName = namePojo.getNameFieldName();
      Field nameField = getCachedField(entities.getFirst().getClass(), nameFieldName);

      if (nameField == null) {
        log.warn("未找到名称字段: {} 在服务实体类: {}", nameFieldName, entities.getFirst().getClass().getName());
        return Map.of();
      }

      // 构建ID到名称的映射
      return entities.stream().collect(
          Collectors.toMap(entity -> ReflectUtil.getFieldValue(entity, "id"), entity -> ReflectUtil.getFieldValue(entity, nameField), (existing, replacement) -> existing
              // 处理ID重复的情况
          ));
    } catch (Exception e) {
      log.error("查询ID到名称的映射失败", e);
      return Map.of();
    }
  }

  /**
   * 为对象列表设置名称
   *
   * @param list        要设置名称的对象列表
   * @param nameConfigs 名称配置列表
   * @param idNameMap   ID到名称的映射
   */
  private void setNamesToObjects(List<?> list, List<NameConfig> nameConfigs, Map<Object, Object> idNameMap) {
    for (Object obj : list) {
      for (NameConfig config : nameConfigs) {
        // 获取ID字段值
        Field idField = getCachedField(obj.getClass(), config.getIdField());
        if (idField == null) {
          continue;
        }

        Object idValue = ReflectUtil.getFieldValue(obj, idField);
        if (idValue == null) {
          continue;
        }

        // 获取对应的名称
        Object nameValue = idNameMap.get(idValue);

        // 设置到目标字段
        for (String nameFieldName : config.getNameFieldList()) {
          Field nameField = getCachedField(obj.getClass(), nameFieldName);
          if (nameField != null) {
            ReflectUtil.setFieldValue(obj, nameField, nameValue);
          } else {
            log.warn("未找到名称目标字段: {} 在类: {}", nameFieldName, obj.getClass().getName());
          }
        }
      }
    }
  }

  /**
   * 从缓存获取字段，如果缓存中没有则反射获取并加入缓存
   *
   * @param clazz     类对象
   * @param fieldName 字段名
   * @return 字段对象，如果未找到返回null
   */
  private Field getCachedField(Class<?> clazz, String fieldName) {
    try {
      String cacheKey = clazz.getName() + "." + fieldName;
      Field field = fieldCache.getIfPresent(cacheKey);

      if (field == null) {
        field = getField(clazz, fieldName);
        if (field != null) {
          fieldCache.put(cacheKey, field);
        }
      }

      return field;
    } catch (Exception e) {
      log.error("获取字段失败，类: {}，字段: {}", clazz.getName(), fieldName, e);
      return null;
    }
  }
}
