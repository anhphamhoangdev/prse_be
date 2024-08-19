package com.hcmute.prse_be.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Config {

    public static final Map<String, String> localconfig = new HashMap<>();
    public static final String CONFIG_HOME = "conf";
    public static final String CONFIG_FILE = "config.ini";
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    static final CompositeConfiguration config;

    public static String getParam(String section, String name) {
        String key = section + "." + name;

        String value = localconfig.get(key);
        if (value != null) {
            int a  = 1;
            return value;
        }

        value = config.getString(key);
        if (value != null) {
            localconfig.put(key, value);
        }
        return value;
    }

    static {
        String HOME_PATH = System.getProperty("apppath");
        String APP_ENV = System.getProperty("appenv");

        if (APP_ENV == null) {
            APP_ENV = "";
        }
        if (!"".equals(APP_ENV)) {
            APP_ENV = APP_ENV + ".";
        }
        if (HOME_PATH == null) {
            HOME_PATH = "";
        } else {
            HOME_PATH = HOME_PATH + File.separator;
        }

        config = new CompositeConfiguration();

        File configFile = new File(HOME_PATH + CONFIG_HOME + File.separator + APP_ENV + CONFIG_FILE);
        try {
            config.addConfiguration(new HierarchicalINIConfiguration(configFile));

            Iterator ii = config.getKeys();

            while (ii.hasNext()) {
                String key = (String) ii.next();
                localconfig.put(key, config.getString(key));
            }

        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
    }
}
