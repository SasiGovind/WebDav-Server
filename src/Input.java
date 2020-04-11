import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {
	ClientHandler handler;
	InputStream in;
	boolean stop;
	
	public Input(InputStream in, ClientHandler handler) throws IOException {
		this.in = in;
		this.handler = handler;
		this.stop = false;
	}

	public void doRun(String handlerThreadName) {
		
		String path;
		
		try (LineBufferedInputStream request = new LineBufferedInputStream(in)) {
			while (!stop) {
				String line = ".";
				String requestHeader = "";
				
				while(!line.equals("")) {
					line = request.readLine();
					if(line != null) requestHeader += line + "\n";
				}
				line = ".";
				System.out.print(requestHeader);
				
				String method = requestHeader.split("\n")[0].split(" ")[0];
				System.out.println(Thread.currentThread().getName());
				path = requestHeader.split("\n")[0].split(" ")[1];
				if(path.startsWith("http://localhost:")) path = requestHeader.split("\n")[0].split(" ")[1].substring(22);
				else path = requestHeader.split("\n")[0].split(" ")[1].substring(1);
				if(path.length()==0) path =".";
				System.out.println("PATHHHHHHH : " + path);
				
				switch (method) {
				/******************************HTTP***********************************/
					case "GET" : 
						handler.getReadFile(path, true);
					break;
					case "HEAD" : 
						handler.getReadFile(path, false);
					break;
					case "PUT" : 
						int size = getFileSize(requestHeader);
						byte[] bytes = readPut(size, request);
						System.out.println("readPutFinesh");
						handler.putFile(path, bytes, getContentType(requestHeader));
					break;
					case "OPTIONS" :
						handler.options();
					break;
					case "DELETE" :
						handler.delete(new File(path), true);
					break;
					/******************************WEBDAV********************************/
					case "MKCOL":
						handler.createCollection(path,true);
					break;
					case "COPY" :
						String destinationCopy = getDestination(requestHeader);
						String depthCopy = getDepth(requestHeader);
						String overWrite = getOverWrite(requestHeader);
						handler.copyFile(path,destinationCopy,depthCopy,overWrite, true);
					break;
					case "MOVE" :
						String destinationMove = getDestination(requestHeader);
						String overWrite2 = getOverWrite(requestHeader);
						handler.move(path, destinationMove, overWrite2);
					break;
					case "PROPFIND" :
						System.out.println("propfind -------------------------------------------------------------");
						String depth = getDepth(requestHeader);
						int sizeProp = getFileSize(requestHeader);
						byte[] bytesProp = readPut(sizeProp, request);
						String str = new String(bytesProp, StandardCharsets.UTF_8);
						System.out.println("boum");
						handler.propfind(path, depth, getProp(str));
					break;
					default:;			
				}
				
				stop = !getConnectionMode(requestHeader);
				if(stop) {handler.endConnection();System.out.println("**************");}
			}
		}catch (Exception e) {}
	}
	
	private String[] getProp(String str) {
		String s = str;
		if (s.contains("allprop")||s.equals("")) {
			System.out.println("boumboum");
			String[] tab = new String[1]; 
			tab[0] = "allprop"; 
			return tab;
		}
		String[] tabProp = new String [nbroccur("<a:prop>", str)];
		for (int i= 0; i<tabProp.length; i++) {
			tabProp[i] = s.split("<a:prop><a:")[1].split("/></a:prop>")[0];
			s = s.split(tabProp[i]+"/></a:prop>")[1];
		}
		return tabProp;
	}
	
	private int nbroccur(String s, String in) {
		Matcher matcher = Pattern.compile(s).matcher(in);
		int i = 0;
		while(matcher.find()) {
	        i++;
	    }
		return i;
	}

	private String getOverWrite(String requestHeader) {
		String overWrite = "F";
		if(requestHeader.contains("Overwrite")) {
			overWrite = requestHeader.split("Overwrite:")[1].split(" ")[1].split("\n")[0];
		}
		return overWrite;
	}

	private String getDepth(String requestHeader) {
		String depth = "infinity";
		if(requestHeader.contains("Depth")) {
			depth = requestHeader.split("Depth:")[1].split(" ")[1].split("\n")[0];
		}
		System.out.println("depth  : "+depth);
		return depth;
	}

	private byte[] readPut(int size, LineBufferedInputStream request) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		byte buf[] = new byte[8192];
		int len = 0;
		int reste = size;
		while (reste > 0 && len != -1) {
			int toRead = buf.length;
			if (toRead > reste) toRead = reste;
			len = request.read(buf, 0, toRead);
			result.write(buf, 0, len);
			reste -= len;
		}
		return result.toByteArray();
	}
	
	private boolean getConnectionMode(String requestHeader) {
		String[] headers = requestHeader.split("\n");
		for(int i = 0; i < headers.length; i++) {
			if(headers[i].contains("keep-alive")) {
				return true;
			}
		}
		return false;
	}
	
	private String getDestination(String requestHeader) {
		String[] headers = requestHeader.split("\n");
		String dest;
		for(int i = 0; i < headers.length; i++) {
			if(headers[i].contains("Destination")) {
				dest = headers[i].split(" ")[1];
				if(dest.contains("http://localhost:1234/")) dest = dest.replace("http://localhost:1234/", "");
				return dest;
			}
		}
		return null;
	}
	
	private int getFileSize(String requestHeader) {
		String[] headers = requestHeader.split("\n");
		int size = 0;
		for(int i = 0; i < headers.length; i++) {
			if(headers[i].contains("content-length") || headers[i].contains("Content-Length")) {
				size = Integer.parseInt(headers[i].split(" ")[1]);
				return size;
			}
		}
		return size;
	}
	
	private String getContentType(String requestHeader) {
		return requestHeader.split("Content-Type: ")[1].split("\n")[0];
	}

	public void closeConnnection() throws IOException {
		in.close();
	}

}