package com.olivia.peanut.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.olivia.peanut.base.model.DistrictCodeBoundary;
import com.olivia.peanut.base.service.QueryDistrictBoundaryService;
import com.olivia.peanut.base.service.entity.GdDistrictBoundary;
import com.olivia.peanut.base.service.entity.GdDistrictBoundary.DistrictsDTO;
import com.olivia.sdk.config.PeanutProperties;
import com.olivia.sdk.utils.HttpClientUtils;
import com.olivia.sdk.utils.JSONUtils;
import jakarta.annotation.Resource;
import java.io.File;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Slf4j
@Service
//@ConditionalOnProperty(//
//    name = "peanut.gao-de-web-key",  // 配置项的key
//    havingValue = "true",         // 配置项的value需要匹配该值
//    matchIfMissing = false        // 如果配置不存在，是否创建Bean（false=不创建）
//)
public class GdQueryDistrictBoundaryServiceImpl implements QueryDistrictBoundaryService {

  @Resource
  PeanutProperties peanutProperties;


  @Override
  @SneakyThrows
  public DistrictCodeBoundary queryDistrictCodeFormMap(String areaCode) {

    String url = "https://restapi.amap.com/v3/config/district?key=" + peanutProperties.getMapConfig().getGaoDeWebKey() + "&keywords=" + areaCode + "&subdistrict=0&extensions=all";

    GdDistrictBoundary districtBoundary = HttpClientUtils.get(url, GdDistrictBoundary.class);

    DistrictCodeBoundary ret = new DistrictCodeBoundary().setDistrictCode(areaCode);
    ret.setId(IdWorker.getId());
    if (Objects.isNull(districtBoundary)) {
      return ret;
    }
    if (districtBoundary.isFail()) {
      return ret;
    }
    List<DistrictsDTO> districts = districtBoundary.getDistricts();
    if (CollUtil.isEmpty(districts)) {
      return ret;
    }
    DistrictsDTO districtsDTO = districts.getFirst();
    String polyline = districtsDTO.getPolyline();

    if (StringUtils.isNotBlank(districtsDTO.getCenter())) {
      String[] center = districtsDTO.getCenter().split(",");
      ret.setCenterLat(center[1]).setCenterLng(center[0]);
    }
    ret.setDistrictName(districtsDTO.getName());
    Map<String, Object> beanToMap = BeanUtil.beanToMap(ret);
    beanToMap.put("polyline", Arrays.stream(polyline.split("[;]")).map(t -> t.split(",")).toList());

    FileUtil.writeUtf8String(JSONUtils.toJSONString(beanToMap),
        new File(peanutProperties.getLocalFileUploadPath()+"/districtBoundary/" + areaCode + ".json"));
    return ret;
  }
}
