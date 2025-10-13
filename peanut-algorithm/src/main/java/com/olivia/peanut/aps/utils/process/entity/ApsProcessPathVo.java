package com.olivia.peanut.aps.utils.process.entity;

import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApsProcessPathVo extends BaseEntityDto {

  private String processPathCode;
  private String processPathName;
  private String processPathRemark;
  private Boolean isDefault;
  private String factoryName;
  private Long factoryId;
  private List<ApsProcessPathRoomVo> pathRoomList;

}
