package SatelliteRelayServer.RelayService.Components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

public class FTPImpl {
    private String server = "xxxxx";
    private int port = 21;
    private FTPClient ftpClient;

    public FTPImpl(String server, int port, boolean isSFTP) {
        this.server = server;
        this.port = port;
        if (isSFTP == true) {
        		ftpClient = new FTPSClient();
        } else {
        		ftpClient = new FTPClient();
        }
    		ftpClient.enterLocalPassiveMode();
    }
    
    // 계정과 패스워드로 로그인
    public boolean login(String user, String password) {
        try {
            this.connect();
            boolean b = ftpClient.login(user, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return b;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

//    // 서버로부터 로그아웃
//    private boolean logout() {
//        try {
//            return ftpClient.logout();
//        }
//        catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//        return false;
//    }

    // 서버로 연결
    public boolean connect() {
        try {
            ftpClient.connect(server, port);
            int reply;
            // 연결 시도후, 성공했는지 응답 코드 확인
            reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.err.println("서버로부터 연결을 거부당했습니다");
                return false;
            }
        }
        catch (IOException ioe) {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch(IOException f) {
                    //
                }
            }
            System.err.println("서버에 연결할 수 없습니다");
            return false;
        }
        return true;
    }

    // FTP의 ls 명령, 모든 파일 리스트를 가져온다
    public FTPFile[] list() {
        FTPFile[] files = null;
        try {
            files = this.ftpClient.listFiles();
            return files;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    // 파일을 전송 받는다
    public File get(String source, String target) {
        OutputStream output = null;
        try {
            File local = new File(source);
            output = new FileOutputStream(local);
        }
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        File file = new File(source);
        try {
            if (ftpClient.retrieveFile(source, output)) {
                return file;
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
    
    // 파일 업로드 
    public void put(File source, String target) throws Exception {
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.makeDirectory(target);
        cd(target);
        FileInputStream fis = new FileInputStream(source);
        boolean isSuccess = ftpClient.storeFile(source.getName(), fis); // File 업로드
        fis.close();

        if (!isSuccess){ 
        		throw new Exception("파일 업로드를 할 수 없습니다."); 
        	} 
    }

    // 서버 디렉토리 이동
    public void cd(String path) {
        try {
            ftpClient.changeWorkingDirectory(path);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

	public void close() throws IOException {
		if(ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
	}

//    // 서버로부터 연결을 닫는다
//    private void disconnect() {
//        try {
//            ftpClient.disconnect();
//        }
//        catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }

}

