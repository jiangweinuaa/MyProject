package com.dsc.spos.service.imp.json;

import java.awt.geom.Point2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.json.cust.req.DCP_ShopListQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ShopListQuery_OpenRes;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import javax.persistence.criteria.Order;

/**
 * 门店列表查询
 *
 * @author Huawei
 */
public class DCP_ShopListQuery_Open extends SPosBasicService<DCP_ShopListQuery_OpenReq, DCP_ShopListQuery_OpenRes> {

    @Override
    protected boolean isVerifyFail(DCP_ShopListQuery_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopListQuery_OpenReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_ShopListQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_ShopListQuery_OpenRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_ShopListQuery_OpenRes();
    }

    @Override
    protected DCP_ShopListQuery_OpenRes processJson(DCP_ShopListQuery_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_ShopListQuery_OpenRes res = null;
        res = this.getResponse();
        int totalRecords = 0;                                //总笔数
        int totalPages = 0;
        String sql = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        SimpleDateFormat hmm = new SimpleDateFormat("HHmm");
        SimpleDateFormat hms2 = new SimpleDateFormat("HHmmss");
        SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss");
        String eId = req.getRequest().geteId(); //SA所开规格中并没企业编码，个人觉得还是需要的，兼容吧
//		ApiUser apiUser = req.getApiUser();
//		String companyId = "";
//		if(apiUser!=null){
//			companyId =	req.getApiUser().getCompanyId();
//		}
        if (Check.Null(eId)) {
            eId = req.geteId();
        }

        String channelId = req.getApiUser().getChannelId(); // 适用渠道

        try {
            //单头查询
            sql = this.getQuerySql(req);
             List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

            String timeSql = "";
            StringBuffer timeBuf = new StringBuffer();
            timeBuf.append(""
                    + " SELECT a.EID , a.baseSetNo , a.RESTRICTSHOP , b.SHOPID , b.shopName, c.item  ,   "
                    + " c.starttime , c.endTime ,a.LASTMODITIME"
                    + " FROM Dcp_Takeout_Baseset a "
                    + " LEFT JOIN Dcp_Takeout_Baseset_Detail b ON a.EID = b.EID AND a.baseSetNo = b.baseSetNo "
                    + " LEFT JOIN dcp_takeout_baseset_ordertime c ON a.EID = c.EID AND a.baseSetNO = c.basesetno "
                    + " WHERE 1= 1 and a.STATUS = '100' and a.eid = '" + eId + "'");
            timeBuf.append(" ORDER BY RESTRICTSHOP  , EID , SHOPID , item ,a.LASTMODITIME");
            timeSql = timeBuf.toString();

            List<Map<String, Object>> timeDatas = this.doQueryData(timeSql, null);

            // 计算提前选菜 查询相关门店
            StringBuffer restrictBuf = new StringBuffer("");
            // 查询适用渠道
            restrictBuf.append(" SELECT a.EID, a.RULENO, a.RESTRICTCHANNEL, b.ID " +
                    " FROM DCP_SCANORDER_BASESET a " +
                    "LEFT JOIN DCP_SCANORDER_BASESET_RANGE b ON a.EID = b.EID AND a.RULENO = b.RULENO and b.RANGETYPE = '3'" +
                    " WHERE a.STATUS = '100' AND a.EID = '" + eId + "'  ORDER BY decode(a.RESTRICTCHANNEL, 1,4), a.RESTRICTCHANNEL desc,decode(a.RESTRICTSHOP, 1,4), a.RESTRICTSHOP desc");
            List<Map<String, Object>> restrictChannelDatas = this.doQueryData(restrictBuf.toString(), null);
            restrictBuf.setLength(0);
            restrictBuf.append(" SELECT a.EID, a.RULENO, a.RESTRICTSHOP, b.ID, a.RESTRICTADVANCEORDER,a.SCANTYPE,a.LASTMODITIME,a.RESTRICTTABLE " +
                    " FROM DCP_SCANORDER_BASESET a " +
                    "LEFT JOIN DCP_SCANORDER_BASESET_RANGE b ON a.EID = b.EID AND a.RULENO = b.RULENO and b.RANGETYPE = '2'" +
                    " WHERE a.STATUS = '100' AND a.EID = '" + eId + "'  ORDER BY decode(a.RESTRICTSHOP, 1,4), a.RESTRICTSHOP desc");
            List<Map<String, Object>> restrictShopDatas = this.doQueryData(restrictBuf.toString(), null);


            res.setDatas(new ArrayList<DCP_ShopListQuery_OpenRes.level1Elm>());


            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                Map<String, Object> oneData_Count = getQDataDetail.get(0);

                String num = oneData_Count.get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / Integer.parseInt(req.getRequest().getPageSize());
                totalPages = (totalRecords % Integer.parseInt(req.getRequest().getPageSize()) > 0) ? totalPages + 1 : totalPages;

                //
                String ISHTTPS = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";

                String DomainName = PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_ShopListQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    DCP_ShopListQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
                    String shopId = oneData.get("SHOPID").toString();
                    String edEId = oneData.get("EID").toString();
                    if (Check.Null(shopId)) {
                        continue;
                    }

                    lv1.setShopId(shopId);
                    lv1.setShopName(oneData.get("SHOPNAME").toString());
                    lv1.setProvince(oneData.get("PROVINCE").toString());
                    lv1.setCity(oneData.get("CITY").toString());
                    lv1.setArea(oneData.get("AREA").toString());
                    lv1.setAddress(oneData.get("ADDRESS").toString());
                    lv1.setLongitude(oneData.get("LONGITUDE").toString());
                    lv1.setLatitude(oneData.get("LATITUDE").toString());
                    lv1.setPhone(oneData.get("PHONE").toString());

                    lv1.setGrade(oneData.getOrDefault("GRADE", "0").toString());
                    lv1.setDistance(oneData.getOrDefault("DISTANCE", "").toString());


                    String fileName = oneData.get("FILENAME").toString();
                    if (!Check.Null(fileName)) {
                        //
                        if (DomainName.endsWith("/")) {
                            lv1.setFileName(httpStr + DomainName + "resource/image/" + fileName);
                        } else {
                            lv1.setFileName(httpStr + DomainName + "/resource/image/" + fileName);
                        }
                    } else {
                        lv1.setFileName(fileName);
                    }

                    String shopBeginTime = oneData.get("SHOPBEGINTIME").toString();
                    // 针对库中值 会出现NANANA 做下兼容 如果时间转化失败 则给个默认值
                    try {
                        if (!Check.Null(shopBeginTime)) {
                            if (shopBeginTime.length() == 6) {
                                shopBeginTime = hm.format(hms2.parse(shopBeginTime));
                            } else {
                                shopBeginTime = hm.format(hms.parse(shopBeginTime));
                            }
                        }
                    } catch (Exception e) {
                        shopBeginTime = "";
                    }


                    String shopEndTime = oneData.get("SHOPENDTIME").toString();
                    try {
                        if (!Check.Null(shopEndTime)) {
                            if (shopEndTime.length() == 6) {
                                shopEndTime = hm.format(hms2.parse(shopEndTime));
                            } else {
                                shopEndTime = hm.format(hms.parse(shopEndTime));
                            }

                        }
                    } catch (ParseException e) {
                        shopEndTime = "";
                    }
                    if (!Check.Null(shopBeginTime) && !Check.Null(shopEndTime)) {
                        lv1.setOpenHours(shopBeginTime + " - " + shopEndTime);
                    } else {
                        lv1.setOpenHours("");
                    }

                    String isSelfPick = oneData.get("ISSELFPICK").toString();
                    lv3.setIsSelfPick(!Check.Null(isSelfPick) == true ? isSelfPick : "N");
                    String selfbegintime = oneData.get("SELFBEGINTIME").toString();
                    String selfendtime = oneData.get("SELFENDTIME").toString();

//					if(!Check.Null(selfbegintime)){
//						selfbegintime = hms.format(( hms.parse(selfbegintime)));
//					}
//					if(!Check.Null(selfendtime)){
//						selfendtime = hms.format(( hms.parse(selfendtime)));
//					}
                    lv3.setSelfBeginTime(selfbegintime);
                    lv3.setSelfEndTime(selfendtime);
                    String iscitydelivery = oneData.get("ISCITYDELIVERY").toString();
                    lv3.setIsCityDelivery(!Check.Null(iscitydelivery) == true ? iscitydelivery : "N");


//					lv1.setOrderTimes(new ArrayList<DCP_ShopListQuery_OpenRes.level2Elm>());
                    lv3.setOrderTimes(new ArrayList<DCP_ShopListQuery_OpenRes.level2Elm>());
                    if (timeDatas != null && !timeDatas.isEmpty()) {
                        ////判断是否存在适用于全部门店的配置信息， 若存在，则所有的门店都采用该模板上的 接单时间
//						boolean isAll = false;
//						for (Map<String, Object> map : timeDatas) {
//							if(map.get("RESTRICTSHOP").toString().equals("0")){
//								isAll = true;
//							}
//						}
                        List<DCP_ShopListQuery_OpenRes.level2Elm> restrictshop1 = new ArrayList<>();
                        List<DCP_ShopListQuery_OpenRes.level2Elm> restrictshop0 = new ArrayList<>();
                        List<DCP_ShopListQuery_OpenRes.level2Elm> restrictshop2 = new ArrayList<>();
                        for (Map<String, Object> map : timeDatas) {
                            DCP_ShopListQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            String timeEId = map.get("EID").toString();
                            String timeShopNo = map.get("SHOPID").toString();
                            String item = map.get("ITEM").toString();
                            String startTime = map.get("STARTTIME").toString();
                            String endTime = map.get("ENDTIME").toString();
                            String restrictshop = map.get("RESTRICTSHOP").toString();
                            if (timeEId.equals(edEId) && timeShopNo.equals(shopId) && restrictshop.equals("1")) {
                                lv2.setItem(item);
                                lv2.setStartTime(startTime);
                                lv2.setEndTime(endTime);
                                restrictshop1.add(lv2);
                            } else if (timeEId.equals(edEId) && !timeShopNo.equals(shopId) && restrictshop.equals("2")) {
                                lv2.setItem(item);
                                lv2.setStartTime(startTime);
                                lv2.setEndTime(endTime);
                                restrictshop2.add(lv2);
                            } else if (timeEId.equals(edEId) && restrictshop.equals("0")) {
                                lv2.setItem(item);
                                lv2.setStartTime(startTime);
                                lv2.setEndTime(endTime);
                                restrictshop0.add(lv2);
                            }
//							if(isAll){ //适用于全部门店的配置信息
//								if(timeEId.equals(edEId)){ // MD,防止规格中不传EID
//									DCP_ShopListQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
//									lv2.setItem(item);
//									lv2.setStartTime(startTime);
//									lv2.setEndTime(endTime);
//									lv1.getOrderTimes().add(lv2);
//								}
//
//							}else{ //适用于指定门店的配置信息
//								if(timeEId.equals(edEId) && timeShopNo.equals(shopId)){
//									DCP_ShopListQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
//									lv2.setItem(item);
//									lv2.setStartTime(startTime);
//									lv2.setEndTime(endTime);
//									lv1.getOrderTimes().add(lv2);
//								}
//							}

                        }
                        if (!CollectionUtils.isEmpty(restrictshop1)) {
                            lv3.setOrderTimes(restrictshop1);
                        } else if (!CollectionUtils.isEmpty(restrictshop2)) {
                            lv3.setOrderTimes(restrictshop2);
                        }
                        if (!CollectionUtils.isEmpty(restrictshop0)) {
                            lv3.setOrderTimes(restrictshop0);
                        }
                    }

                    res.getDatas().add(lv1);

                    String longitude = req.getRequest().getLongitude();// 经度
                    String latitude = req.getRequest().getLatitude();// 维度

                    // **************************** 计算当前地址是否支持配送 Begin **********************
                    // 处理逻辑：1)根据所传经纬度和对应门店的配送地址及范围判断；2）根据配送接单时间判断，2者都支持，返回Y，否则返回N；3)根据门店地址
                    String isAddressDelivery = "N"; // 支持当前地址配送(Y/N)
                    if (!Check.Null(longitude) && !Check.Null(latitude)) {
                        Boolean isDelivery = false;
                        String range_type = oneData.get("RANGE_TYPE").toString(); // 0：半径 1：手绘
                        String range = oneData.get("RANGE").toString(); // 范围 OR 坐标组
                        if (range_type.equals("0") && !Check.Null(range)) {
//							// 半径
//							String slongitude = oneData.get("LONGITUDE").toString();
//							String slatitude = oneData.get("LATITUDE").toString();
//							String address = oneData.get("ADDRESS").toString();
//							// 经纬度为空的时候，根据门店地址查询对应门店的经纬度
//							if (Check.Null(slongitude) || Check.Null(slatitude)) {
//								MyCommon mc = new MyCommon();
//								String point = mc.getLngLat(address);
//								if (!Check.Null(point)) {
//									slongitude = point.substring(0, point.indexOf(","));
//									// 截取逗号后的字符串
//									slatitude = point.substring(slongitude.length() + 1, point.length());
//								}
//								mc = null;
//							}
//							// 如果用户的经纬度和门店的经纬度都存在,则计算距离
//							if(!Check.Null(latitude)&&!Check.Null(longitude)&&!Check.Null(slatitude)&&!Check.Null(slongitude)){
//								double distance = OrderUtil.getDistance(Double.parseDouble(latitude), Double.parseDouble(longitude), Double.parseDouble(slatitude), Double.parseDouble(slongitude));
//								if(distance<=Double.parseDouble(range)){
//									isDelivery = true;
//								}
//							}
                            String distance = oneData.get("DISTANCE").toString();
                            if (!Check.Null(distance)) {
                                if (Double.parseDouble(distance) <= Double.parseDouble(range)) {
                                    isDelivery = true;
                                }
                            }
                        } else if (range_type.equals("1") && !Check.Null(range)) {
                            //  手绘
                            Point2D.Double point = new Point2D.Double(Double.parseDouble(longitude), Double.parseDouble(latitude));
                            List<Point2D.Double> pts = new ArrayList<Point2D.Double>();
                            String[] split = range.split("\\|");
                            for (String s : split) {
                                String[] split1 = s.split(",");
                                pts.add(new Point2D.Double(Double.parseDouble(split1[0]), Double.parseDouble(split1[1])));
                            }
                            if (OrderUtil.IsPtInPoly(point, pts)) {
                                isDelivery = true;
                            }
                        }

                        // 如果配送距离满足，继续判断是否满足当前配送接单的时间
                        if (isDelivery) {
                            isAddressDelivery = "Y";
                            //如果配送距离满足，不需要满足当前配送接单时间了
                            /*Calendar calendar = Calendar.getInstance();
                            String now = hms.format(calendar.getTime()); // 获取到当前时间 格式 HH:mm:ss
                            Date nowTime = hms.parse(now);
                            List<DCP_ShopListQuery_OpenRes.level2Elm> orderTimes = lv3.getOrderTimes();
                            for (DCP_ShopListQuery_OpenRes.level2Elm orderTime : orderTimes) {
                                Date startTime = hm.parse(orderTime.getStartTime().toString()); // 接单开始时间
                                Date endTime = hm.parse(orderTime.getEndTime().toString()); // 接单结束时间
                                if (isEffectiveDate(nowTime, startTime, endTime)) {
                                    isAddressDelivery = "Y";
                                }
                            }*/
                        }

                        // **************************** 计算当前地址是否支持配送 End**********************
                    }

                    lv3.setIsAddressDelivery(isAddressDelivery);
                    lv1.setDelivery(lv3);

                    // 是否开启提前选菜
                    String restrict = "";// 预点餐
                    String scanType = "";// 点餐模式
                    String restrictTable = "";// 点餐模式
                    Map<String, Object> parms = null;
                    /**
                     * 1.筛选适用渠道
                     * 2.筛选适用门店
                     */
                    for (Map<String, Object> restrictChannelData : restrictChannelDatas) {
                        String restrictRuleNo = restrictChannelData.get("RULENO").toString();
                        String restrictChannel = restrictChannelData.get("RESTRICTCHANNEL").toString();
                        String restrictChannelId = restrictChannelData.get("ID").toString();

                        if (restrictChannel.equals("1") && restrictChannelId.equals(channelId)) {
                             parms = getParms(restrictRuleNo, shopId, restrictShopDatas);
                             if(!Check.Null(parms.get("isFlag").toString())){
                                 break;
                             }
                        } else if (restrictChannel.equals("2")) {
                            if (restrictChannelId.equals(channelId)) {
                                continue;
                            } else if (!restrictChannelId.equals(channelId)) {
                                parms = getParms(restrictRuleNo, shopId, restrictShopDatas);
                                if(!Check.Null(parms.get("isFlag").toString())){
                                    break;
                                }
                            }
                        }else if(restrictChannel.equals("0")){
                            parms = getParms(restrictRuleNo, shopId, restrictShopDatas);
                            if(!Check.Null(parms.get("isFlag").toString())){
                                break;
                            }
                        }
                    }

                    if(null!=parms){
                        restrict = parms.get("restrict").toString();
                        scanType = parms.get("scanType").toString();
                        restrictTable = parms.get("restrictTable").toString();
                        lv1.setRestrictAdvanceOrder(restrict);
                        lv1.setScanType(scanType);
                        lv1.setRestrictTable(restrictTable);
                    }

                }

            }

            res.setPageNumber(Integer.parseInt(req.getRequest().getPageNumber()));
            res.setPageSize(Integer.parseInt(req.getRequest().getPageSize()));
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO: handle exception
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败！！");
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    protected String getQuerySql(DCP_ShopListQuery_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = "";
        //分页处理
        int pageNumber = Integer.parseInt(req.getRequest().getPageNumber());
        int pageSize = Integer.parseInt(req.getRequest().getPageSize());
        int startRow = (pageNumber - 1) * pageSize;

        String longitude = req.getRequest().getLongitude();//经度
        String latitude = req.getRequest().getLatitude();//纬度
        String keyTxt = req.getRequest().getKeyTxt(); // 门店名称/地址模糊查询
        String shopId = req.getRequest().getShopId(); // 门店编号
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer();

        String city = req.getRequest().getCity();
        String eId = req.getRequest().geteId(); //SA所开规格中并没企业编码，个人觉得还是需要的，兼容吧

        String companyId = req.getRequest().getCompanyId();
//		ApiUser apiUser = req.getApiUser();
//		String companyId = "";
//		if(apiUser!=null){
//			companyId =	req.getApiUser().getCompanyId();
//		}
        if (Check.Null(eId)) {
            eId = req.geteId();
        }

        sqlbuf.append(" SELECT * FROM ( "
                + " SELECT count(*) OVER() AS NUM,  row_number() over (order BY distance  ) rn, s.* FROM (   "
                + " select a.EID , a.organizationNO as SHOPID , b.org_name as shopName,  a.province, a.city  ,  a.area , a.address,  a.longitude , a.latitude ,"
                + " a.phone ,a.shopBeginTime , a.shopEndTime , a.fileName, c.ISSELFPICK , c.SELFBEGINTIME, c.SELFENDTIME, c.ISCITYDELIVERY,a.RANGE_TYPE ,a.\"RANGE\" ");

        if (Check.Null(longitude) || Check.Null(latitude)) {
            sqlbuf.append(" , null as distance ");
        }

        if (!Check.Null(longitude) && !Check.Null(latitude)) {
            sqlbuf.append(" , F_CRM_GetDistance('" + latitude + "','" + longitude + "',a.latitude,a.longitude ) as distance ");
        }

        sqlbuf.append(" FROM DCP_ORG a  "
                + " LEFT JOIN DCP_ORG_lang b ON a.EID = b.EID AND a.organizationNO = b.organizationNO and b.lang_type = '" + langType + "' " +
                " LEFT JOIN DCP_ORG_ORDERSET c ON a.EID = c.EID AND a.ORGANIZATIONNO = c.ORGANIZATIONNO"
                + " WHERE 1=1 and a.ORG_FORM = '2' and a.STATUS = '100'"
        );

        if (!Check.Null(eId)) {
            sqlbuf.append(" and a.EID = '" + eId + "' ");
        }

        if (!Check.Null(companyId)) {
            sqlbuf.append(" and a.BELFIRM = '" + companyId + "' ");
        }

        if (!Check.Null(city)) {
            sqlbuf.append(" and a.city like '%%" + city + "%%' ");
        }
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (b.ORG_NAME like'%%" + keyTxt + "%%' OR a.ADDRESS like '%%%" + keyTxt + "%')");
        }
        if (!Check.Null(shopId)) {
            sqlbuf.append(" and a.ORGANIZATIONNO  ='" + shopId + "' ");
        }


        sqlbuf.append(" ORDER BY distance ,  a.organizationNO  "
                + " )  s  )   t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow + pageSize)
                + " ORDER BY  distance, SHOPID  "
        );

        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询适用门店相关的参数
     * @param restrictRuleNo
     * @param shopId
     * @param restrictShopDatas
     * @return
     */
    protected Map<String, Object> getParms(String restrictRuleNo,String shopId,List<Map<String, Object>> restrictShopDatas){
        Map<String,Object> map = new HashMap<>();
        String restrict = "",scanType = "",restrictTable="",isFlag = "";
        for (Map<String, Object> restrictShopData : restrictShopDatas) {
            String restrictRuleNo2 = restrictShopData.get("RULENO").toString();
            if (restrictRuleNo.equals(restrictRuleNo2)) {
                String restrictshop = restrictShopData.get("RESTRICTSHOP").toString();
                String Id = restrictShopData.get("ID").toString();
                String restrictadvanceorder = restrictShopData.get("RESTRICTADVANCEORDER").toString();
                String scantype = restrictShopData.get("SCANTYPE").toString();
                String restricttable = restrictShopData.get("RESTRICTTABLE").toString();
                if (restrictshop.equals("1") && Id.equals(shopId)) {
                    restrict = restrictadvanceorder;
                    scanType = scantype;
                    restrictTable = restricttable;
                    isFlag = "1";
                    break;
                } else if (restrictshop.equals("2")) {
                    if (!Id.equals(shopId)) {
                        restrict = restrictadvanceorder;
                        scanType = scantype;
                        restrictTable = restricttable;
                        isFlag = "1";
                    } else {
                        restrict = "";
                        scanType = "";
                        restrictTable = "";
                        isFlag = "";
                        break;
                    }
                } else if (restrictshop.equals("0")) {
                    restrict = restrictadvanceorder;
                    scanType = scantype;
                    restrictTable = restricttable;
                    isFlag = "1";
                    break;
                }
            }
        }
        map.put("restrict",restrict);
        map.put("scanType",scanType);
        map.put("restrictTable",restrictTable);
        map.put("isFlag",isFlag);
        return map;
    }


}
