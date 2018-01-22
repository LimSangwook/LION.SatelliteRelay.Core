package SatelliteRelayServer.RelayService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.Models.ProductInfo;
import SatelliteRelayServer.Models.ProductInfo.FILTER_TYPE;
import SatelliteRelayServer.Models.ProductInfo.PROCESS_TYPE;
import SatelliteRelayServer.RelayService.Components.HistorySaver;
import SatelliteRelayServer.RelayService.Components.TargetDB;
import SatelliteRelayServer.RelayService.Components.TargetFTP;

public class RelayProductService extends TimerTask {
	static Logger logger = Logger.getLogger(RelayProductService.class);
	private Timer jobScheduler = new Timer();
	private HistorySaver historySaver = null;
	private int historyID = -1;

	private ProductInfo productInfo = null;
	private TargetFTP targetFTP = null;
	private TargetDB targetDB = null;
	SatelliteRelayDBManager serviceDB = null;
	
	public ProductInfo getProductInfo() {
		return productInfo;
	}
	public RelayProductService(ProductInfo info, SatelliteRelayDBManager serviceDB) {
		this.productInfo = info;
		this.serviceDB = serviceDB;
		this.historySaver = new HistorySaver(info.productID, serviceDB);
	}

	public void registSchedule() {
		logger.info("[RelayProductService] registSchedule ID:"+ productInfo.productID + " NAME:" + productInfo.productName + " FirstTime:" + productInfo.getScheduleStartDate().toString());
	    jobScheduler.scheduleAtFixedRate(this, productInfo.getScheduleStartDate(), productInfo.getSchduleInterval());
	}
	
	private boolean initServers() {
		logger.info("\t [Schedule Job] init Server - ProcessTYPE : " + productInfo.processTYPE);		
		historySaver.add(historyID, "[InitSendServers] init Server - ProcessTYPE : " + productInfo.processTYPE);		
		// init FTP
		if (productInfo.processTYPE==PROCESS_TYPE.DB_FTP || productInfo.processTYPE==PROCESS_TYPE.FTP_ONLY) {
			logger.info("\t [Schedule Job] Init FTP");
			historySaver.add(historyID, "[InitSendServers] init FTP  ");
			targetFTP = new TargetFTP();
		}
		
		// init DB
		if (productInfo.processTYPE==PROCESS_TYPE.DB_FTP || productInfo.processTYPE==PROCESS_TYPE.DB_ONLY) {
			logger.info("\t [Schedule Job] Init DB");
			historySaver.add(historyID, "[InitSendServers] init DB ");
			targetDB = new TargetDB();
			if (targetDB.init(serviceDB)) {
				logger.info("\t [Schedule Job] Completed Init Send DB Servers");
				historySaver.add(historyID, "[InitSendServers] Completed ");
				return true;
			}
		}
		
		// REMOVE LOCAL ONLY
		if (productInfo.processTYPE==PROCESS_TYPE.REMOVE_LOCAL_ONLY) {
			logger.info("\t [Schedule Job] Init SKIP");
			historySaver.add(historyID, "[InitSendServers] init SKIP");
			return true;
		}
		historySaver.add(historyID, "[InitSendServers] Fail ");
		return false;
	}
	private void closeServers() throws Exception {
		if (productInfo.processTYPE==PROCESS_TYPE.DB_FTP || productInfo.processTYPE==PROCESS_TYPE.DB_ONLY) {
			targetDB.close();
		}
		if (productInfo.processTYPE==PROCESS_TYPE.DB_FTP || productInfo.processTYPE==PROCESS_TYPE.FTP_ONLY) {
			targetFTP.close();
		}
	}	
	
	public void run() {
		logger.info("\t [Schedule Job] START");
		historySaver.clean();
		historyID = historySaver.createNewHistory();
		logger.info("\t [Schedule Job] START HistoryID : " + historyID);
		initServers();
		List<File> matchedFileList = new ArrayList<File>();
		boolean bResult = false;
		if (task0InitNCheck() && task1Filtering(matchedFileList)) {
			for (File afile : matchedFileList ) {
				switch(productInfo.processTYPE) {
				case DB_FTP:
					if (task2FTPTransmition(afile) && task3DBInsert(afile) && task4FileRemove(afile)) {
						bResult = true;
					}
					break;
				case DB_ONLY:
					if (task3DBInsert(afile) && task4FileRemove(afile)) {
						bResult = true;
					}
					break;
				case FTP_ONLY:
					if (task2FTPTransmition(afile) & task4FileRemove(afile)) {
						bResult = true;
					}
					break;
				case REMOVE_LOCAL_ONLY:
					if (task4FileRemove(afile)) {
						bResult = true;
					}
					break;
				}
			}
		}
		if (bResult == true) {
			historySaver.add(historyID, "[RESULT] Successed ");
			historySaver.setStatus(historyID, "SUCCESSED");
			logger.info("\t [Schedule Job] HistoryID : " + historyID + " - Successed!");
		} else  {
			historySaver.add(historyID, "[RESULT] Failed ");
			historySaver.setStatus(historyID, "FAILED");
			logger.info("\t [Schedule Job] HistoryID : " + historyID + " - Failed!");
		}
		
		try {
			closeServers();
		} catch (Exception e) {
			logger.info("\t [Schedule Job] closeServers Error");
			e.printStackTrace();
		}
		logger.info("\t [Schedule Job] END");
	}
	private boolean task0InitNCheck() {
		historySaver.add(historyID, "[INIT] Reay ProductID : " + productInfo.productID);
		
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task0-Init START");
		if (productInfo.getSourcePath().isDirectory() == false || productInfo.getSourcePath().exists() == false) {
			return false;
		}
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task0-Init END");
		return true;
	}
	
	private boolean task1Filtering(List<File> matchedFileList) {
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task1-Filtering START");
		historySaver.add(historyID, "[STEP1] START : Filtering ");
		historySaver.add(historyID, "[STEP1] Filter Type : " + productInfo.getFilterType().toString());
		historySaver.add(historyID, "[STEP1] Simple Filter : " + productInfo.getFilterSimple());
		historySaver.add(historyID, "[STEP1] RegularExpression : " + productInfo.getFilterRegularExpression());

		findMatchedFiles(matchedFileList, productInfo.getSourcePath(), productInfo.getFilterType(), productInfo.getFilterSimple(), productInfo.getFilterRegularExpression());
		historySaver.add(historyID, "[STEP1] matched count : " + matchedFileList.size());
		historySaver.setFileCount(historyID, matchedFileList.size());
		String fileNames = "";
		for (File afile : matchedFileList) {
			historySaver.add(historyID, "[STEP1] matched File : " + afile.getName() );
			fileNames += afile.getName() + " ";
		}
		
		if (matchedFileList.size() != 0) {
			historySaver.SetFiles(historyID, fileNames);
		}
		
		if (matchedFileList.size() == 0) {
			historySaver.add(historyID, "[STEP1] No Matching Files ");
			historySaver.add(historyID, "[STEP1] END : Filtering ");
			logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task1-Filtering END");
			return false;
		}
		
		historySaver.add(historyID, "[STEP1] END : Filtering ");
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task1-Filtering END");
		
		return true;
	}

	private void findMatchedFiles(List<File> matchFile, File path, FILTER_TYPE filter_TYPE, String simpleFilter, String regex) {
		for (File aFile : path.listFiles()) {
			if (aFile.isDirectory()) {
				continue;
			} else {
				if (aFile.canWrite() == false) return; // Lock 걸려있으면 패스
				switch (filter_TYPE) {
				case SIMPLE:	
					if (aFile.getName().contains(simpleFilter) == true) {
						matchFile.add(aFile);
					}
					break;
				case REGEXP:
					String fileName = aFile.getName();
					if (fileName.matches(regex) == true) {
						matchFile.add(aFile);
					}
					break;
				default:
					break;
				}
			}
		}
	}	
	
	private boolean task2FTPTransmition(File afile) {
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task2-FTPTransmition START -" + afile.getName());
		historySaver.add(historyID, "[STEP2] START : FTP Transmition -" + afile.getName() + " - " + productInfo.getFTPTargetPath());
		try {
			productInfo.setAppendixColumns(afile);
			if (targetFTP.sendFile(afile, productInfo.getFTPTargetPath()) == false) {
				historySaver.add(historyID, "[STEP2] File : " + afile.getName() + " send Finished - " + productInfo.getFTPTargetPath());
				return false;
			}
			historySaver.add(historyID, "[STEP2] File : " + afile.getName() + " send Finished - " + productInfo.getFTPTargetPath());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			historySaver.add(historyID, "[STEP2] File : " + afile.getName() + " - " + productInfo.getFTPTargetPath() + " send Error! : " + e.getMessage() );
			return false;
		}
		
		historySaver.add(historyID, "[STEP2] END : FTP Transmition - " + afile.getName() + " - " + productInfo.getFTPTargetPath());
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task2-FTPTransmition END -" + afile.getName() + " - " + productInfo.getFTPTargetPath());
		return true;
	}

	private boolean task3DBInsert(File afile) {
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task3-DBInsert START -" + afile.getName());
		historySaver.add(historyID, "[STEP3] START : DB Insert -" + afile.getName());

		if (targetDB.existFileNameInDB(afile.getName()) == true) {
			historySaver.add(historyID, "[STEP3] Check Exist File Name(IDENTIFIER) : true -> Update");
			if (targetDB.update(productInfo, afile) == false) {
				historySaver.add(historyID, "[STEP3] File : " + afile.getName() + " DB Insert update!!");
				return false;
			}
		} else {
			historySaver.add(historyID, "[STEP3] Check Exist File Name(IDENTIFIER) : false -> Insert ");
			if (targetDB.insert(productInfo, afile) == false) {
				historySaver.add(historyID, "[STEP3] File : " + afile.getName() + " DB Insert Error!!");
				return false;
			}
		}
		historySaver.add(historyID, "[STEP3] File : " + afile.getName() + " DB Insert finished, SEQ : " + productInfo.appendixColumns.SEQ);

		historySaver.add(historyID, "[STEP3] END : DB Insert -" + afile.getName());
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task3-DBInsert END-" + afile.getName());	
		return true;
	}

	private boolean task4FileRemove(File afile) {
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task4-FileRemove START - " + afile.getName());
		historySaver.add(historyID, "[STEP4] START : Remove Local File - " + afile.getName());
		if (afile.delete() == false) {
			historySaver.add(historyID, "[STEP4] File : " + afile.getName() + " Remove Error!");
			return false;
		}
		historySaver.add(historyID, "[STEP4] File : " + afile.getName() + " Remove finished");
		historySaver.add(historyID, "[STEP4] END : Remove Local File - " + afile.getName());
		logger.info("\t [Schedule Job (" + historyID + ")] PID : " + productInfo.productID + "-" + productInfo.productName + " - Task4-FileRemove END - " + afile.getName());		
		return true;
	}

	public void stop() {
		jobScheduler.cancel();
	}
}
