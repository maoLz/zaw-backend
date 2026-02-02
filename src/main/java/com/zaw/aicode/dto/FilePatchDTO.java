package com.zaw.aicode.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilePatchDTO {

    /** 相对路径，例如：views/FlowCanvas.vue */
    private String path;

    /** 对该文件的修改操作 */
    private List<EditDTO> edits;

    private boolean isNew = false;
}
