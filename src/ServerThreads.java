import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThreads extends Thread{
	private int port;
	private boolean stop = false;

	public ServerThreads(int port) throws IOException{
		this.port = port;
		this.start();
	}

	@Override
	public void run() {

		try (ServerSocket serverSocket = new ServerSocket(port)){
			System.out.println("Server lancé avec le port : " + port);
			while (!stop) {
				System.out.println("Attente d'une connexion");
				Socket client = serverSocket.accept();
				System.out.println("Un client a été connecté : "+ client.getInetAddress().getCanonicalHostName());
				new Thread(new ClientHandler(client)).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void finish() {
		stop = true;
	}
}
