package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CdsSetCreateReq;
import com.dsc.spos.json.cust.res.DCP_CdsSetCreateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: CDS叫号屏设置新增
 * @author: wangzyc
 * @create: 2022-05-25
 */
public class DCP_CdsSetCreate extends SPosAdvanceService<DCP_CdsSetCreateReq, DCP_CdsSetCreateRes> {
    @Override
    protected void processDUID(DCP_CdsSetCreateReq req, DCP_CdsSetCreateRes res) throws Exception {
        DCP_CdsSetCreateReq.level1Elm request = req.getRequest();
        String eId = req.geteId();

        try {
            // 获取模版编号 CDS+日期20220525+流水号01
            String baseSetNo = queryMaxBillNo(req);

            String LASTMODITIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String opNO = req.getOpNO();

            String[] columns_CDSSET ={"EID","TEMPLATEID","STATUS","FILETYPE" ,"RESTRICTSHOP","MEMO","UPDATETIME",
                    "ISCUSTOMCOLOUR","THEMECOLOR","SUBCOLOR", "FONTCOLORTYPE","VOICETEXT","VOICETIMES","ROLLTIME",
                    "SHOWWAIMAI","SERVICEURL","UPDATEURL","LASTMODITIME","LASTMODIOPID","COLOURID"
            };

            DataValue[] insValue_CDSSET = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(baseSetNo, Types.VARCHAR),
                            new DataValue(request.getStatus(), Types.VARCHAR),
                            new DataValue(request.getFileType(),Types.VARCHAR),
                            new DataValue(request.getRestrictShop(), Types.VARCHAR),
                            new DataValue(request.getMemo(), Types.VARCHAR),
                            new DataValue(request.getUpdateTime() ,Types.VARCHAR),
                            new DataValue(request.getIsCustomColour() ,Types.VARCHAR),
                            new DataValue(request.getThemeColor(), Types.VARCHAR),
                            new DataValue(request.getSubColor(), Types.VARCHAR),
                            new DataValue(request.getFontColorType(),Types.VARCHAR),
                            new DataValue(request.getVoiceText(),Types.VARCHAR),
                            new DataValue(request.getVoiceTimes(), Types.VARCHAR), //修改人、时间等信息
                            new DataValue(request.getRollTime() ,Types.VARCHAR),
                            new DataValue(request.getShowWaimai() ,Types.VARCHAR),
                            new DataValue(request.getServiceUrl() ,Types.VARCHAR),
                            new DataValue(request.getUpdateUrl() , Types.VARCHAR),
                            new DataValue(LASTMODITIME ,Types.DATE),
                            new DataValue(opNO ,Types.VARCHAR),
                            new DataValue(request.getColourId() ,Types.VARCHAR),

                    };

            InsBean ib_CDSSET = new InsBean("DCP_CDSSET", columns_CDSSET);
            ib_CDSSET.addValues(insValue_CDSSET);
            this.addProcessData(new DataProcessBean(ib_CDSSET));

            List<DCP_CdsSetCreateReq.level3Elm> rangeList = request.getRangeList();
            if(!CollectionUtils.isEmpty(rangeList)){
                String[] columns_CDSSET_SHOP ={"EID","TEMPLATEID","SHOPID" };
                for (DCP_CdsSetCreateReq.level3Elm level3Elm : rangeList) {
                    DataValue[] insValue_CDSSET_SHOP = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(baseSetNo, Types.VARCHAR),
                                    new DataValue(level3Elm.getShopId(), Types.VARCHAR),

                            };

                    InsBean ib_CDSSET_SHOP = new InsBean("DCP_CDSSET_SHOP", columns_CDSSET_SHOP);
                    ib_CDSSET_SHOP.addValues(insValue_CDSSET_SHOP);
                    this.addProcessData(new DataProcessBean(ib_CDSSET_SHOP));
                }

            }

            List<DCP_CdsSetCreateReq.level2Elm> fileList = request.getFileList();
            if(!CollectionUtils.isEmpty(fileList)){
                String[] columns_CDSSET_ADVERT ={"EID","TEMPLATEID","SORTID","FILENAME","LASTMODITIME","LASTMODIOPID" };
                for (DCP_CdsSetCreateReq.level2Elm level2Elm : fileList) {
                    DataValue[] insValue_CDSSET_ADVERT = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(baseSetNo, Types.VARCHAR),
                                    new DataValue(level2Elm.getItem(), Types.VARCHAR),
                                    new DataValue(level2Elm.getFileName(), Types.VARCHAR),
                                    new DataValue(LASTMODITIME, Types.DATE),
                                    new DataValue(opNO, Types.VARCHAR),

                            };

                    InsBean ib_CDSSET_ADVERT = new InsBean("DCP_CDSSET_ADVERT", columns_CDSSET_ADVERT);
                    ib_CDSSET_ADVERT.addValues(insValue_CDSSET_ADVERT);
                    this.addProcessData(new DataProcessBean(ib_CDSSET_ADVERT));
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CdsSetCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CdsSetCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CdsSetCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CdsSetCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsSetCreateReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getStatus())) {
            errMsg.append("状态不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getUpdateTime())) {
            errMsg.append("自动刷新时间不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getIsCustomColour())) {
            errMsg.append("是否启用自定义颜色Y/N不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getFileType())) {
            errMsg.append("广告图类型不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getServiceUrl())) {
            errMsg.append("服务地址不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getUpdateUrl())) {
            errMsg.append("更新地址不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getRestrictShop())) {
            errMsg.append("适用门店不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getShowWaimai())) {
            errMsg.append("Y/N大屏是否展示外卖订单不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CdsSetCreateReq> getRequestType() {
        return new TypeToken<DCP_CdsSetCreateReq>(){};
    }

    @Override
    protected DCP_CdsSetCreateRes getResponseType() {
        return new DCP_CdsSetCreateRes();
    }

    private String queryMaxBillNo(DCP_CdsSetCreateReq req) throws Exception{
        String billNo = "";
        String eId = req.geteId();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String bDate = sdf.format(now);

        String sql = " select max(TEMPLATEID) as TEMPLATEID from DCP_CDSSET where eId = '"+eId+"' and TEMPLATEID like 'CDS"+bDate+"%%%' " ;

        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {

            billNo = (String) getQData.get(0).get("TEMPLATEID");

            if (billNo != null && billNo.length() > 0) {
                long i;
                billNo = billNo.substring(3, billNo.length());
                i = Long.parseLong(billNo) + 1;
                billNo = i + "";
                billNo = "CDS" + billNo;

            } else {
                billNo = "CDS" + bDate + "01";
            }
        } else {
            billNo = "CDS" + bDate + "01";
        }

        return billNo;
    }

}
