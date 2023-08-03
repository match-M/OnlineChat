package com.match.model.basic.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.match.model.basic.constants.DefaultConfigFileName;
import com.match.model.basic.constants.DefaultConfigName;
import com.match.model.basic.constants.InitErrorInfoConst;
import com.match.model.basic.exception.ConfigNameNotFoundException;
import com.match.model.basic.tools.ConfigTools;

public class ConfigList {

    private ConfigTools ConfigTools;
    private static HashMap<String, String> configList;

    public ConfigList() {
        ConfigTools = new ConfigTools(DefaultConfigFileName.USER_SETTING_CONFIG);
        get();
    }
    public ConfigList(String configFileName){
        ConfigTools = new ConfigTools(configFileName);
        get();
    }

    private void get(){
        try {
            configList = ConfigTools.getConfigReader();
        }catch (IOException e){
            e.printStackTrace();

        }
    }

    private void up(String configFileName){
        configList.clear();
        if(configFileName == null)
            configFileName = DefaultConfigFileName.USER_SETTING_CONFIG;
        try {
            configList = ConfigTools.getConfigReader(configFileName);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void upConfigList() {
        up(null);
    }

    public void upConfigList(String configFileName) {
        up(configFileName);
    }

    public String getConfig(String key) throws ConfigNameNotFoundException {
        String value;
        if(configList.get(key) == null)
            throw new ConfigNameNotFoundException(InitErrorInfoConst.INIT_CONFIG_ERROR);
        value = configList.get(key);

        return value;
    }
}
