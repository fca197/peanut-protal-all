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
public class ApsProcessPathRoomVo extends BaseEntityDto {

  private Long processPathId;
  private Long roomId;
  private Long factoryId;

  private List<ApsRoomConfigVo> apsRoomConfigList;
}
