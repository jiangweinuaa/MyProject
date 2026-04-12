package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpCodeCreateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpCodeCreateReq.Chat;
import com.dsc.spos.json.cust.req.DCP_ISVWeComGpCodeCreateReq.Shop;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpCodeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.JoinWay;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_ISVWeComGpCodeCreate
 * 服务说明：社群活码创建
 * @author jinzma
 * @since  2024-02-28
 */
public class DCP_ISVWeComGpCodeCreate extends SPosAdvanceService<DCP_ISVWeComGpCodeCreateReq, DCP_ISVWeComGpCodeCreateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComGpCodeCreateReq req, DCP_ISVWeComGpCodeCreateRes res) throws Exception {

        try{

            String eId = req.geteId();
            String name = req.getRequest().getName();
            List<Chat> chatList = req.getRequest().getChatList();
            List<Shop> shopList = req.getRequest().getShopList();
            String[] chatArray = new String[chatList.size()];
            for (int i=0;i<chatList.size();i++){
                chatArray[i] = chatList.get(i).getChatId();
            }
            String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String gpCodeId = PosPub.getGUID(false);
            String state = gpCodeId.substring(0,30);    //截取前30位作为state传企微

            //资料检查
            {
                String sql = "select name from dcp_isvwecom_gpcode where eid='"+eId+"' and name='"+name+"'  ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isNotEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "名称已存在,无法新增");
                }
            }

            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
            JoinWay joinWay = new JoinWay();
            joinWay.setScene("2");                              //是	场景。1 - 群的小程序插件 2 - 群的二维码插件
            joinWay.setRemark(req.getRequest().getRemark());    //否	联系方式的备注信息，用于助记，超过30个字符将被截断
            joinWay.setAuto_create_room(req.getRequest().getAutoCreate()); //否	当群满了后，是否自动新建群。0-否；1-是。 默认为1
            joinWay.setRoom_base_name(req.getRequest().getBaseName());  //否	自动建群的群名前缀，当auto_create_room为1时有效。最长40个utf8字符
            joinWay.setRoom_base_id(req.getRequest().getBaseId());      //否	自动建群的群起始序号，当auto_create_room为1时有效
            joinWay.setChat_id_list(chatArray);                 //是	使用该配置的客户群ID列表，最多支持5个。
            joinWay.setState(state);                         //否	企业自定义的state参数，用于区分不同的入群渠道。不超过30个UTF-8字符

            String config_id = dcpWeComUtils.add_join_way(dao,joinWay);
            if (Check.Null(config_id)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,未获取到config_id");
            }

            String qr_code = dcpWeComUtils.get_join_way(dao,config_id);
            if (Check.Null(qr_code)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调企微接口失败,未获取到qr_code");
            }

            //新增 DCP_ISVWECOM_GPCODE
            {
                String[] columns = {"EID","GPCODEID","NAME","REMARK","AUTOCREATE","BASENAME","BASEID","LOGO","CONFIGID","GPCODEURL","CREATETIME","STATE"};
                InsBean ib = new InsBean("DCP_ISVWECOM_GPCODE", columns);
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(gpCodeId, Types.VARCHAR),
                        new DataValue(name, Types.VARCHAR),
                        new DataValue(req.getRequest().getRemark(), Types.VARCHAR),
                        new DataValue(req.getRequest().getAutoCreate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBaseName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBaseId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getLogo(), Types.VARCHAR),
                        new DataValue(config_id, Types.VARCHAR),
                        new DataValue(qr_code, Types.VARCHAR),
                        new DataValue(sDate, Types.DATE),
                        new DataValue(state, Types.VARCHAR),
                };
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
            }

            //新增 DCP_ISVWECOM_GPCODE_GPID
            {
                String[] columns = {"EID","GPCODEID","CHATID"};
                for (Chat chat : chatList) {
                    InsBean ib = new InsBean("DCP_ISVWECOM_GPCODE_GPID", columns);
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(gpCodeId, Types.VARCHAR),
                            new DataValue(chat.getChatId(), Types.VARCHAR),
                    };
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

            //新增 DCP_ISVWECOM_GPCODE_SHOP
            {
                if (CollectionUtil.isNotEmpty(shopList)) {
                    String[] columns = {"EID", "GPCODEID", "SHOPID"};
                    for (Shop shop : shopList) {
                        InsBean ib = new InsBean("DCP_ISVWECOM_GPCODE_SHOP", columns);
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(gpCodeId, Types.VARCHAR),
                                new DataValue(shop.getShopId(), Types.VARCHAR),
                        };
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                }
            }




            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComGpCodeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComGpCodeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComGpCodeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpCodeCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getName())) {
                errMsg.append("name不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getAutoCreate())) {
                errMsg.append("autoCreate不能为空,");
                isFail = true;
            }else {
                if ("1".equals(req.getRequest().getAutoCreate())){
                    if (Check.Null(req.getRequest().getBaseName())) {
                        errMsg.append("自动建群时,baseName不能为空,");
                        isFail = true;
                    }else {
                        byte[] utf8Bytes = req.getRequest().getBaseName().getBytes(StandardCharsets.UTF_8);
                        if (utf8Bytes.length > 40) {
                            errMsg.append("自动建群时,baseName不能超过40个utf8字符,");
                            isFail = true;
                        }
                    }

                    if (!PosPub.isNumeric(req.getRequest().getBaseId())) {
                        errMsg.append("自动建群时,baseId不能为空或非数字,");
                        isFail = true;
                    }
                }
            }
            if (!Check.Null(req.getRequest().getRemark())) {
                if (req.getRequest().getRemark().length()>30) {
                    errMsg.append("remark不能超过30个字符,");
                    isFail = true;
                }
            }

            List<Chat> chatList = req.getRequest().getChatList();
            if (CollectionUtil.isEmpty(chatList)) {
                errMsg.append("chatList不能为空,");
                isFail = true;
            } else {
                if (chatList.size() > 5){
                    errMsg.append("chatList不能超过5个,");
                    isFail = true;
                }else {
                    for (Chat chat : chatList) {
                        if (Check.Null(chat.getChatId())) {
                            errMsg.append("chatId不能为空,");
                            isFail = true;
                        }
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComGpCodeCreateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpCodeCreateReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpCodeCreateRes getResponseType() {
        return new DCP_ISVWeComGpCodeCreateRes();
    }
}
