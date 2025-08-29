package com.olivia.peanut.store.service.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GdPlaceTextResponse {

  @JsonProperty("count")
  private String count;
  @JsonProperty("infocode")
  private String infocode;
  @JsonProperty("pois")
  private List<PoisDTO> pois;
  @JsonProperty("status")
  private String status;
  @JsonProperty("info")
  private String info;

  @NoArgsConstructor
  @Data
  public static class PoisDTO {

    @JsonProperty("parent")
    private String parent;
    @JsonProperty("address")
    private String address;
    @JsonProperty("business")
    private BusinessDTO business;
    @JsonProperty("distance")
    private String distance;
    @JsonProperty("pcode")
    private String pcode;
    @JsonProperty("adcode")
    private String adcode;
    @JsonProperty("pname")
    private String pname;
    @JsonProperty("cityname")
    private String cityname;
    @JsonProperty("type")
    private String type;
    @JsonProperty("photos")
    private List<PhotosDTO> photos;
    @JsonProperty("typecode")
    private String typecode;
    @JsonProperty("adname")
    private String adname;
    @JsonProperty("citycode")
    private String citycode;
    @JsonProperty("navi")
    private NaviDTO navi;
    @JsonProperty("name")
    private String name;
    @JsonProperty("indoor")
    private IndoorDTO indoor;
    @JsonProperty("location")
    private String location;
    @JsonProperty("id")
    private String id;

    @NoArgsConstructor
    @Data
    public static class BusinessDTO {

      @JsonProperty("opentime_today")
      private String opentimeToday;
      @JsonProperty("cost")
      private String cost;
      @JsonProperty("keytag")
      private String keytag;
      @JsonProperty("rating")
      private String rating;
      @JsonProperty("alias")
      private String businessAlias;
      @JsonProperty("parking_type")
      private String parkingType;
      @JsonProperty("tel")
      private String tel;
      @JsonProperty("tag")
      private String tag;
      @JsonProperty("rectag")
      private String rectag;
      @JsonProperty("opentime_week")
      private String opentimeWeek;
    }

    @NoArgsConstructor
    @Data
    public static class NaviDTO {

      @JsonProperty("navi_poiid")
      private String naviPoiid;
      @JsonProperty("entr_location")
      private String entrLocation;
      @JsonProperty("exit_location")
      private String exitLocation;
      @JsonProperty("gridcode")
      private String gridcode;
    }

    @NoArgsConstructor
    @Data
    public static class IndoorDTO {

      @JsonProperty("truefloor")
      private String truefloor;
      @JsonProperty("cpid")
      private String cpid;
      @JsonProperty("floor")
      private String floor;
      @JsonProperty("indoor_map")
      private String indoorMap;
    }

    @NoArgsConstructor
    @Data
    public static class PhotosDTO {

      @JsonProperty("title")
      private String title;
      @JsonProperty("url")
      private String url;
    }
  }
}
