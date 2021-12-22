package com.example.klayswapalert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.Lombok;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Component
public class KlayController {
    List<String> poolList = new ArrayList<>();
    HashMap<String, Object> result = new HashMap<String, Object>();
    String text = "";
    Gson gson = new Gson();
    int count = 0;

    public void funcTelegram(String str){
        String token = "5017964370:AAHhaPkQrmoo2nLV8sfpAP7sGGpAzzWJhd4";
        String chat_id = "-1001668983352";

        BufferedReader in = null;

        try {
            URL obj = new URL("https://api.telegram.org/bot" + token + "/sendmessage?chat_id=" + chat_id + "&text=" + str); // 호출할 url

            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line;

            while((line = in.readLine()) != null) { // response를 차례대로 출력
                System.out.println(line);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) try { in.close(); } catch(Exception e) { e.printStackTrace(); }
        }
    }

    @Scheduled(cron = " 0 0 0/1 * * * ")
    public void healthCheck() {
        String text2 = "살아있음";
        funcTelegram(text2);
    }

    @Scheduled(cron = " 0 0/5 * * * * ")
    public void callApi(){
        try {

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(5000); //타임아웃 설정 5초
            factory.setReadTimeout(5000);//타임아웃 설정 5초
            RestTemplate restTemplate = new RestTemplate(factory);

            HttpHeaders header = new HttpHeaders();
            header.add("User-Agent", "Appication");

            HttpEntity<?> entity = new HttpEntity<>(header);

            String url = "https://api-cypress.scope.klaytn.com/v1/tokens";

            for(int i=1; i<14; i++){
                UriComponents uri = UriComponentsBuilder.fromHttpUrl(url+"?"+"page=" + i +"&key=KSLP").build();
                //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
                ResultVO resultVO = restTemplate.getForObject(uri.toString(), ResultVO.class);

                if(poolList.isEmpty()) poolList.add("temp");
                resultVO.getResult().forEach( p -> {
                    String tokenName = p.getTokenName();
                    if(!poolList.contains(tokenName)){
                        poolList.add(p.tokenName);
                        //텔레그램 발송
                        funcTelegram(count++ + "번째" + tokenName + "추가됨");
                    }
                });
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body"  , e.getStatusText());
            System.out.println("dfdfdfdf");
            System.out.println(e.toString());

        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body"  , "excpetion오류");
            System.out.println(e.toString());
        }

    }


}
