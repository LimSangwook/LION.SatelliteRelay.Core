package LionPlus.SatelliteRelayServer;


import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.PropertyConfigurator;

import SatelliteRelayServer.RelayService.Components.FTPImpl;

public class FTPPassiveTest {
	public static void main(String[] args) {
		PropertyConfigurator.configure("./conf/log4j.properties");
		
		FTPImpl ftp = new FTPImpl("211.202.204.33",21, false);
		ftp.login("iswook", "u2pia79");
		FTPFile[] list = ftp.list();
		for (FTPFile ftpFile : list) {
			System.out.println(ftpFile.toString());
		}
	}
}

