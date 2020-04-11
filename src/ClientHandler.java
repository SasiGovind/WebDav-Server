import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class ClientHandler implements Runnable {
	
	private Socket client;
	private Output out;
	private Input in;
	
	public ClientHandler(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {	
		try{
			System.out.println("Thread started with name:" + Thread.currentThread().getName());
			out = new Output(client.getOutputStream());
			in = new Input(client.getInputStream(), this);
			in.doRun(Thread.currentThread().getName());
		} catch (IOException ex) {}finally {}
	}
	
	// Read File ___________________________________________________________________________
	
	public void getReadFile(String fName, boolean get) {
		File file = new File(fName);
		String name = fName;
		
		if(file.isDirectory()) {
			if(fName.equals(".")) fName = "";
			if(new File(fName+"index.html").exists()) {
				System.out.println("BOOOOOM");
				file = new File(fName+"index.html");
				name = "index.html";
			}
			else {
				if(file.list().length>0) {
					File[] files = file.listFiles();
					boolean b = false;
					for(int i = 0; i < files.length; i++) {
						if(files[i].isFile() && !b) {
							file = files[i];
							name = file.getName();
							b = true;
							break;
						}
					}
					if (!b) {
						out.serverError("409 Conflict");
						return;
					}
				}else {
					out.serverError("409 Conflict"); 
					return;
				} 
			}
		}
		
		int fileLength = (int)file.length();
		byte[] fileData = new byte[fileLength];
		String contentType = findContentType(name);
		
        try(FileInputStream fileIn = new FileInputStream(file)) {
			fileIn.read(fileData);
			fileIn.close();
			out.sendFile(fileLength, contentType, fileData, get);
		} catch (FileNotFoundException e) {
			out.fileInexistant(name,get);
		} catch (IOException e) {}
	}
	
	// Put File _______________________________________________________________________________
	
	public void putFile(String path, byte[] bytes, String type) {
		File file = new File(path);
		System.out.println("content file |"+type+"|");
		System.out.println("contentTypeeeeee new file : |"+findContentType(file.getName())+"|");
		String response;
		if(!type.equals(findContentType(file.getName()))) {
			out.serverError("400 Bad Request");
			return;
		}
		else if(file.exists() && !file.isDirectory()) response = "204 No Content";
		else response = "201 Created";
		System.out.println("HELLO");
		try(FileOutputStream fileOut = new FileOutputStream(file)) {
			fileOut.write(bytes);
			fileOut.close();
		} catch (FileNotFoundException e) {
			out.fileInexistant(file.getPath(),true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("putFinish");
		out.sendPut(response, path);
	}
	
	// Option ____________________________________________________________________________________
	
	public void options() {
		out.sendOptions();
	}
	
	// Delete _____________________________________________________________________________________
	
	public void delete(File file, boolean response){
		System.out.println(file.getAbsolutePath());
		boolean process_done = false;
		if(file.exists() && file.isFile()) {
			System.out.println("FILE EXIST");
			file.delete();
			process_done = true;
		}else if(file.exists() && file.isDirectory()){
			System.out.println("DIRECTORY EXIST");
			File[] listOfFiles = file.listFiles();
    	    for (int i = 0; i < listOfFiles.length; i++) {
    	    	delete(listOfFiles[i],false);
    	    }
    	    file.delete();
    	    process_done = true;
		}else if(!file.exists()){
			out.fileInexistant(file.getPath(),true);
			System.out.println("FILE DOESN'T EXIST");
		}else {
			out.serverError("500 Internal Server Error");
			System.out.println("ERROR 500");
		}
		if(response && process_done) out.sendDelete(file.getPath());
	}
	
	// MKCOL ___________________________________________________________________________________
	
	public void createCollection(String path, boolean response) {
		System.out.println("mkcol : "+path);
		String src = path;
		if(path.endsWith("/")) src = path.substring(0, path.length()-1);
		
		if(new File(src).mkdir()) {
			if(response) out.sendMkcol();
		}else {
			if(new File(src).exists()) {
				out.serverError("405 Method Not Allowed");
			}else {
				out.serverError("409 Conflict");				
			}
			System.out.println("Le dossier n'as pas pu être crée");
		}
	}
	
	// COPY ______________________________________________________________________________________
	
	public void copyFileFile(File source, File destination) {
		/*boolean same = false;
		if(source.getPath().equals(source.getPath())) {
			System.out.println("1.....source : "+source.getPath());
			System.out.println("1.....dst : "+destination.getPath());
			same = true;
			System.out.println("SAME path source and dest");
			String dest = destination.getPath().toString();
			System.out.println("TEST"+dest);
			String name = dest.split("\\.")[0];
			System.out.println("TEST1"+name);
			String format = dest.split("\\.")[1];
			System.out.println("TEST2"+format);
			System.out.println("DEST NAME : "+ name+"Bis."+format);
			destination = new File(name+"Bis."+format);
		}*/
		System.out.println("source : "+source.getPath());
		System.out.println("dst : "+destination.getPath());
		InputStream is = null;
	    OutputStream os = null;
        try {
			is = new FileInputStream(source);
			os = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	        is.close();
	        os.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
        /*
        if(same) {
        	copyFileFile(destination, source);
        	delete(destination, false);
        }*/
	}
	
	public void copyFile(String src, String dst, String depth, String overWrite, boolean response) {
		if(depth!=null) {
			if(src.equals(dst)) {
				out.serverError("403 Forbidden");
				return;
			}
			System.out.println("cF : source : "+src);
			System.out.println("cF : destination : "+dst);
		    File source = new File(src);
		    if(source.exists()) {
			    File destination = new File(((dst.length()==0)?".":dst));
			    if(!destination.exists()) {out.serverError("409 Conflict");return;}
			    System.out.println("src : "+src);
			    System.out.println("dst : "+dst);
			    if(source.isFile() && (destination.isDirectory() || destination.isFile())) {
			    	if(src.contains("/")) {		
				    	String[] folders = src.split("/");
				    	String file = folders[folders.length-1];
				    	destination = new File((dst+file));
				    	System.out.println("F->D : "+dst+"/"+file);
			    	}else {								
			    		destination = new File((dst+src));
			    		System.out.println("coucou"+dst+src);
			    	}
			    	///////////////////////OVERWRITE//////////////////////
			    	if(destination.exists()) {
				    	if(overWrite.equals("T")) {
				    		if(source.getName().equals(destination.getName())) {
				    			out.serverError("412 Precondition Failed");
					    		return;
				    		} 
				    		delete(destination, false);
				    	}else if(overWrite.equals("F")) {
				    		out.serverError("412 Precondition Failed");
				    		return;
				    	}
				    }
			    	//////////////////////////////////////////////////////
			    	copyFileFile(source, destination);   
			    }else if(source.isDirectory() && destination.isDirectory()) {
			    	System.out.println("BOUM");
			    	File folder = new File(src);
			    	File[] listOfFiles = folder.listFiles();

		    	    for (int i = 0; i < listOfFiles.length; i++) {
		    	      if (listOfFiles[i].isFile()) {
		    	        System.out.println("File " + listOfFiles[i].getName());
		    	        copyFile(src+listOfFiles[i].getName(), dst, depth, overWrite, false);
		    	      }else if (listOfFiles[i].isDirectory()) {
		    	        System.out.println("Directory " + listOfFiles[i].getName());
		    	        
		    	        //////////////////////OVERWRITE/////////////////////
		    	        if(new File(dst+listOfFiles[i].getName()).exists()) {
					    	if(overWrite.equals("T")) {
					    		if(source.getPath().equals(destination.getPath())) {
					    			out.serverError("412 Precondition Failed");
						    		return;
					    		}
					    		delete(new File(dst+listOfFiles[i].getName()), false);
					    	}else if(overWrite.equals("F")) {
					    		out.serverError("412 Precondition Failed");
					    		return;
					    	}
					    }
		    	        ///////////////////////////////////////////////////
		    	        
		    	        createCollection(dst+listOfFiles[i].getName(), false);
		    	        copyFile(src+listOfFiles[i].getName()+"/", dst+listOfFiles[i].getName()+"/", ((depth.equals("0"))?null:depth), overWrite, false);
		    	      }
		    	    }
			    }
			    if(response) out.sendCopy();
		    }else {
		    	out.serverError("409 Conflict");
		    	System.out.println("DOESN'T EXIST");
		    }
		}
	}
	
	// MOVE ______________________________________________________________________________________________
	
	public void move(String src, String dst, String ov) {
		copyFile(src, dst, "infinity", ov, false);
		delete(new File(src),false);
		out.sendMove(dst);
	}
	
	// PROPFIND _________________________________________________________________________________________________________

	public void propfind(String path, String depth, String[] prop) {
		File file = new File(path);
		if(!file.exists()) {out.serverError("404 Not Found");return;}
		String xml = "<?xml version=\"1.0\"?>\n";
		xml = xml+"<a:multistatus xmlns:a=\"DAV:\">\n";
		if (depth.equals("0")) xml = xml+parcoursZero(file, " ", prop);
		else if (depth.equals("1")) xml = xml+parcoursUn(file, " ", prop);
		else xml = xml+parcoursInfinity(file, " ", prop);
		xml = xml+"</a:multistatus>\n";
		out.sendProfind(xml);
	}
	
	private String propFile(File file, String space, String[] prop) {
		String xml = "";
		String path = file.getPath().replaceAll("\\\\", "/");
		xml = xml+space+"<a:response>\n";
		xml = xml+space+" <a:href>http://localhost:1234/"+path+"</a:href>\n";
		xml = xml+space+" <a:propstat>\n";
		xml = xml+space+" <a:prop>\n";
		
		if (prop[0].equals("allprop")) {
			System.out.println("allprop");
			xml = xml+getdisplayname(space, file);
			xml = xml+getlastmodify(space, file);
			xml = xml+getcontentlength(space, file);
			xml = xml+getcontenttype(space, file);
		} else {
			for(int i = 0; i<prop.length; i++) {
				if (prop[i].equals("displayname")) xml = xml+getdisplayname(space, file);
				else if (prop[i].equals("lastmodified")) xml = xml+getlastmodify(space, file);
				else if (prop[i].equals("contentlength")) xml = xml+getcontentlength(space, file);
				else if (prop[i].equals("contenttype")) xml = xml+getcontenttype(space, file);
			}
		}
		
		xml = xml+space+" </a:prop>\n";
		xml = xml+space+" <a:status>HTTP/1.1 200 OK</a:status>\n";
		xml = xml+space+" </a:propstat>\n";
		xml = xml+space+"</a:response>\n";
		return xml;
	}
	
	private String getdisplayname(String space, File file) {
		return space+" <a:displayname>"+file.getName()+"</a:displayname>\n";
	}
	
	private String getlastmodify(String space, File file) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return space+" <a:lastmodified>"+sdf.format(file.lastModified())+"</a:lastmodified>\n";
	}
	
	private String getcontenttype(String space, File file) {
		if(file.isFile()) return space+" <a:contenttype>"+findContentType(file.getName())+"</a:contenttype>\n";
		else return space+" <a:resourcetype> Collection </a:resourcetype>\n";
	}
	
	private String getcontentlength(String space, File file) {
		if(file.isFile()) return space+" <a:contentlength>"+file.length()+"</a:contentlength>\n";
		else return space+" <a:contentlength>"+folderSize(file)+"</a:contentlength>\n";
	}
	
	private String parcoursZero(File file, String space, String[] prop) {
		String xml = "";
		xml = xml+propFile(file, space, prop);
		if(file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				xml = xml+propFile(listOfFiles[i], space+" ", prop);
			}
		}
		return xml;
	}
	
	private String parcoursUn(File file, String space, String[] prop) {
		String xml = "";
		xml = xml+propFile(file, space, prop);
		if(file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				xml = xml+propFile(listOfFiles[i], space+" ", prop);
				if (listOfFiles[i].isDirectory()) {
					File[] files = listOfFiles[i].listFiles();
					for (int j = 0; j < files.length; j++) {
						xml = xml+propFile(files[j], space+"  ", prop);
					}
				}
			}
		}
		return xml;
	}
	
	private String parcoursInfinity(File file, String space, String[] prop) {
		String xml = "";
		xml = xml+propFile(file, space, prop);
		if(file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				xml = xml+parcoursInfinity(listOfFiles[i], space+" ", prop);
			}
		}
		return xml;
	}
	
	public static long folderSize(File directory) {
	    long length = 0;
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += folderSize(file);
	    }
	    return length;
	}
	
	private String findContentType(String fileName){
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")){
		  return "text/html";
		}else if (fileName.endsWith(".png")){
		  return "image/png";
		}else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")){
		  return "image/jpeg";
		}else if (fileName.endsWith(".gif")){
		  return "image/gif";
		}else if (fileName.endsWith(".class") || fileName.endsWith(".jar")){
		  return "applicaton/octet-stream";
		}else return "text/plain";
	}
	
	public void endConnection() {
		try {
			out.closeConnection();
			in.closeConnnection();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}






















