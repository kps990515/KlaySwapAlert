package com.example.klayswapalert;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

@Component
@Slf4j
@NoArgsConstructor
public class KlaySwapService {
    private WebDriver driver;
    private WebElement element;
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:/chromedriver/chromedriver.exe";
    private static String url = "https://scope.klaytn.com/search/tokens-nft?key=KSLP";
    public int count = 233;
    public String text;

    @Scheduled(cron = " */30 * * * * * ")
    public void crawl() {
        setDriver();

        try {
            driver.get(url);
            Thread.sleep(1000);

            element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div[1]/div/span"));
            String raw = element.getText();
            String [] array = raw.split(" ");
            int diff = Integer.parseInt(array[5])-count;
            if(Integer.parseInt(array[5]) > count){
                text = "비상!!!" + diff +  "개 추가됨!!";
                count = Integer.parseInt(array[5]);
                funcTelegram(text);
            }else{
                text = "변동없음!!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    public void setDriver(){
        // WebDriver 경로 설정
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
    }

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
}
