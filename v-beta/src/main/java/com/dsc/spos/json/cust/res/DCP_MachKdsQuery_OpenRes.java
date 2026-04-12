package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_MachKdsQuery_Open
 * 服务说明：KDS信息查询
 * @author wangzyc
 * @since  2021/4/13
 */
@Data
public class DCP_MachKdsQuery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
//        private String eId;                      // 企业编码
        private List<level2Elm> machShopList;   // 生产机构列表

        private String posService;              // POS服务地址
    }

    @Data
    public class level2Elm{
        private String machShopId;              // 生产机构编码
        private String machShopName;            // 生产机构名称
        private String kds;                     // KDS是否启用 Y/N
        private List<level3Elm> stallList;      // 档口列表

        private String completeImage;           // 是否启用生产完成插图Y/N
        private String isQrcode;                // 是否生成订单二维码Y/N
        private List<level6Elm> imageList;      // 图片列表

    }

    @Data
    public class level3Elm{
        private String stallId;                  // 档口编号
        private String stall;                    // 档口名称
        private String tagType;                  // 商品范围 0.全部商品 1.指定生产标签
        private List<level4Elm> subTagList;      // 指定生产标签
        private level5Elm funcList;              // 功能权限清单
    }

    @Data
    public class level4Elm{
        private String tagNo;                     // 生产标签编号
    }

    @Data
    public class level5Elm{
        private String isOrder;                         // 订单中心Y/N
        private String achievements;                    // 绩效统计Y/N
        private String employee;                        // 员工认证Y/N
        private String goodsDetails;                    // 商品明细Y/N
    }

    @Data
    public class level6Elm{
        private String imageUrl;                        // 图片地址，需拼接

    }
 }
