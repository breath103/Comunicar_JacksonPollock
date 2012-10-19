import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


interface TCPClientDelegate {
	public void onReceiveMessage(String message);
}

class TCPClient implements Runnable {
	public TCPClientDelegate delegate;
	private String ip;
	private int port;
	private String str;
	Socket socket;
	
	private Thread messageLoopThread ;
	
	private BufferedWriter bufferedWriter;
	private BufferedReader bufferedReader;
	
	//생성자
	public TCPClient(String ip, int port,TCPClientDelegate delegate ) throws IOException{
		this.ip = ip;
		this.port = port;
		Socket tcpSocket = this.getSocket();   //사용자 메서드
		OutputStream os_socket = tcpSocket.getOutputStream();   //소켓에 쓰고
		InputStream is_socket = tcpSocket.getInputStream();   //소켓에서 읽는다

		bufferedWriter = new BufferedWriter(new OutputStreamWriter(os_socket));
		bufferedReader = new BufferedReader(new InputStreamReader(is_socket));
	
		this.delegate = delegate;
		
		messageLoopThread = (new Thread(this));
		messageLoopThread.start();
	}
	
	private Socket getSocket(){   //호스트의 주소와 포트를 사용, 소켓을 만들어 리턴하는 사용자 메서드
		Socket tcpSocket = null;
		try{
			tcpSocket = new Socket(ip, port);
		}catch(IOException ioe){
			ioe.printStackTrace();
			System.exit(0);
		}
		return tcpSocket;
	}

	@Override
	public void run() {
		String data;
		try {
			while ((data = bufferedReader.readLine()) != null) {
				delegate.onReceiveMessage(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}