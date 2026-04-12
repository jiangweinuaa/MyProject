package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_BomDetailReq;
import com.dsc.spos.json.cust.res.DCP_BomDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_BomDetail extends SPosBasicService<DCP_BomDetailReq, DCP_BomDetailRes>{

	@Override
	protected boolean isVerifyFail(DCP_BomDetailReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (Check.Null(req.getRequest().getBomNo()))
		{
			errMsg.append("配方编号不能为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_BomDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BomDetailReq>(){};
	}

	@Override
	protected DCP_BomDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BomDetailRes();
	}

	@Override
	protected DCP_BomDetailRes processJson(DCP_BomDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		//查詢資料未加生产单位
		DCP_BomDetailRes res = this.getResponse();
		String eId = req.geteId();
		
		//查詢資料
		String sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		DCP_BomDetailRes.level1Elm lv1 = res.new level1Elm();
		if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
		{
			lv1.setBomNo(getQData.get(0).get("BOMNO").toString());
			lv1.setBomType(getQData.get(0).get("BOMTYPE").toString());
			lv1.setPluNo(getQData.get(0).get("PLUNO").toString());
			lv1.setPluName(getQData.get(0).get("PLU_NAME").toString());
			lv1.setUnit(getQData.get(0).get("UNIT").toString());
			lv1.setUnitName(getQData.get(0).get("UNAME").toString());
			lv1.setStatus(getQData.get(0).get("STATUS").toString());
			lv1.setMulQty(getQData.get(0).get("MULQTY").toString());
			lv1.setEffDate(getQData.get(0).get("EFFDATE").toString());
			lv1.setMemo(getQData.get(0).get("MEMO").toString());
			lv1.setRestrictShop(getQData.get(0).get("RESTRICTSHOP").toString());

            lv1.setBatchQty(getQData.get(0).get("BATCHQTY").toString());
            lv1.setSpec(getQData.get(0).get("SPEC").toString());
            lv1.setCategory(getQData.get(0).get("CATEGORY").toString());
            lv1.setCategoryName(getQData.get(0).get("CATEGORYNAME").toString());
            lv1.setBaseUnit(getQData.get(0).get("BASEUNIT").toString());
            lv1.setBaseUnitName(getQData.get(0).get("BASEUNITNAME").toString());
            lv1.setProdType(getQData.get(0).get("PRODTYPE").toString());
            lv1.setCreateOpId(getQData.get(0).get("CREATEOPID").toString());
            lv1.setCreateOpName(getQData.get(0).get("CREATEOPNAME").toString());
            lv1.setCreateTime(getQData.get(0).get("CREATETIME").toString());
            lv1.setCreateDeptId(getQData.get(0).get("CREATEDEPTID").toString());
            lv1.setCreateDeptName(getQData.get(0).get("CREATEDEPTNAME").toString());
            lv1.setLastModiOpId(getQData.get(0).get("LASTMODIOPID").toString());
            lv1.setLastModiOpName(getQData.get(0).get("LASTMODIOPNAME").toString());
            lv1.setLastModiTime(getQData.get(0).get("LASTMODITIME").toString());
            lv1.setVersionNum(getQData.get(0).get("VERSIONNUM").toString());
            lv1.setFixedLossQty(getQData.get(0).get("FIXEDLOSSQTY").toString());
            lv1.setIsProcessEnable(getQData.get(0).get("ISPROCESSENABLE").toString());
            lv1.setInWGroupNo(getQData.get(0).get("INWGROUPNO").toString());
            lv1.setInWGroupName(getQData.get(0).get("INWGROUPNAME").toString());
            lv1.setContainType(getQData.get(0).get("CONTAINTYPE").toString());
            lv1.setRemainType(getQData.get(0).get("REMAINTYPE").toString());

            lv1.setMinQty(getQData.get(0).get("MINQTY").toString());
            lv1.setOddValue(getQData.get(0).get("ODDVALUE").toString());
            lv1.setProductExceed(getQData.get(0).get("PRODUCTEXCEED").toString());
            lv1.setProcRate(getQData.get(0).get("PROCRATE").toString());
            lv1.setDispType(getQData.get(0).get("DISPTYPE").toString());
            lv1.setSemiwoType(getQData.get(0).get("SEMIWOTYPE").toString());
            lv1.setSemiwoDeptType(getQData.get(0).get("SEMIWODEPTTYPE").toString());
            lv1.setFixPreDays(getQData.get(0).get("FIXPREDAYS").toString());
            lv1.setSdlaborTime(getQData.get(0).get("SDLABORTIME").toString());
            lv1.setSdmachineTime(getQData.get(0).get("SDMACHINETIME").toString());
            lv1.setStandardHours(getQData.get(0).get("STANDARDHOURS").toString());

            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("MATERIAL_PLUNO", true);
			List<Map<String, Object>> getMaterial = MapDistinct.getMap(getQData, condition);
			condition.put("REPLACE_PLUNO", true);
			List<Map<String, Object>> getMaterial_replace = MapDistinct.getMap(getQData, condition);
			
			condition.clear();
			condition.put("SHOPID", true);
			condition.put("ORGANIZATIONNO", true);
			List<Map<String, Object>> getRange = MapDistinct.getMap(getQData, condition);

            //condition.clear();
            //condition.put("ORGANIZATIONNO", true);
            //List<Map<String, Object>> getRangeO = MapDistinct.getMap(getQData, condition);


            lv1.setRangeList(new ArrayList<DCP_BomDetailRes.level2Range>());
			lv1.setMaterialList(new ArrayList<DCP_BomDetailRes.level2Material>());
            lv1.setCoByList(new ArrayList<>());
			
			for (Map<String, Object> map : getMaterial)
			{
				String materialPluNo = map.get("MATERIAL_PLUNO").toString();
				if(materialPluNo==null||materialPluNo.isEmpty())
				{
					continue;
				}
				DCP_BomDetailRes.level2Material lv2 = res.new level2Material();
				lv2.setIsBuckle(map.get("ISBUCKLE").toString());
				lv2.setIsReplace(map.get("ISREPLACE").toString());
				lv2.setLossRate(map.get("LOSS_RATE").toString());
				lv2.setMaterialBDate(map.get("MATERIAL_BDATE").toString());
				lv2.setMaterialEDate(map.get("MATERIAL_EDATE").toString());
				lv2.setMaterialPluName(map.get("MATERIAL_PLUNAME").toString());
				lv2.setMaterialPluNo(materialPluNo);
				lv2.setMaterialQty(map.get("MATERIAL_QTY").toString());
				lv2.setMaterialUnit(map.get("MATERIAL_UNIT").toString());
				lv2.setMaterialUnitName(map.get("MATERIAL_UNITNAME").toString());
				lv2.setQty(map.get("QTY").toString());
				lv2.setSortId(map.get("SORTID").toString());
                lv2.setCostRate(map.get("COSTRATE").toString());

				lv2.setIsPick(map.get("ISPICK").toString());
				lv2.setIsBatch(map.get("ISBATCH").toString());
				lv2.setpWGroupNo(map.get("PWGROUPNO").toString());
				lv2.setIsMix(map.get("ISMIX").toString());
				lv2.setMixGroup(map.get("MIXGROUP").toString());
				lv2.setkWGroupNo(map.get("KWGROUPNO").toString());
                lv2.setpWGroupName(map.get("PWGROUPNAME").toString());
                lv2.setkWGroupName(map.get("KWGROUPNAME").toString());

				lv2.setReplaceList(new ArrayList<DCP_BomDetailRes.level3Replace>());
				
				for (Map<String, Object> item : getMaterial_replace)
				{
					String materialPluNo_replace = item.get("MATERIAL_PLUNO").toString();
					String replacePluNo = item.get("REPLACE_PLUNO").toString();
					if(replacePluNo==null||replacePluNo.isEmpty())
					{
						continue;
					}
					if(materialPluNo_replace.equals(materialPluNo)==false)
					{
						continue;
					}
					
					DCP_BomDetailRes.level3Replace lv3 = res.new level3Replace();
					lv3.setMaterialQty(item.get("MATERIAL_QTY_REPLACE").toString());
					lv3.setMaterialUnit(item.get("MATERIAL_UNIT_REPLACE").toString());
					lv3.setMaterialUnitName(item.get("MATERIAL_UNITNAME_REPLACE").toString());
					lv3.setPriority(item.get("PRIORITY").toString());
					lv3.setReplaceBDate(item.get("REPLACE_BDATE").toString());
					lv3.setReplaceEDate(item.get("REPLACE_EDATE").toString());
					lv3.setReplacePluName(item.get("REPLACE_PLUNAME").toString());
					lv3.setReplacePluNo(replacePluNo);
					lv3.setReplaceQty(item.get("REPLACE_QTY").toString());
					lv3.setReplaceUnit(item.get("REPLACE_UNIT").toString());
					lv3.setReplaceUnitName(item.get("REPLACE_UNITNAME").toString());
					lv2.getReplaceList().add(lv3);
					
				}
				lv1.getMaterialList().add(lv2);
				
				
				
			}
			
			//范围表
			for (Map<String, Object> mapRange : getRange)
			{
				String shopId = mapRange.get("SHOPID").toString();
				String organizationno = mapRange.get("ORGANIZATIONNO").toString();
				if(Check.Null(shopId)&&Check.Null(organizationno))
				{
					continue;
				}
				DCP_BomDetailRes.level2Range lvRange = res.new level2Range();
				lvRange.setShopId(shopId);
				lvRange.setShopName(mapRange.get("SHOPNAME").toString());
				lvRange.setOrganizationNo(organizationno);
				lvRange.setOrganizationName(mapRange.get("ORGANIZATIONNAME").toString());

				lv1.getRangeList().add(lvRange);
			}

            //农副产品
            String coSql="select a.*,b.plu_name as pluname ,c.uname  from DCP_BOM_COBYPRODUCT a" +
                    " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.unit and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='"+req.geteId()+"' and a.bomno='"+req.getRequest().getBomNo()+"' ";
            List<Map<String, Object>> coList = this.doQueryData(coSql, null);
            if(coList.size()>0) {
                for (Map<String, Object> map : coList) {

                    DCP_BomDetailRes.CoByList coByList = res.new CoByList();
                    coByList.setProductType(map.get("PRODUCTTYPE").toString());
                    coByList.setPluNo(map.get("PLUNO").toString());
                    coByList.setPluName(map.get("PLUNAME").toString());
                    coByList.setUnit(map.get("UNIT").toString());
                    coByList.setUName(map.get("UNAME").toString());
                    coByList.setCostRate(map.get("COSTRATE").toString());
                    lv1.getCoByList().add(coByList);

                }
            }

        }

		
		res.setDatas(lv1);
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_BomDetailReq req) throws Exception {
		// TODO Auto-generated method stub
	
		String eId= req.geteId();		
		String bomNo = req.getRequest().getBomNo();
		
		String sql = null;
		
		StringBuffer sqlbuf = new StringBuffer("");
		String langType=req.getLangType();

		
		

		sqlbuf.append("SELECT * FROM ( "
				+ " select a.bomno,a.bomtype,a.pluno,a.unit,a.mulqty,to_char(a.effdate,'yyyyMMdd') as effdate,a.memo,a.status,a.restrictshop,G.PLU_NAME,u.uname, "
				+ " b.material_pluno,g2.PLU_NAME material_pluname,b.material_unit,u2.uname material_unitname,b.material_qty,b.qty,b.loss_rate,b.isbuckle,b.isreplace,b.material_bdate,b.material_edate,b.sortid, "
				+ " c.material_unit material_unit_replace,u3.uname material_unitname_replace,  c.material_qty material_qty_replace, c.replace_pluno,g3.plu_name replace_pluname, c.replace_unit,u4.uname replace_unitname, c.replace_qty,c.replace_bdate,c.replace_edate,c.priority,"
				+ " r.shopid,s.org_name shopname,r.organizationno,s1.org_name as organizationname,a.batchqty,b1.spec, b.COSTRATE,"
                + " d.category,d.category_name as categoryname ,c1.baseunit,u1.uname as baseunitname,a.prodType,a.createtime,a.lastModiTime,a.createOpId,e1.op_name as createopname,  "
                + " a.lastModiOpId,e2.op_name as lastModiOpname,a.createDeptId,d1.departname as createDeptname,a.versionnum, "
                + " a.FIXEDLOSSQTY,a.INWGROUPNO,a.ISPROCESSENABLE,"
				+ " b.isPick,b.isBatch,b.pWGroupNo,b.isMix,b.mixGroup,b.kWGroupNo,wg1.WGROUPNAME as pwgroupname,wg2.WGROUPNAME as kwgroupname ,wg3.WGROUPNAME as inwgroupname,a.REMAINTYPE,a.CONTAINTYPE ,a.minQty,a.oddValue,a.productExceed,a.procRate,a.dispType,a.semiwoType,a.semiwoDeptType,a.fixPreDays,a.sdlaborTime,a.sdmachineTime,a.standardHours  "
                + " from dcp_bom a"
				+ " left join DCP_BOM_MATERIAL b on a.eid = b.eid and a.bomno=b.bomno "
				+ " left join DCP_BOM_MATERIAL_R c on a.eid =c.eid and a.bomno = c.bomno and b.material_pluno=c.material_pluno "
				+ " left join dcp_goods_lang g on a.eid=g.eid and a.pluno=g.pluno and g.lang_type='"+langType+"' "
				+ " left join dcp_unit_lang u on a.eid = u.eid and a.unit=u.unit and u.lang_type='"+langType+"' "	
				
				+ " left join dcp_goods_lang g2 on a.eid=g2.eid and b.material_pluno=g2.pluno and g2.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang u2 on a.eid = u2.eid and b.material_unit=u2.unit and u2.lang_type='"+langType+"'"
				
				+ " left join dcp_goods_lang g3 on a.eid=g3.eid and c.replace_pluno=g3.pluno and g3.lang_type='"+langType+"'"
				+ " left join dcp_unit_lang u3 on a.eid = u3.eid and c.material_unit=u3.unit and u3.lang_type='"+langType+"'"//替代料表中子件单位
				+ " left join dcp_unit_lang u4 on a.eid = u4.eid and c.replace_unit=u4.unit and u4.lang_type='"+langType+"'"
				
				+ " left join dcp_bom_range  r on a.eid = r.eid and a.bomno = r.bomno "
				+ " left join dcp_org_lang  s on a.eid =s.eid and r.shopid = s.organizationno and s.lang_type='"+langType+"'"

                + " left join dcp_org_lang  s1 on a.eid =s1.eid and r.organizationno = s1.organizationno and s1.lang_type='"+langType+"'"

                + " left join DCP_GOODS_UNIT_LANG b1 on b1.eid=a.eid and b1.pluno=a.pluno and b1.ounit=a.unit and b1.lang_type='"+langType+"' "
                + " left join dcp_goods c1 on c1.eid=a.eid and c1.pluno=a.pluno "
                + " left join DCP_CATEGORY_LANG d on d.eid=a.eid and d.category=c1.category and d.lang_type='"+langType+"' "
                + " left join dcp_unit_lang u1 on a.eid = u1.eid and c1.baseunit=u1.unit and u1.lang_type='"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+langType+"'"
                + " left join DCP_DEPARTMENT_LANG d1 on d1.eid=a.eid and d1.departno=a.createDeptId and d1.lang_type='"+langType+"' "
                + " left join MES_WAREHOUSE_GROUP wg1 on wg1.eid=a.eid and wg1.wgroupno=b.pWGroupNo"
                + " left join MES_WAREHOUSE_GROUP wg2 on wg2.eid=a.eid and wg2.wgroupno=b.kWGroupNo"
                + " left join MES_WAREHOUSE_GROUP wg3 on wg3.eid=a.eid and wg3.wgroupno=a.INWGROUPNO"

        );
		
		
		sqlbuf.append(" where a.eid='"+eId+"' and a.bomno='"+bomNo+"'");			
		sqlbuf.append(" )" );
		
		
		sql = sqlbuf.toString();
		return sql;
	}

}
