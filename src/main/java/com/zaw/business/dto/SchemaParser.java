package com.zaw.business.dto;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.*;

public class SchemaParser {

    public static List<FieldInfo> parseSchema(
            OpenAPI openAPI,
            Schema<?> schema
    ) {
        schema = resolveRef(openAPI, schema);


        List<FieldInfo> fields = new ArrayList<>();

        if(!schema.getTypes().contains("object")){
            return fields;
        }


        Map<String, Schema> properties = schema.getProperties();
        List<String> requiredList = schema.getRequired();

        if (properties == null) {
            return fields;
        }

        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName.equals("functionType")){
                System.out.println(fieldName);
            }
            Schema<?> fieldSchema = entry.getValue();

            boolean required = requiredList != null && requiredList.contains(fieldName);

            FieldInfo fieldInfo = parseField(openAPI, fieldName, fieldSchema, required);
            fields.add(fieldInfo);
        }

        return fields;
    }

    private static FieldInfo parseField(
            OpenAPI openAPI,
            String fieldName,
            Schema<?> schema,
            boolean required
    ) {
        schema = resolveRef(openAPI, schema);

         Set<String> types = schema.getTypes();

        FieldInfo field = new FieldInfo(fieldName, types, required);

        // 对象类型 → 递归
        if (types.contains("object")) {
            field.children.addAll(parseSchema(openAPI, schema));
        }

        // 数组类型
        if (types.contains("array")) {
            Schema<?> itemSchema = (Schema<?>) schema.getItems();
            FieldInfo itemField = parseField(openAPI, fieldName + "[]", itemSchema, true);
            field.children.add(itemField);
        }
        if(schema.getEnum() != null){
            field.setEnums(schema.getEnum());
        }

        return field;
    }

    private static Schema<?> resolveRef(OpenAPI openAPI, Schema<?> schema) {
        if (schema.get$ref() == null) {
            return schema;
        }
        String name = schema.get$ref().substring(schema.get$ref().lastIndexOf("/") + 1);
        return openAPI.getComponents().getSchemas().get(name);
    }
}
