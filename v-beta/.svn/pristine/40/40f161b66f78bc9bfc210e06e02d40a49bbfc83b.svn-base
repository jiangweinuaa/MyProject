package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：ExternalUseridToPending
 * 服务说明：external_userid查询pending_id
 * 企微URL：https://developer.work.weixin.qq.com/document/path/97108
 * @author jinzma
 * @since  2024-02-22
 */
@Data
public class ExternalUseridToPending {
    private String errcode;
    private String errmsg;
    private List<Result> result;
    @Data
    public class Result {
      private String external_userid;
      private String pending_id;
    }
}
