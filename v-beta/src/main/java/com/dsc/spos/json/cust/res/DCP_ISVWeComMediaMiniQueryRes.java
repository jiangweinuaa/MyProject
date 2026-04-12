package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服务函数：DCP_ISVWeComMediaMiniQuery
 * 服务说明：小程序页面路径查询
 * @author jinzma
 * @since  2024-03-07
 */
@Data
public class DCP_ISVWeComMediaMiniQueryRes extends JsonRes {


    private List<Datas> datas;

    @Data
    public class Datas {
        private String miniId;
        private String title;
        private String appId;
        private String miniUrl;
        private String mediaUrl;
        private String createTime;
        private String mediaId;
        private String lastModiTime;
        private String weComPicUrl;

    }

}
