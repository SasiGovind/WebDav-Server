import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class Output {
	PrintWriter os;
	BufferedOutputStream bos;
	
	public Output(OutputStream out) throws IOException{
		this.os = new PrintWriter(out, true);
		this.bos = new BufferedOutputStream(out);
	}

	public void sendFile(int fileLength, String contentType, byte[] fileData, boolean get) { //Get
		os.println("HTTP/1.1 200 OK");
        os.println("Server: LocalHost Server");
        os.println("Date: " + new Date());
        os.println("Accept-Ranges: bytes");
        os.println("Content-type: " + contentType);
        os.println("Content-length: " + fileLength);
        os.println("Connection: close");
        os.println(); 
        os.flush();
        if(get) {
        	try {
    			bos.write(fileData, 0, fileLength);
    			bos.flush();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
        System.out.println("HELLO");
	}

	public void sendPut(String response, String path) {
		os.println("HTTP/1.1 "+response);
		os.println("Content-length: 0");
        os.println("Content-Location: "+path);
        os.println();
        os.flush();
        System.out.println("response "+response);
        System.out.println("path "+path);
	}
	
	public void fileInexistant(String fName, boolean content) {
		os.println("HTTP/1.1 404 File Not Found");
	    os.println("Server: LocalHost Server");
	    os.println("Date: " + new Date());
	    os.println("Content-Type: text/html");
	    os.println("Content-length: " + (content?(126 + fName.length()):0));
	    os.println();
	    if(content) {
		    os.println("<!DOCTYPE html>");
		    os.println("<html>");
		    os.println("<head><title>File Inexistant</title></head>");
		    os.println("<body>");
		    os.println("<h1>404 File Inexistant: "+ fName + "</h1>");
		    os.println("</body>");
		    os.println("</html>");
	    }

	    os.flush();
	}

	public void sendOptions() {
		os.println("HTTP/1.1 200 OK");
		os.println("Server: LocalHost Server");
		os.println("Date: "+ new Date());
		os.println("Access-Control-Allow-Origin: http://test.html");
		os.println("Access-Control-Allow-Methods: GET, HEAD, PUT, OPTIONS, DELETE, COPY, MOVE, MKCOL, PROPFIND");
		os.println("Access-Control-Allow-Headers: X-PINGOTHER, Content-Type");
		os.println("Access-Control-Max-Age: 86400");
		os.println("Vary: Accept-Encoding, Origin");
		os.println("Content-Encoding: gzip");
		os.println("Content-Length: 0");
		os.println("Keep-Alive: timeout=2, max=100");
		os.println("Connection: Keep-Alive");
		os.println("Content-Type: text/plain");
		os.println();
		os.flush();
	}

	public void sendDelete(String fName) {
		os.println("HTTP/1.1 200 OK");
	    os.println("Server: LocalHost Server");
	    os.println("Date: " + new Date());
	    os.println("Content-Type: text/html");
	    os.println("Content-length: " + (115 + fName.length()));
	    os.println("Connection: Closed");
	    os.println();
	    os.println("<!DOCTYPE html>");
	    os.println("<html>");
	    os.println("<head><title>File Deleted</title></head>");
	    os.println("<body>");
	    os.println("<h1>URL " + fName +" Deleted.</h1>");
	    os.println("</body>");
	    os.println("</html>");
	    os.flush();
	}

	public void sendCopy() {
		os.println("HTTP/1.1 204 No Content");
		os.println("Content-length: 0");
		os.println();
		os.flush();
	}
	
	public void sendMkcol() {
		os.println("HTTP/1.1 201 Created");
		os.println("Content-length: 0"); 
		os.println();
		os.flush();
	}

	public void sendMove(String dst) {
		os.println("HTTP/1.1 204 No Content");
		os.println("Location: "+dst);
		os.println();
		os.flush();
	}
	
	public void closeConnection() throws IOException {
		os.println("Connection closed.");
		os.close();
		bos.close();
		System.out.println("Connection closed.\n");
	}

	public void sendProfind(String xml) {
		os.println("HTTP/1.1 207 Multi-Status");
		os.println("Content-Type: text/xml");
		os.println("Content-length: "+xml.length());
		os.println();
		os.println(xml);
		os.flush();
	}

	public void serverError(String errorMsg) {
		os.println("HTTP/1.1 "+errorMsg);
		os.println("Content-length: 0");
		os.println();
		os.flush();
	}
}
