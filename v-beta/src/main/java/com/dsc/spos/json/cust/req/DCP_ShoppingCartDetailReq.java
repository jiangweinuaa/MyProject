package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: 购物车查询
 * @author: wangzyc
 * @create: 2021-05-27
 */
@Data
public class DCP_ShoppingCartDetailReq extends JsonBasicReq {
    private level1Elm request;

    public class level1Elm{

    }
}
