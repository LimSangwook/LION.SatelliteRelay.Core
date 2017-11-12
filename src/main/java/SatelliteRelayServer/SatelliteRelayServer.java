package SatelliteRelayServer;

import org.apache.log4j.Logger;

import SatelliteRelayServer.RelayService.RelayService;
import SatelliteRelayServer.RestAPIService.RestAPIService;

public class SatelliteRelayServer {
	static Logger logger = Logger.getLogger(SatelliteRelayServer.class);
	SatelliteRelayDBManager relayDBManager = new SatelliteRelayDBManager();
	RelayService scheduleService = new RelayService();
	RestAPIService webFrontService = new RestAPIService();
	public static SatelliteRelayServer Create() {
		return new SatelliteRelayServer();
	}

	public void run() throws Exception {
		logger.info("[SatelliteRelayService] START");
		
		if (relayDBManager.init() && scheduleService.init(relayDBManager) && webFrontService.init(relayDBManager, scheduleService)) {
			scheduleService.run();
			webFrontService.run();
		}
		SatelliteRelayTrayIcon tray = new SatelliteRelayTrayIcon();
		tray.run();
	}
}
