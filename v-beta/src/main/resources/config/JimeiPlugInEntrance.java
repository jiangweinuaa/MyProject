package config;

import java.util.Map;

import com.digiwin.plugin.ServicePluginEntrance;
import com.dsc.dsm.dao.DsmDAO;
import com.dsc.dsm.dao.impl.DsmDbPoolDAOImp;
import com.dsc.dsm.service.utils.DispatchService;

public class DsmPlugInEntrance implements ServicePluginEntrance {
	
	private DsmDAO dao;

	@Override
	public String startup(Map<String, String> param) {
		dao = DsmDbPoolDAOImp.getDao();
		return Boolean.TRUE + "";
	}

	@Override
	public String serviceEntrance(String input) {
		//執行 service
		DispatchService ds = DispatchService.getInstance();
		String resXml = ds.callService(input, dao);
		return resXml;
	}

	@Override
	public String shutdown(Map<String, String> param) {
		this.dao.closeDAO();
		return Boolean.TRUE + "";
	}

	@Override
	public String[] getServiceList() {
		return DispatchService.getInstance().getServiceList();
//		this.getClass().getResource(name);
//		String[] serviceList = {
//				"HolidayDataGet",
//				"ProductDataGet",
//				"ProductRankGet",
//				"PromotionPlanDataGet",
//				"PromotionTargetAmtGet",
//				"SaleRatioGet",
//				"SeriesDataGet",
//				"ShiftScheduleGet",
//				"ShiftScheduleUpdate",
//				"ShopDataGet",
//				"SizeDataGet",
//				"TargetAmtGet",
//				"TimeIntervalDataGet",
//				"TrustGet",
//				"UserAuthCheck",
//				"UserDataGet",
//				"UserDataUpdate",
//				"UserPermissionGet",
//				"UserShopGet",
//				"WorkShiftGet",
//				"WorkShiftUpdate",
//				"YearOnYearGet",	
//		};
//		return serviceList;
	}

}
