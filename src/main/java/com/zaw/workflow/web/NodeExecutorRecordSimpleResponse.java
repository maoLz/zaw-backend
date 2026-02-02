package com.zaw.workflow.web;

import com.zaw.workflow.enums.ExecutorStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeExecutorRecordSimpleResponse {

    private Long id;

    private ExecutorStatus status;

    private String nodeKey;

    private Date startTime;
}
