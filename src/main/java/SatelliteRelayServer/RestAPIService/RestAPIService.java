package SatelliteRelayServer.RestAPIService;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
		
		get("/heartbeat", (req, res) -> {
			return "{\"status\":\"running\"}";
		});
		

// Satellite
		// List 가져오기
		get("/satellite/list/:page", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 20;
			int totalData = relayServiceDB.getHistoryCount();
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONArray jsonRoot = new JSONArray();
			jsonRoot.put(new JSONObject("{\"TotalData\":" + totalData + "}"));
			jsonRoot.put(new JSONObject("{\"TotalPage\":" + totalPage + "}"));
			jsonRoot.put(new JSONObject("{\"NowPage\":" + nowPage + "}"));
			jsonRoot.put(new JSONObject("{\"ListPerPage\":" + listPerPage + "}"));
			jsonRoot.put(relayServiceDB.getSatelliteListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		// 삭제
		delete("/satellite/delete/:id", (req, res) -> {
			int id = Integer.parseInt(req.params(":id"));
			return (relayServiceDB.deleteSatellite(id)==true)?"TRUE":"FALSE";
		});
		// 수정
		put("/satellite/update/:id", (req, res) -> {
			String id = req.params(":id");
			return "Hello World - " + id;
		});
		// Create
		post("/satellite/create", (req, res) -> {
			String id = req.params(":id");
			return "Hello World - " + id;
		});
// Product
		get("/product/list/:page", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 20;
			int totalData = relayServiceDB.getHistoryCount();
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONArray jsonRoot = new JSONArray();
			jsonRoot.put(new JSONObject("{\"TotalData\":" + totalData + "}"));
			jsonRoot.put(new JSONObject("{\"TotalPage\":" + totalPage + "}"));
			jsonRoot.put(new JSONObject("{\"NowPage\":" + nowPage + "}"));
			jsonRoot.put(new JSONObject("{\"ListPerPage\":" + listPerPage + "}"));
			jsonRoot.put(relayServiceDB.getProductListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		// 삭제
		delete("/product/delete/:id", (req, res) -> {
			int id = Integer.parseInt(req.params(":id"));
			return relayServiceDB.deleteProduct(id);
		});
		// 수정
		put("/product/update/:id", (req, res) -> {
			String id = req.params(":id");
			return "Hello World - " + id;
		});
		// Create
		post("/product/create/:id", (req, res) -> {
			String id = req.params(":id");
			return "Hello World - " + id;
		});
		
// History
		// List 가져오
		get("/history/list/:page", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":page"));
			int listPerPage = 20;
			int totalData = relayServiceDB.getHistoryCount();
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONArray jsonRoot = new JSONArray();
			jsonRoot.put(new JSONObject("{\"TotalData\":" + totalData + "}"));
			jsonRoot.put(new JSONObject("{\"TotalPage\":" + totalPage + "}"));
			jsonRoot.put(new JSONObject("{\"NowPage\":" + nowPage + "}"));
			jsonRoot.put(new JSONObject("{\"ListPerPage\":" + listPerPage + "}"));
			jsonRoot.put(relayServiceDB.getHistoryListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
		
		get("/history/stop/:historyID", (req, res) -> {
			int nowPage = Integer.parseInt(req.params(":historyID"));
			int listPerPage = 20;
			int totalData = relayServiceDB.getHistoryCount();
			int totalPage = totalData / listPerPage + ((totalData % listPerPage) > 0?1:0);
			JSONArray jsonRoot = new JSONArray();
			jsonRoot.put(new JSONObject("{\"TotalData\":" + totalData + "}"));
			jsonRoot.put(new JSONObject("{\"TotalPage\":" + totalPage + "}"));
			jsonRoot.put(new JSONObject("{\"NowPage\":" + nowPage + "}"));
			jsonRoot.put(new JSONObject("{\"ListPerPage\":" + listPerPage + "}"));
			jsonRoot.put(relayServiceDB.getHistoryListJsonArray(listPerPage, nowPage));
			return jsonRoot.toString();
		});
	}
}
