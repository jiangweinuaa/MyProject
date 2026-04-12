package com.dsc.spos.thirdpart.duandian;

import lombok.Data;

import java.util.List;

@Data
public class queryOrderRes extends responseDTO {
    private List<channelOrderDTO> res;
    private int pageNo;
    private int pageSize;
    private int totalSize;
    /**
     * 是否最后一页 1是、0否
     */
    private String lastPage;

}
