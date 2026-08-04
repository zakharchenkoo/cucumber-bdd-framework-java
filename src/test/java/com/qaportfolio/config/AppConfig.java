package com.qaportfolio.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.Sources({
        "system:properties",
        "file:config/app.properties"
})
public interface AppConfig extends Config {

    AppConfig INSTANCE = ConfigFactory.create(AppConfig.class);

    static AppConfig getInstance() {
        return INSTANCE;
    }

    @Key("base.url")
    @DefaultValue("https://www.saucedemo.com")
    String baseUrl();

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("explicit.wait")
    @DefaultValue("10")
    int explicitWait();
}
