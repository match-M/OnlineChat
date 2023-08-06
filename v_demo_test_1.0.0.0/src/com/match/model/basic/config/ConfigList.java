package com.match.model.basic.config;

import java.io.IOException;
import java.util.HashMap;

import com.match.model.basic.constants.DefaultConfigFileName;
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

    /**
     * 获取配置信息
     */
    private void get(){
        try {
            configList = ConfigTools.getConfigReader();
        }catch (IOException e){
            e.printStackTrace();

        }
    }

    /**
     * 更新配置信息
     * @param configFileName 配置文件名
     */
    private void up(String configFileName){
        //判断配置列表是否为空
        if(configList != null)
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
        //防止出现配置文件丢失修复后依然无法找到文件的问题
        ConfigTools = new ConfigTools(DefaultConfigFileName.USER_SETTING_CONFIG);
        //更新配置信息
        up(null);
    }

    public void upConfigList(String configFileName) {
        ConfigTools = new ConfigTools(configFileName);
        up(configFileName);
    }

    /**
     * 获取配置值
     * @param key 配置名称
     * @return 返回配置值
     * @throws ConfigNameNotFoundException 如果配置文件丢失或者配置文件损害则会抛出这个异常
     */
    public String getConfig(String key) throws ConfigNameNotFoundException {
        String value;
        if(configList.get(key) == null)
            throw new ConfigNameNotFoundException(InitErrorInfoConst.INIT_CONFIG_ERROR);
        value = configList.get(key);

        return value;
    }
}
