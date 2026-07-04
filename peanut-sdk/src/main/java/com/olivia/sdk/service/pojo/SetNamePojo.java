package com.olivia.sdk.service.pojo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olivia.sdk.utils.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 名称设置配置类 用于定义名称设置的相关参数，包括服务类、实体类、字段映射关系等
 */
@Setter
@Getter
@Accessors(chain = true)
public class SetNamePojo {

  /**
   * 名称配置列表，定义ID字段与名称字段的映射关系
   */
  private List<NameConfig> nameConfigList = new ArrayList<>();

  /**
   * 服务类类型，用于获取数据的服务接口
   */
  private Class<? extends IService<?>> serviceName;

  /**
   * 名称字段名，从服务查询结果中获取名称的字段
   */
  private String nameFieldName;

  /**
   * 查询字段名，默认使用"id"，用于从服务查询数据的字段
   */
  private String selectFieldName = "id";

  /**
   * 实体类类型，服务对应的实体类
   */
  private Class<? extends BaseEntity> entityClass;

  /**
   * 添加名称配置
   *
   * @param nameConfig 名称配置对象
   * @return 当前对象，支持链式调用
   */
  public SetNamePojo addNameConfig(NameConfig nameConfig) {
    if (nameConfig != null) {
      this.nameConfigList.add(nameConfig);
    }
    return this;
  }

  /**
   * 批量添加名称配置
   *
   * @param nameConfigs 名称配置列表
   * @return 当前对象，支持链式调用
   */
  public SetNamePojo addNameConfigs(List<NameConfig> nameConfigs) {
    if (nameConfigs != null && !nameConfigs.isEmpty()) {
      this.nameConfigList.addAll(nameConfigs);
    }
    return this;
  }

}
