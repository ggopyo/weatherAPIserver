package com.example.mongodb.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("WeatherItem")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WeatherItem {
    @Id
    private String name;
    private String baseDate;
    private String baseTime;
    private String temperature; // 기온
    private String hourPrecipitation; // 1시간 강수량
    private String eastWestWind; //동서바람성분
    private String southNorthWind;  //남북바람성분
    private String humidity;  //습도
    private String precipitationForm;  //강수형태
    private String windDirection; //풍향
    private String windSpeed; //풍속
    //기상청_단기예보 ((구)_동네예보) 조회서비스 오픈API 활용가이드 최종.zip 참조한 변수명
    //https://www.data.go.kr/data/15084084/openapi.do



}