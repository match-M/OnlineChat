package com.match.model.basic.tools;

import java.io.*;
import java.util.*;

import com.match.model.basic.constants.DefaultConfigName;
import com.match.model.basic.constants.DefaultConfigValues;
import com.match.model.basic.constants.DefaultConfigFileName;
import com.match.model.basic.constants.InitErrorInfoConst;
import com.match.model.basic.exception.ConfigFileNotFoundException;
import com.match.model.basic.exception.ConfigNameNotFoundException;

public class ConfigTools {

    private String configFileName; //配置文件的名字，不需要后缀只要写文件名即可
    public ConfigTools(){}
    public  ConfigTools(String configFileName){
        this.configFileName = configFileName;
    }


    private boolean newConfigFile(String configFileName){
        if(configFileName == null)
            configFileName = this.configFileName;
        File configFile = new File("./config/"+configFileName+".properties");
        boolean result = false;
        try {
            result = configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建新的配置文件
     * @return true-创建成功，false-创建失败
     */
    public boolean createConfigFile(){
        return newConfigFile(null);
    }

    /**
     * 创建新的配置文件，这个方法一般用于修复配置文件时使用
     */
    public void createConfigFile(String configFileName){
        newConfigFile(configFileName);
    }

    /**
     * 这是写入默认服务器配置
     * @param ip 默认服务器的IP地址
     * @param port 默认服务器的主机端口
     */
    public void writeConfig(String ip, int port){
        Properties config = new Properties();
        try {
            FileWriter fileWriter = new FileWriter("./config/"+DefaultConfigFileName.SERVER+".properties");
            config.setProperty(DefaultConfigName.IP, ip);
            config.setProperty(DefaultConfigName.PORT, String.valueOf(port));
            config.store(fileWriter, "ServerAddressConfig");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 因为不确定配置名称和配置内容的数量所以采用数组的形式
     * @param configKey 配置名称
     * @param configValue 配置内容
     * @param explanatoryNote 注释
     */
    public void writeConfig(String[] configKey, Object[] configValue,
                            String explanatoryNote){
        Properties config = new Properties();
        int i = 0;
        try {
            FileWriter fileWriter = new FileWriter("./config/"+configFileName+".properties");
            for(String key : configKey){
                config.setProperty(key, String.valueOf(configValue[i]));
                i++;
            }
            config.store(fileWriter, explanatoryNote);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改配置，支持批量修改
     * @param keys 配置名称
     * @param values 新的配置内容
     */
    public void upConfig(String[] keys, Object[] values){
        int i = 0;
        FileWriter fileWriter;
        Properties config = new Properties();
        try {
            HashMap<String, String> configList = getConfigReader();
            fileWriter = new FileWriter("./config/"+configFileName+".properties");
            for (String key : keys){
                configList.replace(key, String.valueOf(values[i]));
                i++;
            }
            Set<String> Keys = configList.keySet();
            Iterator<String> it = Keys.iterator();
            String Key;
            while(it.hasNext()){
                Key = it.next();
                config.setProperty(Key, configList.get(Key));
            }
            config.store(fileWriter, null);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加配置
     * @param key 配置名称
     * @param value 新的配置内容
     */
    public void add(String key, Object value){
        FileWriter fileWriter;
        Properties config = new Properties();
        try {
            fileWriter = new FileWriter("./config/"+configFileName+".properties", true);
            config.setProperty(key, String.valueOf(value));
            config.store(fileWriter, null);
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件
     * @return 返回包含配置名称和配置内容的hashmap，key-配置名称，value-配置内容
     */
    private HashMap<String, String> getConfig(String configFileName) throws IOException {
        Properties prop = new Properties();
        HashMap<String, String> config = new HashMap<>();
        if (configFileName == null)
            configFileName = this.configFileName;
        FileReader fileReader = new FileReader("./config/" + configFileName + ".properties");
        prop.load(fileReader);
        Set<String> keySet = prop.stringPropertyNames();
        for (String key : keySet) {
            config.put(key, prop.getProperty(key));
        }
        fileReader.close();
       /* try {

        }catch (IOException e){
            e.printStackTrace();
        }*/
        return config;
    }

    /**
     *这是用于检查默认配置文件是否存在和是否损坏的
     * @return key - 是配置文件的名称，value - 存有这个配置文件状态码的hashmap集合
     * code:-1 文件不存在
     * code:0 文件内容损坏
     * code:1 文件正常
     */
    private HashMap<String, HashMap<String, Integer>> check() {
        HashMap<String, HashMap<String, Integer>> results = new HashMap<>();
        HashMap<String, Integer> code = new HashMap<>();
        HashMap<String, String> configInfo;
        String key;
        int statCode;
        for (String configFileName : DefaultConfigFileName.CONFIG_NAME_LIST) {
            try {
                statCode = 1; //初始化
                configInfo = getConfig(configFileName);
                switch (configFileName) {
                    case DefaultConfigFileName.SERVER: {
                        if (configInfo.get(DefaultConfigName.IP) == null ||
                                configInfo.get(DefaultConfigName.PORT) == null) {
                            statCode = 0;
                        }
                        break;
                    }
                    case DefaultConfigFileName.USER_SETTING_CONFIG: {
                        if (configInfo.get(DefaultConfigName.VIEW_INTRANET_TEST_PROMPT) == null) {
                            statCode = 0;
                        }
                        break;
                    }
                }
                key = configFileName+"Code";
                code.put(key, statCode);
                results.put(configFileName, code);
            } catch (IOException e) {
                System.out.println("-1");
                statCode = -1;
                key = configFileName+"Code";
                code.put(key, statCode);
                results.put(configFileName, code);
            }
        }
        return results;
    }

    private boolean useDefaultConfig(String configFileName){
        boolean flag = false;
        if (configFileName.equals(DefaultConfigFileName.SERVER)){
            writeConfig(DefaultConfigValues.IP, DefaultConfigValues.PORT);
            flag = true;
            return flag;
        }
        this.configFileName = configFileName;
        String[] keys = new String[]{DefaultConfigName.VIEW_INTRANET_TEST_PROMPT};
        Object[] values = new Object[]{DefaultConfigValues.VIEW_INTRANET_TEST_PROMPT};
        writeConfig(keys, values, null);
        flag = true;
        return flag;
    }

    public boolean repair(){
        HashMap<String, HashMap<String, Integer>> checkRes = getConfigFileCheckResult();
        int statCode;
        String key;
        boolean flag = false;
        for (String configFileName : DefaultConfigFileName.CONFIG_NAME_LIST) {
            HashMap<String, Integer> statCodes = checkRes.get(configFileName);
            key = configFileName+"Code";
            statCode = statCodes.get(key);
            switch (statCode){
                case -1:{
                    createConfigFile(configFileName);
                    flag = useDefaultConfig(configFileName);
                    break;
                }
                case 0:{
                    flag = useDefaultConfig(configFileName);
                    break;
                }
                case 1: flag = true; break;
                default: flag = false; break;
            }
        }
        return flag;
    }

    /**
     * 配置文件自检，一般在初始化时会使用
     */
    public void selfCheck() throws ConfigFileNotFoundException, ConfigNameNotFoundException {
        HashMap<String, HashMap<String, Integer>> checkRes = getConfigFileCheckResult();
        int statCode;
        String key;
        for (String configFileName : DefaultConfigFileName.CONFIG_NAME_LIST) {
            HashMap<String, Integer> statCodes = checkRes.get(configFileName);
            key = configFileName + "Code";
            statCode = statCodes.get(key);
            if (statCode == -1) throw new ConfigFileNotFoundException(InitErrorInfoConst.INIT_CONFIG_FILE_ERROR);
            if(statCode == 0) throw new ConfigNameNotFoundException(InitErrorInfoConst.INIT_CONFIG_ERROR);
        }
    }

    /**
     * 获取配置文件内的配置信息
     * @return 返回配置文件的检查结果
     */
    public HashMap<String, HashMap<String, Integer>> getConfigFileCheckResult(){
        return check();
    }

    /**
     * 获取配置文件，文件名是this.configFileName
     * @return 返回包含配置名称和配置内容的hashmap，key-配置名称，value-配置内容
     */
    public HashMap<String, String> getConfigReader() throws IOException {
        return this.getConfig(null);
    }

    /**
     * 获取配置文件内的配置信息
     * @return 返回包含配置名称和配置内容的hashmap，key-配置名称，value-配置内容
     */
    public HashMap<String, String> getConfigReader(String configFileName) throws IOException  {
        return this.getConfig(configFileName);
    }
}