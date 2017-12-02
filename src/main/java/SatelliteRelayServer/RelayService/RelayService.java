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
		serviceDB.setStatusRunningToFail();
		for (ProductInfo info : products) {
			SatelliteDTO satelliteInfo = serviceDB.getSatelliteInfo(info.satelliteID);
			info.setSatelliteInfo(satelliteInfo);
			RelayProductService productService = new RelayProductService(info, serviceDB);
			productService.registSchedule();
			productServiceList.add(productService);
		}
	}
	
	public void removeService(int productID) {
		for (RelayProductService productService : productServiceList) {
			if (productService.getProductInfo().productID == productID) {
				productService.stop();
				productServiceList.remove(productService);
				break;
			}
		}
	}
	
	public void addService(int productID) {
		ProductInfo product = serviceDB.getProduct(productID);
		
		SatelliteDTO satelliteInfo = serviceDB.getSatelliteInfo(product.satelliteID);
		product.setSatelliteInfo(satelliteInfo);
		RelayProductService productService = new RelayProductService(product, serviceDB);
		productService.registSchedule();
		productServiceList.add(productService);
	}
	
	public void reloadService(int productID) {
		removeService(productID);
		addService(productID);
	}
}
