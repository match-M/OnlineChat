package com.match.model.basic.constants;

import java.util.ArrayList;
import java.util.List;

public class DefaultConfigFileName {
    public static final List<String> CONFIG_NAME_LIST = new ArrayList<>();
    public static final String SERVER = "server";
    public static final String USER_SETTING_CONFIG = "userSettingConfig";
    static {
        CONFIG_NAME_LIST.add(SERVER);
        CONFIG_NAME_LIST.add(USER_SETTING_CONFIG);
    }
}
