package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: KDS机器人查询
 * @author: wangzyc
 * @create: 2021-09-16
 */
@Data
public class DCP_KdsCookQuery_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String machineId; // 机台编号
        private String sortId; // 顺序
        private String cookId; // 机器人编号
        private String cookName; // 机器人名称
        private String status; // 启用状态Y/N
    }
}
