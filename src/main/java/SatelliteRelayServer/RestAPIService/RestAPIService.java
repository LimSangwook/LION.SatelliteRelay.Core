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
			res.header("Access-Control-Allow-Origin", "*");
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 15;
			int totalData = relayServiceDB.getCount("Satellite");
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			logger.info("[Request Satellite List] /satellite/list/" + nowPage);
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("TotalData",totalData);
			jsonRoot.put("TotalPage",totalPage);
			jsonRoot.put("NowPage",nowPage);
			jsonRoot.put("ListPerPage",listPerPage);
			jsonRoot.put("Datas",relayServiceDB.getSatelliteListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		// 전체 List 가져오기
		get("/satellite/listAll", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			int totalData = relayServiceDB.getCount("Satellite");
			logger.info("[Request Satellite List] /satellite/listAll");
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("TotalData",totalData);
			jsonRoot.put("Datas",relayServiceDB.getSatelliteListJsonArray(totalData, 1));
			return jsonRoot.toString();
		});
		// 삭제
		get("/satellite/delete/:id", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			int id = Integer.parseInt(req.params(":id"));
			logger.info("[Request Satellite Delete] /satellite/delete/" + id);
			return (relayServiceDB.delete("Satellite", id)==true)?"Satellite Delete Completed":"Satellite Delete Failed";
		});
		// 수정
		post("/satellite/update", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.Update("Satellite", req.queryMap());
			logger.info("[Request Satellite Update] /satellite/update/");
			return b==true?"Satellite Update Completed":"Satellite Update Failed";
		});
		// Create
		post("/satellite/create", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.Create("Satellite", req.queryMap());
			logger.info("[Request Satellite Create] /satellite/update/");
			return b==true?"Satellite Create Completed":"Satellite Create Failed";
		});
// Product
		get("/product/list/:page", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
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
			res.header("Access-Control-Allow-Origin", "*");
			int id = Integer.parseInt(req.params(":id"));
			logger.info("[Request Product DELETE] Product ID : " + id);
			boolean ret = relayServiceDB.delete("Product", id);
			if (ret == true) {
				relayService.removeService(id);
				logger.info("[Request Product DELETE] Product delete completed ID : " + id);
				return "Product delete completed";
			}
			return "Product delete failed";
		});
		// 수정
		post("/product/update", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean ret = relayServiceDB.Update("Product", req.queryMap());
			int productID = Integer.parseInt(req.queryMap().value("ID"));
			if (ret == true) {
				logger.info("[Request Product update] ID " + productID);
				relayService.reloadService(productID);
				return "Product update completed ";
			}
			return "Product update failed";
		});
		// Create
		post("/product/create", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			Integer productID = relayServiceDB.getNewID("Product");
			boolean ret = relayServiceDB.Create("Product", req.queryMap());
			logger.info("[Request Product Create] " + req.queryMap().value("NAME"));
			if (ret == true) {
				logger.info("[Request Product Create] Allow ID : " + productID);
				relayService.addService(productID);
				return "Product create completed";
			}
			return "Product create failed";
		});
		
// History
		// List 가져오
		get("/history/list/:page", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
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
			res.header("Access-Control-Allow-Origin", "*");
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
			res.header("Access-Control-Allow-Origin", "*");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.UpdateFTPInfo(req.queryMap());
			return b==true?"FTP update completed":"FTP update failed";
		});
		
		//Select
		get("/serverInfo/db", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			String URL = relayServiceDB.getDBURL();
			String USER = relayServiceDB.getDBUSER();
			JSONObject jsonRoot = new JSONObject();
			jsonRoot.put("DB_URL",URL);
			jsonRoot.put("DB_USER",USER);
			return jsonRoot.toString();
		});		
		
		// Update
		post("/serverInfo/db", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "*");
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			boolean b = relayServiceDB.UpdateDBInfo(req.queryMap());
			return b==true?"DB update completed":"DB update failed";
		});		
	}
}
