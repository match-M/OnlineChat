package com.match.model.basic.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 这个类是生成json数据的
 * @author match
 */
public class GeneratingTools {

    private String data;
    private JSONObject jsonData = new JSONObject();

    public GeneratingTools() { }

    public GeneratingTools(String data){
        this.data = data;
    }

    public JSONObject json(){
        return JSON.parseObject(data);
    }

    public void json(String key, Object value){
        jsonData.put(key, value);
    }

    public JSONObject getJson() { return jsonData; }
    public void initJson() {
        jsonData.clear();
    }
}
