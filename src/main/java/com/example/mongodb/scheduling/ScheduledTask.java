package com.example.mongodb.scheduling;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.mongodb.model.DaejeonDistricts;
import com.example.mongodb.model.WeatherItem;
import com.example.mongodb.repository.ItemRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.lang.String.valueOf;

@Component

public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private static String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";// 날씨 공공데이터(초단기실황조회) URL
    private static String serviceKey = "y8YlOsRl3U%2BsizzI%2F8XAFyZ%2BEC2%2BpC%2BZKvmaydgd9gcLtLjRon2iL9FUHQkrvbOOKn%2F%2FI1AYuT41c1b9FWK8aw%3D%3D";

    @Autowired
    ItemRepository weatherItemRepo;



    //    @Scheduled(fixedRate = 30000) // 초단기실황 호출 사이의 간격을 지정하여 호출도 가능함
    @Scheduled(cron = "30 0,10,29,30,40,50 * * * *") // 매 시간 10분 단위로 30초 이후 실행됨. 예) 1시 0분 30초, 2시 20분 30초 ...등
    public void reportCurrentTime() {
        weatherItemRepo.findAll();

        log.info("현재 시각 {}", dateFormat.format(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(Calendar.MINUTE) <= 39) calendar.add(Calendar.HOUR_OF_DAY, -1);
        // 정각 기준 매 40분 마다 해당 정각 시각을 호출할 수 있음, 그렇지 않으면 오류가 반환됨
        // 예) 03:30분에 0300 호출 (x), 03:30분에 0200 호출(o),
        // 예) 03:40분에 0300 호출(o), 03:40분에 0200 호출(x)

        int date = calendar.get(Calendar.DATE);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        String baseDateReq = "20220320";    //조회하고싶은 날짜
        String baseTimeReq = String.format("%02d%02d", hourOfDay, 0); // 조회하고 싶은 시간 // 0120,0330,1220,등...
        String type = "json";
        DaejeonDistricts[] daejeonDistrict = new DaejeonDistricts[5];
        daejeonDistrict[0] = new DaejeonDistricts("대전광역시 동구", "68", "100"); // 생성자로 지역명과 지역의 좌표를 입력함
        daejeonDistrict[1] = new DaejeonDistricts("대전광역시 중구", "68", "100");
        daejeonDistrict[2] = new DaejeonDistricts("대전광역시 서구", "67", "100");
        daejeonDistrict[3] = new DaejeonDistricts("대전광역시 유성구", "67", "101");
        daejeonDistrict[4] = new DaejeonDistricts("대전광역시 대덕구", "68", "100");
        try {
            for (int i = 0; i < daejeonDistrict.length; i++) {
                WeatherItem weatherItem = new WeatherItem();
                String districtName = daejeonDistrict[i].getName();
                String nx = daejeonDistrict[i].getNx();
                String ny = daejeonDistrict[i].getNy();
                StringBuilder urlBuilder = new StringBuilder(apiUrl);
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
                urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
                urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
                urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDateReq, "UTF-8")); /* 조회하고싶은 날짜*/
                urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTimeReq, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
                urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* 타입 */
                URL url = new URL(urlBuilder.toString());
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                BufferedReader rd;
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                String result = sb.toString(); 

                JSONObject jsonObject = new JSONObject(result); // json 형태인 Data가 String 타입으로 되어있어 JSONObject로 변환해주는 작업 필요
                JSONObject geoObject = jsonObject.getJSONObject("response");
                JSONObject header = geoObject.getJSONObject("header");
                String resultCode = header.getString("resultCode");
                String resultMsg = header.getString("resultMsg");

                JSONObject body = geoObject.getJSONObject("body");
                JSONObject items = body.getJSONObject("items");
                int pageNo = body.getInt("pageNo");
                int numOfRows = body.getInt("numOfRows");
                int totalCount = body.getInt("totalCount");

                ArrayList<JSONObject> listdata = new ArrayList<>();
                JSONArray jArray = items.getJSONArray("item");
                System.out.println("---------" + districtName + "--------------");// 구 이름
                System.out.println(resultCode);
                System.out.println(resultMsg);
                System.out.println(numOfRows);
                System.out.println(totalCount);
                System.out.println(pageNo);
                System.out.println("-------------------");
                if (jArray != null) {
                    for (int j = 0; j < jArray.length(); j++) {
                        listdata.add(jArray.getJSONObject(j));
                    }
                }
                weatherItem.setName(daejeonDistrict[i].getName()); // 구 이름 입력

                for (int j = 0; j < listdata.size(); j++) {
                    JSONObject item = listdata.get(j);
                    String category = item.getString("category");//자료구분코드
                    String obsrValue = item.getString("obsrValue");//실황 값
                    String baseDateRes = item.getString("baseDate");//발표일자
                    String baseTimeRes = item.getString("baseTime");//발표시각
                    weatherItem.setBaseDate(baseDateRes);
                    weatherItem.setBaseTime(baseTimeRes);
                    System.out.println(obsrValue);

                    if (category.equals("T1H")) weatherItem.setTemperature(obsrValue);//기온
                    else if (category.equals("RN1")) weatherItem.setHourPrecipitation(obsrValue);//1시간 강수량
                    else if (category.equals("UUU")) weatherItem.setEastWestWind(obsrValue);//동서바람성분
                    else if (category.equals("VVV")) weatherItem.setSouthNorthWind(obsrValue);//남북바람성분
                    else if (category.equals("REH")) weatherItem.setHumidity(obsrValue);//습도
                    else if (category.equals("PTY")) weatherItem.setPrecipitationForm(obsrValue);//강수형태
                    else if (category.equals("VEC")) weatherItem.setWindDirection(obsrValue);//풍향
                    else if (category.equals("WSD")) weatherItem.setWindSpeed(obsrValue);//풍속
                }
                updateWeatherItems(weatherItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void updateWeatherItems(WeatherItem weatherItem) {
        weatherItemRepo.save(weatherItem);
        System.out.println("데이터 업데이트 완료...");
    }

}