package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProcessTaskCloseReq  extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_ProcessTaskCloseReq.LevelRequest request;

    @Data
    public class LevelRequest{
       private List<ProcessTaskList> processTaskList;
       private String close;
    }

    @Data
    public class ProcessTaskList{
        private String processTaskNo;
    }

}


