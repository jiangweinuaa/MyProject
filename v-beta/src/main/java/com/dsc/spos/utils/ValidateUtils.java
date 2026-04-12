package com.dsc.spos.utils;

import com.dsc.spos.dao.DsmDAO;

import java.util.List;
import java.util.Map;

public class ValidateUtils {

    public static boolean isEmployeeExist(DsmDAO dao,String eId, String employeeNo) throws Exception{
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select * from dcp_employee a where a.eid='" + eId + "' and a.employeeno='" + employeeNo + "'");
            List<Map<String, Object>> Datas = dao.executeQuerySQL(sb.toString(), null);

            if (Datas != null && Datas.size() > 0) {
                Datas.clear();
                Datas = null;
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    /**
     *
     * @param dao
     * @param eId
     * @param departNo
     * @param organizationNo 归属组织
     * @return
     * @throws Exception
     */
    public static boolean isDepartExist(DsmDAO dao,String eId, String departNo,String organizationNo) throws Exception{
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select * from DCP_DEPARTMENT a where a.eid='" + eId + "' and a.DEPARTNO='" + departNo + "' and a.organizationno='" + organizationNo + "'");
            List<Map<String, Object>> Datas = dao.executeQuerySQL(sb.toString(), null);
            if (Datas != null && Datas.size() > 0) {
                Datas.clear();
                Datas= null;
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    public static boolean isWarehouseExist(DsmDAO dao,String eId, String warehouse,String organizationNo) throws Exception{
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select * from DCP_WAREHOUSE a where a.eid='" + eId + "' and a.WAREHOUSE='" + warehouse + "' and a.organizationno='" + organizationNo + "' " +
                    " and a.status='100' ");
            List<Map<String, Object>> Datas = dao.executeQuerySQL(sb.toString(), null);
            if (Datas != null && Datas.size() > 0) {
                Datas.clear();
                Datas= null;
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    public static boolean isWarehouseLocationExist(DsmDAO dao,String eId, String warehouse,String location,String organizationNo) throws Exception{
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select * from DCP_location a where a.eid='" + eId + "' and a.warehouse='" + warehouse + "'" +
                    " and a.location='"+location+"' and a.organizationno='" + organizationNo + "' and a.status='100' ");
            List<Map<String, Object>> Datas = dao.executeQuerySQL(sb.toString(), null);
            if (Datas != null && Datas.size() > 0) {
                Datas.clear();
                Datas= null;
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }


}
