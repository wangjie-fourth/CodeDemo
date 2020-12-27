package com.github.wang.jie.data;

import com.alibaba.fastjson.JSON;
import com.github.wang.jie.UserDetailInfo;
import com.github.wang.jie.UserInfo;
import org.apache.commons.lang3.SerializationUtils;

import java.lang.reflect.Type;

public class MyInheritableThreadLocal<T> extends InheritableThreadLocal<T>{

    @Override
    protected T childValue(T parentValue) {
        String json = JSON.toJSONString(parentValue);
        T object = JSON.parseObject(json, (Type) UserInfo.class);
        return super.childValue(object);
    }
}
