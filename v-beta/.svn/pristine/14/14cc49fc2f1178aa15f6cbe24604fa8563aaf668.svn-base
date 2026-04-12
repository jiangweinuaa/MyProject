package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComCustomDetail
 * 服务说明：查询企微客户详情
 * @author jinzma
 * @since  2024-01-25
 */
@Data
public class DCP_ISVWeComCustomDetailRes extends JsonRes {
    private Datas datas;
    @Data
    public class Datas{
        private String externalUserId;
        private String name;
        private String memberId;
        private String avatar;
        private String customType;
        private String gender;
        private String status;

        private List<Follow> followList;
        private List<MemberTag> memberTagList;
        private List<Tag> tagList;
        private List<RemarkMobile> remarkMobiles;

    }
    @Data
    public class Follow{
        private String userId;
        private String userName;
        private String addWay;
        private String followRemark;
        private String followDescrip;
        private String follwTime;
    }
    @Data
    public class MemberTag{
        private String tagId;
        private String tagName;
    }
    @Data
    public class Tag{
        private String tagId;
        private String tagName;
        private String userId;
    }
    @Data
    public class RemarkMobile{
        private String mobile;
        private String userId;
    }
}
