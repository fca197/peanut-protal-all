package com.olivia.peanut.aps.utils.process.entity;

import com.olivia.peanut.aps.utils.bom.model.ApsGoodsBomVo;
import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import java.time.LocalDate;
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
public class ApsProcessPathInfo {

  private List<Info> dataList;

  @Setter
  @Getter
  @Accessors(chain = true)
  public static class Info extends BaseEntityDto {

    private Long roomId;
    private Long sectionId;
    private Long stationId;
    private Long statusId;
    private Integer executeTime;
    private Long factoryId;
    private LocalDate beginLocalDate;
    private LocalDate endLocalDate;
    private Integer sortIndex;
    private List<ApsGoodsBomVo> apsGoodsBomList;
  }
}
