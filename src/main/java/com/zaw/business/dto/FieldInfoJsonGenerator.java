package com.zaw.business.dto;

import cn.hutool.core.collection.CollectionUtil;

import java.util.*;

public class FieldInfoJsonGenerator {

    public static Map<String, Object> generate(List<FieldInfo> fields) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (FieldInfo field : fields) {
            result.put(field.name, generateValue(field));
        }
        return result;
    }

    private static Object generateValue(FieldInfo field) {

        // array
        if (field.types.contains("array")) {
            List<Object> list = new ArrayList<>();

            if (!field.children.isEmpty()) {
                // array 只取一个元素示例
                list.add(generateValue(field.children.get(0)));
            }

            return list;
        }

        // object
        if (field.types.contains("object")) {
            Map<String, Object> obj = new LinkedHashMap<>();
            for (FieldInfo child : field.children) {
                obj.put(child.name, generateValue(child));
            }
            return obj;
        }
        if(CollectionUtil.isNotEmpty(field.getEnums())){
            Map<String,Object> map = new HashMap<>();
            map.put("enum",field.getEnums());
            map.put("type","enum");
            return map;
        }

        // primitive
        return generatePrimitive(field.types, field.name);
    }

    private static Object generatePrimitive(Set<String> types, String fieldName) {

        if (types.contains("integer") || types.contains("int")) {
            return 0;
        }

        if (types.contains("number") || types.contains("long")
                || types.contains("double") || types.contains("float")) {
            return 0;
        }

        if (types.contains("boolean")) {
            return false;
        }

        if (types.contains("string")) {
            return generateString(fieldName);
        }

        return null;
    }

    private static String generateString(String fieldName) {
        // 可根据字段名智能一点
        if (fieldName.toLowerCase().contains("time")
                || fieldName.toLowerCase().contains("date")) {
            return "2026-01-01T00:39:10.620Z";
        }
        return "string";
    }
}

