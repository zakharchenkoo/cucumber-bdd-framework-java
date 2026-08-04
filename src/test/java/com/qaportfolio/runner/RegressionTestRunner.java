package com.qaportfolio.runner;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.qaportfolio.steps")
@ConfigurationParameter(
        key = FILTER_TAGS_PROPERTY_NAME,
        value = "@smoke or @regression"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
public class RegressionTestRunner {
}
