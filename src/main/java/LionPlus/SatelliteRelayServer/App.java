package LionPlus.SatelliteRelayServer;

import org.apache.log4j.Logger;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.SatelliteRelayServer;

/**
 * Hello world!
 *
 */
public class App 
{
	static Logger logger = Logger.getLogger(SatelliteRelayDBManager.class);
    public static void main( String[] args )
    {
        	System.out.println("Satellite Relay Server start now ");
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
