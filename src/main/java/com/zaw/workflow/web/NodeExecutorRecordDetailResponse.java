package com.zaw.workflow.web;

import com.zaw.workflow.enums.ExecutorStatus;
import java.util.Date;
import lombok.Data;

@Data
public class NodeExecutorRecordDetailResponse {
    private Long nodeId;
    private String nodeKey;
    private ExecutorStatus status;
    private Date startTime;
    private Date endTime;
    private String input;
    private String output;
}
