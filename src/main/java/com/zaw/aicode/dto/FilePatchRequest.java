package com.zaw.aicode.dto;


import java.util.List;

import lombok.Data;


/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/2
 */
@Data
public class FilePatchRequest {

    private String projectPath;

    private String patchStr;

    private List<FilePatchDTO> patchDTOS;

}

