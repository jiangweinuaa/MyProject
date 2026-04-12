package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BatchLocationStockAllocReq;
import com.dsc.spos.json.cust.res.DCP_BatchLocationStockAllocRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.batchLocation.BatchLocationPlu;
import com.dsc.spos.utils.batchLocation.BatchLocationStockAlloc;
import com.dsc.spos.utils.batchLocation.WarehouseLocationPlu;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BatchLocationStockAlloc extends SPosBasicService<DCP_BatchLocationStockAllocReq, DCP_BatchLocationStockAllocRes> {

    @Override
    protected boolean isVerifyFail(DCP_BatchLocationStockAllocReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BatchLocationStockAllocReq> getRequestType() {
        return new TypeToken<DCP_BatchLocationStockAllocReq>() {
        };
    }

    @Override
    protected DCP_BatchLocationStockAllocRes getResponseType() {
        return new DCP_BatchLocationStockAllocRes();
    }

    @Override
    protected DCP_BatchLocationStockAllocRes processJson(DCP_BatchLocationStockAllocReq req) throws Exception {
        DCP_BatchLocationStockAllocRes res = this.getResponseType();

        List<DCP_BatchLocationStockAllocReq.Datas> reqDatas = req.getRequest().getDatas();
        List<BatchLocationPlu> batchLocationPlus = new ArrayList<>();

        for (DCP_BatchLocationStockAllocReq.Datas reqData : reqDatas) {
            BatchLocationPlu onePlu = new BatchLocationPlu();
            onePlu.setId(Integer.parseInt(reqData.getItem()));
            onePlu.setWarehouse(reqData.getWarehouse());
            onePlu.setPQty(reqData.getPQty());
            onePlu.setPUnit(reqData.getPUnit());
            onePlu.setPluNo(reqData.getPluNo());

            batchLocationPlus.add(onePlu);
        }

        List<WarehouseLocationPlu> allocList = BatchLocationStockAlloc.batchLocationStockAlloc(batchLocationPlus);

        res.setDatas(new ArrayList<>());
        allocList = allocList.stream().sorted(Comparator.comparing(WarehouseLocationPlu::getId)).collect(Collectors.toList());
        int nowId = -1;

        DCP_BatchLocationStockAllocRes.Datas nowData = null;
        for (WarehouseLocationPlu alloc : allocList) {

            if (-1 == nowId || nowId != alloc.getId()) {
                DCP_BatchLocationStockAllocRes.Datas resData = res.new Datas();

                resData.setItem(String.valueOf(alloc.getId()));
                resData.setPUnit(alloc.getPUnit());
                resData.setPluNo(alloc.getPluNo());

                resData.setBatchList(new ArrayList<>());

                res.getDatas().add(resData);

                nowId = alloc.getId();
                nowData = resData;
            }

            DCP_BatchLocationStockAllocRes.BatchList batchList = res.new BatchList();

            batchList.setLocation(alloc.getLocation());
            batchList.setBatchNo(alloc.getBatchNo());
            batchList.setQty(alloc.getStock());
            batchList.setAllocQty(alloc.getAllocQty());
            batchList.setProdDate(alloc.getProdDate());
            batchList.setValidDate(alloc.getValidDate());

            nowData.getBatchList().add(batchList);

        }


        res.setSuccess(true);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_BatchLocationStockAllocReq req) throws Exception {
        return "";
    }
}
