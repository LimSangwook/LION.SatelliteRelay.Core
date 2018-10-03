package LionPlus.nifs.p1;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.SatelliteRelayServer;

/**
 * Hello world!
 *
 */
public class SatelliteRelayServerApp 
{
	static Logger logger = Logger.getLogger(SatelliteRelayDBManager.class);
    public static void main( String[] args )
    {
    		PropertyConfigurator.configure("./conf/log4j.properties");

        	System.out.println("Satellite Relay Server start now (Ver. 2018.10.03)");
    		logger.info("[Application] START");
		SatelliteRelayServer service = SatelliteRelayServer.Create();
		try {
			service.run();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
    }
}
