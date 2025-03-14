package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ToolUtil {

    // 把实体转换成Map
    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = BeanUtil.beanToMap(bean, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> {

                    if (fieldValue == null) {
                        return null;
                    } else if (fieldValue instanceof LocalDateTime) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        return ((LocalDateTime) fieldValue).format(formatter);
                    } else if (fieldValue instanceof Integer) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Long) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Double) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Float) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Boolean) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Byte) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Short) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof Character) {
                        return fieldValue.toString();
                    } else if (fieldValue instanceof String) {
                        return fieldValue;
                    } else {
                        return fieldValue.toString();
                    }
                }));
        return map;
    }

}
