package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_DinnerQueryReq;
import com.dsc.spos.json.cust.res.DCP_DinnerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

public class DCP_DinnerQuery extends SPosBasicService<DCP_DinnerQueryReq,DCP_DinnerQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_DinnerQueryReq req) throws Exception 
	{
		return false;
	}

	@Override
	protected TypeToken<DCP_DinnerQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_DinnerQueryReq>(){};
	}

	@Override
	protected DCP_DinnerQueryRes getResponseType() 
	{
		return new DCP_DinnerQueryRes();
	}

	@Override
	protected DCP_DinnerQueryRes processJson(DCP_DinnerQueryReq req) throws Exception
	{
		String sql = null;		

		//查詢資料
		DCP_DinnerQueryRes res = null;
		res = this.getResponse();
        boolean bshopId = false; // ShopList 传值时才返回 shopID
        if(!CollectionUtils.isEmpty(req.getRequest().getShopList())){
            bshopId = true;
        }
		//单头总数
		sql = this.getQuerySql(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_DinnerQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{

			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			

			String codeSql = "SELECT * FROM ( "
					+ " SELECT  '1' AS ttype , ruleNo , ruleName , qrCode , lastmoditime , restrictShop  "
					+ " FROM dcp_scanorder_baseset  a "
					+ " WHERE eid = '"+req.geteId()+"'  AND restrictShop = '0' AND a.status = '100' "
					+ " UNION   "
					+ " SELECT '2' AS ttype , a.ruleNo , a.ruleName , a.qrCode  , a.lastmoditime  , a.restrictShop "
					+ " FROM   dcp_scanorder_baseset  a "
					+ " INNER JOIN DCP_SCANORDER_BASESET_RANGE b ON a.eid = b.eid AND a.ruleNo = b.ruleNO "
					+ " AND a.eId = '"+req.geteId()+"'  AND a.status = '100'  "
					
					+ " AND (  ( A.restrictShop = '1'   AND B.ID = '"+req.getShopId()+"' ) OR ( a.restrictshop = '2' AND  b.Id != '"+req.getShopId()+"' ) )  "
					+ " ) T  ORDER BY ttype desc , lastmoditime DESC , ruleNo , restrictShop ";
			
			String dinnerQRCodeUrl = "";
			List<Map<String, Object>> codeDatas = this.doQueryData(codeSql, null);

			if(codeDatas != null && codeDatas.isEmpty() == false){
				dinnerQRCodeUrl = codeDatas.get(0).get("QRCODE").toString();
			}

			//取出门店号+桌台号
            StringBuffer sJoinDinnerno=new StringBuffer("");
            StringBuffer sJoinShop=new StringBuffer("");
            StringBuffer sJoinEid=new StringBuffer("");
            for (Map<String, Object> oneData : getQData)
            {
                sJoinDinnerno.append(oneData.get("DINNERNO").toString()+",");
                sJoinShop.append(oneData.get("ORGANIZATIONNO").toString()+",");
                sJoinEid.append(req.geteId()+",");
            }
            //
            Map<String, String> mapDinner=new HashMap<String, String>();
            mapDinner.put("EID", sJoinEid.toString());
            mapDinner.put("ORGANIZATIONNO", sJoinShop.toString());
            mapDinner.put("DINNERNO", sJoinDinnerno.toString());
            //
            MyCommon cm=new MyCommon();
            String withasSql_Dinnerno=cm.getFormatSourceMultiColWith(mapDinner);
            mapDinner=null;

            List<Map<String, Object>> mealsData=null;
            if (!Check.Null(withasSql_Dinnerno))
            {
                StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                                                             + withasSql_Dinnerno + " ) "
                                                             + "select b.*, c.plu_name,d.uname ");
                sqlbuf.append("from a "
                                      + "inner join DCP_DINNERTABLE_MEAL b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.DINNERNO=b.DINNERNO "
                                      + "left join dcp_goods_lang c on b.eid=c.eid and b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' "
                                      + "left join dcp_unit_lang d on b.eid=d.eid and b.unitid=d.unit and d.lang_type ='"+req.getLangType()+"' "
                                      + "where b.eid='"+req.geteId()+"' ");

                mealsData = this.doQueryData(sqlbuf.toString(), null);
            }


            for (Map<String, Object> oneData : getQData)
			{

				DCP_DinnerQueryRes.level1Elm oneLv1 = new DCP_DinnerQueryRes().new level1Elm();
				oneLv1.setDinnerNo(oneData.get("DINNERNO").toString());
				oneLv1.setGuestNum(oneData.get("GUESTNUM").toString());
				oneLv1.setPriority(oneData.get("PRIORITY").toString());
				oneLv1.setTeaPluNo(oneData.get("TEAPLUNO").toString());
				oneLv1.setTeaPluName(oneData.get("TEAPLUNAME").toString());
				oneLv1.setDinnerGroup(oneData.get("DINNERGROUP").toString());				
				oneLv1.setDinnerGroupName(oneData.get("DINNERGROUPNAME").toString());				
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setUpdateTime(oneData.get("UPDATE_TIME").toString());
				oneLv1.setDinnerClass(oneData.get("DINNERCLASS").toString());
				oneLv1.setDinnerQRCodeUrl(dinnerQRCodeUrl);

				oneLv1.setTeaQty(oneData.get("TEAQTY").toString());
				oneLv1.setTissuePluName(oneData.get("TISSUEPLUNAME").toString());
				oneLv1.setTissuePluNo(oneData.get("TISSUEPLUNO").toString());
				oneLv1.setTissueQty(oneData.get("TISSUEQTY").toString());
				oneLv1.setRicePluNo(oneData.get("RICEPLUNO").toString());
				oneLv1.setRicePluName(oneData.get("RICEPLUNAME").toString());
				oneLv1.setRiceQty(oneData.get("RICEQTY").toString());

				oneLv1.setUseType(oneData.get("USETYPE").toString());

                int maxGuestNum=0;
				if (PosPub.isNumericType(oneData.get("MAXGUESTNUM").toString()))
				{
                    maxGuestNum=Integer.parseInt(oneData.get("MAXGUESTNUM").toString());
				}
                oneLv1.setMaxGuestNum(maxGuestNum);

                if(bshopId){
                    oneLv1.setShopId(oneData.get("ORGANIZATIONNO").toString());
                    oneLv1.setShopName(oneData.get("ORG_NAME").toString());
                }

                //默认菜品
                oneLv1.setMealList(new ArrayList<>());
                if (mealsData != null)
                {
                    //过滤当前门店+桌台的
                    List<Map<String, Object>> tempMeals=mealsData.stream().filter(p->p.get("ORGANIZATIONNO").toString().equals(oneData.get("ORGANIZATIONNO").toString()) && p.get("DINNERNO").toString().equals(oneData.get("DINNERNO").toString())).collect(Collectors.toList());
                    if (tempMeals != null && tempMeals.size()>0)
                    {
                        for (Map<String, Object> tempMeal : tempMeals)
                        {
                            DCP_DinnerQueryRes.levelElmMeal meal=res.new levelElmMeal();
                            meal.setDefMode(tempMeal.get("DEFMODE").toString());
                            meal.setPluName(tempMeal.get("PLU_NAME").toString());
                            meal.setPluNo(tempMeal.get("PLUNO").toString());
                            meal.setUnitId(tempMeal.get("UNITID").toString());
                            meal.setUnitName(tempMeal.get("UNAME").toString());
                            meal.setQty(tempMeal.get("QTY").toString());
                            oneLv1.getMealList().add(meal);
                        }
                    }
                }

				res.getDatas().add(oneLv1);
			}

		}
		else
		{
			totalRecords = 0;
			totalPages = 0;			
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_DinnerQueryReq req) throws Exception
	{
		String sql=null;

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		String langType=req.getLangType();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

        List<String> shopList = req.getRequest().getShopList();

        String status=req.getRequest().getStatus();
		String keyTxt=req.getRequest().getKeyTxt();
		String dinnerGroup = req.getRequest().getDinnerGroup();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ " select count(*) over() num,A.ORGANIZATIONNO,A.DINNERNO,A.GUESTNUM,A.PRIORITY,A.DINNERGROUP,B.DINNERGROUPNAME,A.DINNERCLASS,A.STATUS,"
				+ " A.UPDATE_TIME,row_number() over (order by A.PRIORITY) rn,"
				+ " A.TEAPLUNO,gl.plu_name as TEAPLUNAME, a.teaQty ,"
				+ " a.tissuepluno, d.plu_name as tissuepluname, a.tissueqty ,"
				+ " a.Ricepluno, e.plu_name as ricepluname, a.riceqty , a.useType,a.MAXGUESTNUM,g.ORG_NAME "
				+ " from  DCP_DINNERTABLE A "
				+ " INNER JOIN DCP_DINNER_AREA B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.DINNERGROUP=B.DINNERGROUP "
				+ " LEFT JOIN DCP_GOODS C ON A.EID=C.EID AND A.TEAPLUNO=C.PLUNO "
				+ " left join dcp_goods_lang gl  on gl.eid=c.eid   and gl.pluno=c.pluno and gl.lang_type='"+langType+"'"
				+ " left join dcp_goods_lang d   on a.EID = d.EID  and a.tissuepluno = d.pluno  and d.lang_type='"+langType+"' "
				+ " left join dcp_goods_lang e   on a.EID = e.EID  and a.Ricepluno = e.pluno  and e.lang_type='"+langType+"' " +
                " left join DCP_ORG_LANG g on a.EID = g.EID and A.ORGANIZATIONNO = g.ORGANIZATIONNO and g.STATUS = '100'and g.LANG_TYPE = '"+langType+"'"
				
				
				
				+ " where A.EID='"+req.geteId()+"'  ");

        if(CollectionUtils.isEmpty(shopList)){

            sqlbuf.append("AND A.ORGANIZATIONNO='"+req.getShopId()+"'");
        }
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and A.STATUS='" + status + "' ");
		}

		if(dinnerGroup!=null && dinnerGroup.length()>0)
		{
			sqlbuf.append(" and A.dinnerGroup='" + dinnerGroup + "' ");
		}

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (A.DINNERNO LIKE '%%"+ keyTxt +"%%' OR A.DINNERGROUP LIKE '%%"+ keyTxt +"%%' OR B.DINNERGROUPNAME LIKE '%%"+ keyTxt +"%%' ) ");
		}

        if(!CollectionUtils.isEmpty(shopList)){
            sqlbuf.append("AND A.ORGANIZATIONNO IN (");
            shopList.forEach(shop->{
                sqlbuf.append("'"+shop+"',");
            });
            sqlbuf.deleteCharAt(sqlbuf.length() - 1);
            sqlbuf.append(")");
        }

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize) + " order  by  ORGANIZATIONNO,dinnerGroup ,priority ");

		sql=sqlbuf.toString();
		return sql;
	}

}
