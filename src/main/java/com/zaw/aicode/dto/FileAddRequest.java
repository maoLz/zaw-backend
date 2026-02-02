package com.zaw.aicode.dto;

import java.util.List;

import lombok.Data;

@Data
public class FileAddRequest {

    private String basePath;

    private String patchStr;

    private List<FileAddPatch> patchDTOS;

    @Data
    public static class FileAddPatch{

        private String fileName;

        private String content;
    }
}
