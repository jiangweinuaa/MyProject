package com.dsc.spos.utils.batchLocation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"pluNo","pUnit","batchNo","warehouse","location"})
public class WarehouseLocationPlu {

    private int id;
    private String pluNo; //品号
    private String featureNo; //特征码
    private String pUnit; //单位
    private String stock; //库存量
    private String batchNo; //批号
    private String allocQty; //分配量
    private String warehouse; //仓库
    private String location;  //库位
    private String validDate; //有效日期
    private String prodDate; //生产日期

    private String pickGroupNo; //拣货分区
    private int sortId;     //拣货分组
    private int newId;      //新分配id，重新回写获得
}
