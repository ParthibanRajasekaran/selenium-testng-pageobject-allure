package com.template.project.common;

import lombok.SneakyThrows;

import javax.naming.ConfigurationException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Constants {

  public static final int MAX_RETRY_COUNT = 1;

  public static final long FLUENT_WAIT_TIMEOUT_SECONDS = 60;

  public static final long FLUENT_WAIT_POLLING_INTERVAL_MILLISECONDS = 5000;

  public static String CONFIG_FILE_DIRECTORY;

  public static String DATA_FILE_DIRECTORY;

  protected static URL findResource(final String name) throws ConfigurationException {
    if (null == name || "".equals(name)) {
      throw new ConfigurationException("Must provide a valid folder name");
    }
    final URL url = Constants.class.getClassLoader().getResource(name);
    if (null == url) {
      throw new ConfigurationException(name + " could not be found");
    }
    return url;
  }

  static {
    try {
      CONFIG_FILE_DIRECTORY = findResource("config").getPath();
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    try {
      DATA_FILE_DIRECTORY = findResource("data").getPath();
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * The configured path to configuration files.
   *
   * @return The location of the config folder.
   */
  @SneakyThrows
  public static String getConfigPath() {
    try {
      return URLDecoder.decode(CONFIG_FILE_DIRECTORY, String.valueOf(StandardCharsets.UTF_8));
    } catch (final UnsupportedEncodingException e) {
      throw new ConfigurationException("Invalid configuration, encoding not valid");
    }
  }

  /**
   * The configured path to data files.
   *
   * @return The location of the data folder.
   */
  public static String getDataPath() throws ConfigurationException {
    try {
      return URLDecoder.decode(DATA_FILE_DIRECTORY, String.valueOf(StandardCharsets.UTF_8));
    } catch (final UnsupportedEncodingException e) {
      throw new ConfigurationException("Invalid configuration, encoding not valid");
    }
  }

  public String getSeleniumConfigPath() throws ConfigurationException {
    return findResource("selenium_config.properties").getPath();
  }
}
