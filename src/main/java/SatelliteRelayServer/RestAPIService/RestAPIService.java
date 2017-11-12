package SatelliteRelayServer.RestAPIService;

import static spark.Spark.get;
import static spark.Spark.post;

import javax.servlet.MultipartConfigElement;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.RelayService.RelayService;

public class RestAPIService {
	static Logger logger = Logger.getLogger(RestAPIService.class);
	private RelayService relayService = null;
	private SatelliteRelayDBManager relayServiceDB = null;

	public boolean init(SatelliteRelayDBManager relayServiceDB, RelayService relayService) {
		this.relayService = relayService;
		this.relayServiceDB = relayServiceDB;
		return true;
	}

	public void run() {
		logger.info("WebFrontService.run()");
		
		get("/status", (req, res) -> {
			return "{\"status\":\"running\"}";
		});
		

// Satellite
		// List 가져오기
		get("/satellite/list/:page", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 15;
			int totalData = relayServiceDB.getCount("Satellite");
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("TotalData",totalData);
			jsonRoot.put("TotalPage",totalPage);
			jsonRoot.put("NowPage",nowPage);
			jsonRoot.put("ListPerPage",listPerPage);
			jsonRoot.put("Datas",relayServiceDB.getSatelliteListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		// 삭제
		get("/satellite/delete/:id", (req, res) -> {
			System.out.println("DELETE!!!!");
			int id = Integer.parseInt(req.params(":id"));
			return (relayServiceDB.delete("Satellite", id)==true)?"TRUE":"FALSE";
		});
		// 수정
		post("/satellite/update", (req, res) -> {
			System.out.println("### Update Satellite ");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.Update("Satellite", req.queryMap());
			return b==true?"Completed updating":"Failed updating";
		});
		// Create
		post("/satellite/create", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.Create("Satellite", req.queryMap());
			return b==true?"Completed creating":"Failed Creating";
		});
// Product
		get("/product/list/:page", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 15;
			int totalData = relayServiceDB.getCount("Product");
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("TotalData",totalData);
			jsonRoot.put("TotalPage",totalPage);
			jsonRoot.put("NowPage",nowPage);
			jsonRoot.put("ListPerPage",listPerPage);
			jsonRoot.put("Datas",relayServiceDB.getProductListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		// 삭제
		get("/product/delete/:id", (req, res) -> {
			int id = Integer.parseInt(req.params(":id"));
			return relayServiceDB.delete("Product", id);
		});
		// 수정
		post("/product/update", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.Update("Product", req.queryMap());
			return b==true?"Completed updating":"Failed updating";
		});
		// Create
		post("/product/create", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.Create("Product", req.queryMap());
			return b==true?"Completed creating":"Failed Creating";
		});
		
// History
		// List 가져오
		get("/history/list/:page", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 15;
			int totalData = relayServiceDB.getCount("History");
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("TotalData",totalData);
			jsonRoot.put("TotalPage",totalPage);
			jsonRoot.put("NowPage",nowPage);
			jsonRoot.put("ListPerPage",listPerPage);
			jsonRoot.put("Datas",relayServiceDB.getHistoryListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		
// ServerInfo
		//Select
		get("/serverInfo/ftp", (req, res) -> {
			String IP = relayServiceDB.getFTPServer();
			String ID = relayServiceDB.getFTPID();
			int port = relayServiceDB.getFTPPort();
			boolean isSFTP = relayServiceDB.getIsSFTP();
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("FTP_IP",IP);
			jsonRoot.put("FTP_PORT",port);
			jsonRoot.put("FTP_ID",ID);
			jsonRoot.put("FTP_SFTP",isSFTP);
			return jsonRoot.toString();
		});
		
		// Update
		post("/serverInfo/ftp", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.UpdateFTPInfo(req.queryMap());
			return b==true?"Completed updating":"Failed updating";
		});
		
		//Select
		get("/serverInfo/db", (req, res) -> {
			String URL = relayServiceDB.getDBURL();
			String USER = relayServiceDB.getDBUSER();
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("DB_URL",URL);
			jsonRoot.put("DB_USER",USER);
			return jsonRoot.toString();
		});		
		
		// Update
		post("/serverInfo/db", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.UpdateDBInfo(req.queryMap());
			return b==true?"Completed updating":"Failed updating";
		});		
	}
}
