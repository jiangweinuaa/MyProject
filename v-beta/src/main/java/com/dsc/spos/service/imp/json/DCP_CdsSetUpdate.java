package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CdsSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CdsSetUpdateRes;
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

/**
 * @description: CDS叫号屏设置修改
 * @author: wangzyc
 * @create: 2022-05-25
 */
public class DCP_CdsSetUpdate extends SPosAdvanceService<DCP_CdsSetUpdateReq, DCP_CdsSetUpdateRes> {
    @Override
    protected void processDUID(DCP_CdsSetUpdateReq req, DCP_CdsSetUpdateRes res) throws Exception {
        DCP_CdsSetUpdateReq.level1Elm request = req.getRequest();
        String eId = req.geteId();


        try {
            String LASTMODITIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String opNO = req.getOpNO();
            String baseSetNo = request.getBaseSetNo();

            UptBean up1 = new UptBean("DCP_CDSSET");
            up1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
            up1.addUpdateValue("FILETYPE", new DataValue(request.getFileType(), Types.VARCHAR));
            up1.addUpdateValue("RESTRICTSHOP", new DataValue(request.getRestrictShop(), Types.VARCHAR));
            up1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
            up1.addUpdateValue("UPDATETIME", new DataValue(request.getUpdateTime(), Types.VARCHAR));
            up1.addUpdateValue("ISCUSTOMCOLOUR", new DataValue(request.getIsCustomColour(), Types.VARCHAR));
            up1.addUpdateValue("THEMECOLOR", new DataValue(request.getThemeColor(), Types.VARCHAR));
            up1.addUpdateValue("SUBCOLOR", new DataValue(request.getSubColor(), Types.VARCHAR));
            up1.addUpdateValue("FONTCOLORTYPE", new DataValue(request.getFontColorType(), Types.VARCHAR));
            up1.addUpdateValue("VOICETEXT", new DataValue(request.getVoiceText(), Types.VARCHAR));
            up1.addUpdateValue("VOICETIMES", new DataValue(request.getVoiceTimes() ,Types.VARCHAR));
            up1.addUpdateValue("ROLLTIME", new DataValue(request.getRollTime() ,Types.VARCHAR));
            up1.addUpdateValue("SHOWWAIMAI", new DataValue(request.getShowWaimai(),Types.VARCHAR));
            up1.addUpdateValue("SERVICEURL", new DataValue(request.getServiceUrl(),Types.VARCHAR));
            up1.addUpdateValue("UPDATEURL", new DataValue(request.getUpdateUrl(),Types.VARCHAR));
            up1.addUpdateValue("COLOURID", new DataValue(request.getColourId(),Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(LASTMODITIME,Types.DATE));
            up1.addUpdateValue("LASTMODIOPID", new DataValue(opNO,Types.VARCHAR));

            up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            up1.addCondition("TEMPLATEID", new DataValue(baseSetNo, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(up1));
            /**
             * DCP_CDSSET_SHOP
             */
            DelBean db1 = new DelBean("DCP_CDSSET_SHOP");
            db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            db1.addCondition("TEMPLATEID", new DataValue(baseSetNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            List<DCP_CdsSetUpdateReq.level3Elm> rangeList = request.getRangeList();
            if(!CollectionUtils.isEmpty(rangeList)){
                String[] columns_CDSSET_SHOP ={"EID","TEMPLATEID","SHOPID" };
                for (DCP_CdsSetUpdateReq.level3Elm level3Elm : rangeList) {
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

            /**
             * DCP_CDSSET_ADVERT
             */
            DelBean db2 = new DelBean("DCP_CDSSET_ADVERT");
            db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
            db2.addCondition("TEMPLATEID", new DataValue(baseSetNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            List<DCP_CdsSetUpdateReq.level2Elm> fileList = request.getFileList();
            if(!CollectionUtils.isEmpty(fileList)){
                String[] columns_CDSSET_ADVERT ={"EID","TEMPLATEID","SORTID","FILENAME","LASTMODITIME","LASTMODIOPID" };
                for (DCP_CdsSetUpdateReq.level2Elm level2Elm : fileList) {
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
    protected List<InsBean> prepareInsertData(DCP_CdsSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CdsSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CdsSetUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CdsSetUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsSetUpdateReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (Check.Null(request.getBaseSetNo())) {
            errMsg.append("模板编号不能为空,");
            isFail = true;
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
    protected TypeToken<DCP_CdsSetUpdateReq> getRequestType() {
        return new TypeToken<DCP_CdsSetUpdateReq>(){};
    }

    @Override
    protected DCP_CdsSetUpdateRes getResponseType() {
        return new DCP_CdsSetUpdateRes();
    }
}
