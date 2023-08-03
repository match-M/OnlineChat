package com.match.model.basic.tools;

import com.alibaba.fastjson.JSONObject;

/**
 * 这是解析服务器消息的工具类，服务器采用json数据
 * @author match
 */
public class ParsingTools {

    private JSONObject data;

    public ParsingTools(String data) {
        if(data == null)
            return;
        this.data = JSONObject.parseObject(data);
    }

    /**
     *
     * @param key 要获取的字符串的key
     * @return 通过key解析到的的value
     */
    public String getString(String key){
        return data.getString(key);
    }

    public Object get(String key){
        return data.get(key);
    }
}
