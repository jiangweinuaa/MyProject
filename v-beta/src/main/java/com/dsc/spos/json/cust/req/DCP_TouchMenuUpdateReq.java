package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: DCP_TouchMenuUpdate
 * @author: wangzyc
 * @create: 2021-06-15
 */
@Data
public class DCP_TouchMenuUpdateReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String oprType;         // 操作类型：1-创建 2-修改
        private String menuNo;          // 菜单编码，修改时不可改
        private List<level2Elm> menuName_lang;  // 菜单名称多语言
        private String memo;            // 备注
        private String priority;        // 优先级
        private String status;          // 状态：-1未启用100已启用0已禁用
        private String beginDate;       // 生效日期YYYYMMDD
        private String endDate;         // 失效日期YYYYMMDD
        private String restrictShop;    // 适用门店：0-所有门店 1-指定门店 2-排除门店
        private String restrictChannel; // 适用门店：0-所有门店 1-指定门店 2-排除门店
        private String restrictPeriod;  // 适用时段：0-所有时段 1-指定时段
        private List<level3Elm> rangeList;  // 适用范围明细
        
        private String copyGoodsMenuNo;//复制商品来源的菜单编码
    }

    @Data
    public class level2Elm{
        private String langType;        // 语言类别 zh_CN：中文简体  zh_TW：繁体中文 en_US：英文
        private String name;            // 名称
    }

    @Data
    public class level3Elm{
        private String rangeType;        // 适用范围：1-公司2-门店3-渠道 5-时段
        private String id;               // 编码
        private String name;            // 名称
    }


}
