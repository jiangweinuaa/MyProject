package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 购物车商品删除
 * @author: wangzyc
 * @create: 2021-05-27
 */
@Data
public class DCP_ShoppingCartDeleteReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<level2Elm> pluList;  // 商品列表
    }

    @Data
    public class level2Elm{
        private String item;        // 项次（查询服务的item）
    }
}
