package SatelliteRelayServer.RelayService.Components;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import SatelliteRelayServer.SatelliteRelayDBManager;


public class TargetFTP {
	static Logger logger = Logger.getLogger(TargetFTP.class);
	private FTPImpl ftp;
	
	public boolean init(SatelliteRelayDBManager serviceDB) {
		if (serviceDB.getIsSFTP() == true) {
			ftp = new FTPImpl(serviceDB.getFTPServer(), serviceDB.getFTPPort(), serviceDB.getIsSFTP());
			if (ftp.connect() && ftp.login(serviceDB.getFTPID(), serviceDB.getFTPPassword())) { 
				return true;
			}
		} else {
			ftp = new FTPImpl(serviceDB.getFTPServer(), serviceDB.getFTPPort(), serviceDB.getIsSFTP());
			if (ftp.connect() && ftp.login(serviceDB.getFTPID(), serviceDB.getFTPPassword())) { 
				return true;
			}
		}
		return false;
	}

	public boolean sendFile(File afile, String ftpTargetPath) {
		try {
			ftp.put(afile, ftpTargetPath);
		} catch (Exception e) {
			logger.error("FTP SendFile Faile : " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void close() throws IOException {
		if (ftp != null)
			ftp.close();
	}
}
