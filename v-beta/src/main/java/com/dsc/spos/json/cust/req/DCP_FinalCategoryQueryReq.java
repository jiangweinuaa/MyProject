package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 末级商品分类查询
 * @author: wangzyc
 * @create: 2021-06-16
 */
@Data
public class DCP_FinalCategoryQueryReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String status;  // 有效否-1未启用 100已启用 0已禁用
        // 20220208 增加关键字搜索 By wangzyc
        private String keyTxt; // 搜索关键字，模糊匹配编号和名称
    }
}
