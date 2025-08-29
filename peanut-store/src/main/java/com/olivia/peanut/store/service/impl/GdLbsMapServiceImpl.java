package com.olivia.peanut.store.service.impl;

import static com.olivia.sdk.utils.$.getStringList;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.olivia.peanut.store.model.StoreShoppingMall;
import com.olivia.peanut.store.service.LbsMapService;
import com.olivia.peanut.store.service.StoreShoppingMallService;
import com.olivia.peanut.store.service.entity.SelectAroundPoiReq;
import com.olivia.peanut.store.service.entity.SelectPoiReq;
import com.olivia.peanut.store.service.impl.dto.GdPlaceTextResponse;
import com.olivia.peanut.store.service.impl.dto.GdPlaceTextResponse.PoisDTO;
import com.olivia.peanut.store.service.impl.dto.GdPlaceTextResponse.PoisDTO.*;
import com.olivia.sdk.config.PeanutProperties;
import com.olivia.sdk.model.KVEntity;
import com.olivia.sdk.utils.BaseEntity;
import com.olivia.sdk.utils.HttpClientUtils;
import com.olivia.sdk.utils.RunUtils;
import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Setter
public class GdLbsMapServiceImpl implements LbsMapService {

  private static final String BASE_TEXT_URL = "https://restapi.amap.com/v5/place/text?";
  private static final String BASE_AROUND_URL = "https://restapi.amap.com/v5/place/around?";
  //  private static final String SHOW_FIELDS = "children,business,indoor,navi,photos";
  private static final String SHOW_FIELDS = "business,indoor,navi,photos";
  private static final int MAX_PAGE_NUM = 2;
  @Resource
  StoreShoppingMallService storeShoppingMallService;
  @Resource
  private PeanutProperties peanutProperties;


  @Override
  public void selectAndInsertCityPoi(SelectPoiReq req) {
    // 调整参数构建器，使其符合函数式接口定义
    ParameterBuilder parameterBuilder = (params, pageNum, pageSize) -> buildCityPoiParams(params, req, pageNum, pageSize);

    processPoiRequest(req.getPageNum(), req.getPageSize(), BASE_TEXT_URL, parameterBuilder, this::savePoiList);
  }

  @Override
  public void selectAndInsertAroundPoi(SelectAroundPoiReq req) {
    // 调整参数构建器，使其符合函数式接口定义
    ParameterBuilder parameterBuilder = (params, pageNum, pageSize) -> buildAroundPoiParams(params, req, pageNum, pageSize);

    processPoiRequest(req.getPageNum(), req.getPageSize(), BASE_AROUND_URL, parameterBuilder, this::savePoiList);
  }

  /**
   * 通用POI请求处理方法，消除代码重复
   */
  private void processPoiRequest(int initialPageNum, int pageSize, String baseUrl, ParameterBuilder parameterBuilder, PoiSaver poiSaver) {
    // 使用AtomicInteger追踪页码，避免修改原始请求对象
    AtomicInteger currentPage = new AtomicInteger(initialPageNum);

    RunUtils.asyncRun(currentPage.get() <= MAX_PAGE_NUM, "processPoiRequest", () -> {
      try {
        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        parameterBuilder.build(params, currentPage.get(), pageSize);

        // 构建URL（修复了原代码中的错误，使用=而不是-）
        String urlParams = params.entrySet().stream().filter(entry -> Objects.nonNull(entry.getValue())).map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));

        // 不使用JDK 21的字符串模板，确保兼容性
        String fullUrl = baseUrl + urlParams;
        // 执行请求
        GdPlaceTextResponse response = HttpClientUtils.get(fullUrl, GdPlaceTextResponse.class);
        if (Objects.isNull(response)) {
          log.info("Received null response for page: {}", currentPage.get());
          return;
        }

        List<PoisDTO> pois = response.getPois();
        if (CollUtil.isEmpty(pois)) {
          log.info("No POIs found for page: {}", currentPage.get());
          return;
        }
        // 保存POI数据
        poiSaver.save(pois);

        // 如果当前页数据量等于页大小，继续请求下一页
        if (pois.size() == pageSize && currentPage.incrementAndGet() <= MAX_PAGE_NUM) {
          processPoiRequest(currentPage.get(), pageSize, baseUrl, parameterBuilder, poiSaver);
        }
      } catch (Exception e) {
        log.error("Error processing POI request for page: {}", currentPage.get(), e);
      }
    });
  }

  /**
   * 构建城市POI查询参数
   */
  private void buildCityPoiParams(Map<String, Object> params, SelectPoiReq req, int pageNum, int pageSize) {
    params.put("keywords", req.getKeywords());
    params.put("key", peanutProperties.getMapConfig().getGaoDeWebKey());
    params.put("city_limit", true);
    params.put("page_size", pageSize);
    params.put("page_num", pageNum);
    params.put("show_fields", SHOW_FIELDS);

    List<String> poiList = req.getTypes();
    if (CollUtil.isNotEmpty(poiList)) {
      params.put("types", String.join(",", poiList));
    }
  }

  /**
   * 构建周边POI查询参数
   */
  private void buildAroundPoiParams(Map<String, Object> params, SelectAroundPoiReq req, int pageNum, int pageSize) {
    params.put("keywords", req.getKeywords());
    params.put("location", req.getLocationLngLat());
    params.put("key", peanutProperties.getMapConfig().getGaoDeWebKey());
    params.put("city_limit", true);
    params.put("page_size", pageSize);
    params.put("page_num", pageNum);
    params.put("radius", req.getRadius());
    params.put("sortrule", req.getSortrule());
    params.put("region", req.getRegion());
    params.put("show_fields", SHOW_FIELDS);

    List<String> poiList = req.getTypes();
    if (CollUtil.isNotEmpty(poiList)) {
      params.put("types", String.join(",", poiList));
    }
  }

  /**
   * 保存POI数据
   */
  private void savePoiList(List<PoisDTO> poisDTOList) {
    if (CollUtil.isEmpty(poisDTOList)) {
      log.info("poiDTOList size is 0");
      return;
    }
    List<StoreShoppingMall> storeShoppingMallList = poisDTOList.stream().filter(Objects::nonNull).map(this::gdPoi2entity).toList();
    log.info("storeShoppingMallList {}", storeShoppingMallList);
//    List<String> mapPoiIdList = storeShoppingMallList.stream().map(StoreShoppingMall::getMapPoiId).toList();

//    Map<String, Long> mapPoidSet = this.storeShoppingMallService.list(
//            new LambdaQueryWrapper<StoreShoppingMall>().select(BaseEntity::getId, StoreShoppingMall::getMapPoiId).in(StoreShoppingMall::getMapPoiId, mapPoiIdList)).stream()
//        .collect(Collectors.toMap(StoreShoppingMall::getMapPoiId, BaseEntity::getId));

    // TODO: 实现POI数据的保存逻辑
    List<StoreShoppingMall> saveList = new ArrayList<>();
    List<StoreShoppingMall> updateList = new ArrayList<>();
    storeShoppingMallList.forEach(storeShoppingMall -> {
//      Long id = mapPoidSet.get(storeShoppingMall.getMapPoiId());
//      if (Objects.nonNull(id)) {
//        storeShoppingMall.setId(id);
//        updateList.add(storeShoppingMall);
//      } else {
//        saveList.add(storeShoppingMall);
//      }
    });
    this.storeShoppingMallService.saveBatch(saveList);
    this.storeShoppingMallService.updateBatchById(updateList);

    // 1. 转换DTO为实体对象
    // 2. 批量保存到数据库
  }

  private StoreShoppingMall gdPoi2entity(PoisDTO poisDTO) {
    if (Objects.isNull(poisDTO)) {
      return null;
    }
    BusinessDTO business = poisDTO.getBusiness();
    StoreShoppingMall storeShoppingMall = new StoreShoppingMall();
//    storeShoppingMall.setAreaCode(poisDTO.getAdcode()).setShoppingMallAddress(poisDTO.getAddress()).setAreaName(poisDTO.getAdname()).setMapPoiId(poisDTO.getId());

//    storeShoppingMall.setShoppingMallTypeCodeList(getStringList(poisDTO.getTypecode(), "|"));
    if (Objects.nonNull(business)) {
      List<String> tagList = getStringList(business.getTag(),",");
//      storeShoppingMall.setBusinessAlias(business.getBusinessAlias()).setBusinessKeytag(business.getKeytag()).setBusinessOpenTimeToday(business.getOpentimeToday())
//          .setBusinessOpenTimeWeek(business.getOpentimeWeek()).setBusinessRating(business.getRating()).setBusinessTag(tagList).setBusinessTel(getStringList(business.getTel(),";"));
      ;
    }
    IndoorDTO indoor = poisDTO.getIndoor();
    if (Objects.nonNull(indoor)) {
//      storeShoppingMall.setIndoorCpid(indoor.getCpid()).setIndoorIndoorMap(indoor.getIndoorMap()).setIndoorFloor(indoor.getFloor())
//          .setIndoorIndoorMap(indoor.getIndoorMap()).setIndoorTrueFloor(indoor.getTruefloor());
    }
    String type = poisDTO.getType();
//    storeShoppingMall.setShoppingMallType(getStringList(type,";"));
//    storeShoppingMall.setShoppingMallLocation(poisDTO.getLocation());
    storeShoppingMall.setCityName(poisDTO.getCityname());
    storeShoppingMall.setShoppingMallName(poisDTO.getName());
    storeShoppingMall.setProvinceCode(poisDTO.getPcode());
    storeShoppingMall.setProvinceName(poisDTO.getPname());
    NaviDTO navi = poisDTO.getNavi();
    if (Objects.nonNull(navi)) {
//      storeShoppingMall.setNaviGridCode(navi.getGridcode()).setNaviExitLocation(navi.getExitLocation()).setNaviEnterLocation(navi.getEntrLocation());
    }
    List<PhotosDTO> photos = poisDTO.getPhotos();
    if (CollUtil.isNotEmpty(photos)) {
//      List<KVEntity> list = photos.stream().map(t -> new KVEntity().setLabel(t.getTitle()).setValue(t.getUrl())).toList();
//      storeShoppingMall.setPhotos(list);
    }

    return storeShoppingMall;
  }


  /**
   * 函数式接口：用于构建请求参数
   */
  @FunctionalInterface
  private interface ParameterBuilder {

    void build(Map<String, Object> params, int pageNum, int pageSize);
  }

  /**
   * 函数式接口：用于保存POI数据
   */
  @FunctionalInterface
  private interface PoiSaver {

    void save(List<PoisDTO> poisDTOList);
  }
}
