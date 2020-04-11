import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Server {

	public static void main(String[] args){
		int port = args.length==1?Integer.parseInt(args[0]): 1234;
		try {
			//ServerThreads socketServer = new ServerThreads(port);
			new ServerThreads(port);
			
			JFrame frame = new JFrame("Server Webdav");

			        JPanel panel = new JPanel();
			        panel.setLayout(new FlowLayout());
			        JLabel label = new JLabel("Server WebDav Launched");
			        JLabel separation = new JLabel("-----------------------------------------------------");
			   //   JLabel separation = new JLabel("--------------(you can start some tests)-------------");
			        JLabel stop = new JLabel("Close(X) the window to stop the server");

			        panel.add(label);
			        panel.add(separation);
			        panel.add(stop);
			        
			        frame.add(panel);
			        frame.setSize(300, 110);
			        frame.setLocationRelativeTo(null);
			        frame.setResizable(false);
			        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			        frame.setVisible(true);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
