package SatelliteRelayServer.RelayService.Components;

import java.time.LocalDateTime;

import SatelliteRelayServer.SatelliteRelayDBManager;

public class HistorySaver {
	private SatelliteRelayDBManager serviceDB = null;
	private int productID = -1;
	public HistorySaver(int productID, SatelliteRelayDBManager serviceDB) {
		this.serviceDB = serviceDB;
		this.productID = productID;
	}
	public void add(int historyID, String log) {
		LocalDateTime.now();
		String logString = "[" + LocalDateTime.now() + "] " + log;
		serviceDB.addHistoryLog(historyID, logString);
	}
	
	public void clean() {
	}
	public void SetFiles(int historyID, String fileNames) {
		serviceDB.setHistoryFile(historyID, fileNames);
	}
	public void setStatus(int historyID, String string) {
		serviceDB.setHistoryStatus(historyID, string);
	}

	public void setFileCount(int historyID, int count) {
		serviceDB.setHistoryFileCount(historyID, count);
	}
	public int createNewHistory() {
		return serviceDB.createHistoryLog(productID);
	}
}
