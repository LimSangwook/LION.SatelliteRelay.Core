package SatelliteRelayServer.RelayService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.DB.SatelliteDTO;
import SatelliteRelayServer.Models.ProductInfo;
import SatelliteRelayServer.RelayService.Components.TargetDB;
import SatelliteRelayServer.RelayService.Components.TargetFTP;

public class RelayService {
	static Logger logger = Logger.getLogger(RelayService.class);

	List<ProductInfo> products = null;
	TargetFTP targetFTP = new TargetFTP();
	TargetDB targetDB = new TargetDB();
	SatelliteRelayDBManager serviceDB = null; // SQLite

	private List<RelayProductService> productServiceList = new ArrayList<RelayProductService>();

	public boolean init(SatelliteRelayDBManager db) {
		logger.info("[RelayService] init");
		serviceDB = db;
		products = serviceDB.getProducts();
		return true;
	}	

	public void run() throws Exception {
		logger.info("[RelayService] run");
		
		for (ProductInfo info : products) {
			SatelliteDTO satelliteInfo = serviceDB.getSatelliteInfo(info.satelliteID);
			info.setSatelliteInfo(satelliteInfo);
			RelayProductService productService = new RelayProductService(info, serviceDB);
			productService.registSchedule();
			productServiceList.add(productService);
		}
	}
	public void stopService() {
		for (RelayProductService productService : productServiceList) {
			productService.stop();
		}
	}
}
