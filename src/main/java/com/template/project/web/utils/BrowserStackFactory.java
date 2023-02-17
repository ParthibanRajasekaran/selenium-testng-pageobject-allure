package com.template.project.web.utils;

import com.browserstack.local.Local;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BrowserStackFactory {

    public static WebDriver driver;
    public static Local local;
    private static JSONObject config;
    private static Map<String, Object> commonCapabilities;
    private static String username;
    private static String accessKey;

    public static WebDriver getDriver() {
        return driver;
    }

    private static JSONObject getBrowserStackConfig(){
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(new FileReader("src/main/resources/config/browserstack.json"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getBrowserStackAccessKey() {

        config = getBrowserStackConfig();
        commonCapabilities = (Map<String, Object>) config.get("capabilities");
        HashMap bstackOptionsMap = (HashMap) commonCapabilities.get("bstack:options");


        username = System.getenv("BROWSERSTACK_USERNAME");
        if (username == null) {
            username = (String) config.get("user");
        }

        accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = (String) config.get("key");
        }
        try {
            if ((bstackOptionsMap.get("local") != null &&
                    bstackOptionsMap.get("local").toString().equalsIgnoreCase("true") && (local == null))) {
                local = new Local();
                Map<String, String> options = new HashMap<String, String>();
                options.put("key", accessKey);
                local.start(options);
            }
        } catch (Exception e) {
            System.out.println("Error while start local - " + e);
        }
    }


    @SuppressWarnings("unchecked")
    public static void setBrowserCapabilities() {
        config = getBrowserStackConfig();
        JSONObject envs = (JSONObject) config.get("environments");

        MutableCapabilities capabilities = new MutableCapabilities();

        String environment = System.getProperty("envConfig");
        if (environment == null || environment.equals("")) environment = "default";

        Map<String, Object> envCapabilities = (Map<String, Object>) envs.get(environment);
        Iterator it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue());
        }

        commonCapabilities = (Map<String, Object>) config.get("capabilities");
        it = commonCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (capabilities.getCapability(pair.getKey().toString()) == null) {
                capabilities.setCapability(pair.getKey().toString(), pair.getValue());
            } else if (pair.getKey().toString().equalsIgnoreCase("bstack:options")) {
                HashMap bstackOptionsMap = (HashMap) pair.getValue();
                bstackOptionsMap.putAll((HashMap) capabilities.getCapability("bstack:options"));
                capabilities.setCapability(pair.getKey().toString(), bstackOptionsMap);
            }
        }

        if (capabilities.getCapability("bstack:options") != null) {
            HashMap<String, String> bstackOptionsMap = (HashMap<String, String>) capabilities.getCapability("bstack:options");
            if ((bstackOptionsMap.get("local") != null &&
                    bstackOptionsMap.get("local").equalsIgnoreCase("true") && (local == null || !local.isRunning()))) {
                local = new Local();
                Map<String, String> options = new HashMap<String, String>();
                options.put("key", accessKey);
                local.start(options);
            }
            bstackOptionsMap.put("source", "testng:sample-selenium-4:v1.1");
        }

        ClientConfig customConfig = ClientConfig.defaultConfig().readTimeout(Duration.ofMinutes(15))
                .connectionTimeout(Duration.ofMinutes(15));
        try {
            driver = RemoteWebDriver.builder()
                    .config(customConfig)
                    .address(new URL("https://"+username+":"+accessKey+"@"+config.get("server")+"/wd/hub"))
                    .oneOf(capabilities)
                    .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
