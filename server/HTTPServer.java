package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class HTTPServer {
	
	private ServerSocket serverSocket;
	private final String rootDirectory = ".";
	private final String[] defaultServingList = {"index.html", "index.htm", "Default.html", "Default.htm", "default.html", "default.htm"};
	
	public void runServer(int port) throws IOException
	{
		serverSocket = new ServerSocket(port,20);
		
		System.out.println("Server started, listening on the port " + port);
		
		while (true)
		{
			Socket socket = serverSocket.accept();
			
			System.out.println("Client connection received from " + socket.getInetAddress().getCanonicalHostName());
			
			BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);	
			String line, input = "", firstLine;
			firstLine = in.readLine();
			input += firstLine;
			while((line = in.readLine()) != null && !line.equals(""))
			{
				input += '\n' + line;
			}
			
			System.out.println(firstLine);
			String parts[] = firstLine.split(" ");
			if(parts.length != 3)
			{
				out.append("HTTP/1.1 400 Bad Request\n\r\n");
				System.out.println("Response: HTTP/1.1400 Bad Request");
				in.close();
				out.close();
				socket.close();
				continue;
			}
			String method = parts[0],
					path = parts[1],
					version = parts[2];
			
			if(!version.equalsIgnoreCase("HTTP/1.1")){
				out.append("HTTP/1.1 505 HTTP Version Not Supported\n\r\n");
				System.out.println("Response: HTTP/1.1 505 HTTP Version Not Supported");
			}
			else if(method.equalsIgnoreCase("get"))
			{
				get(path, out);
			}
			else
			{
				//At present not handling any other protocol :(
				out.append("HTTP/1.1 405 Method Not Allowed\n\r\n");
				System.out.println("Response: HTTP/1.1 405 Method Not Allowed");
				
			}
			in.close();
			out.close();
			socket.close();
			
		}
		
	}
	
	
	private void get(String path, PrintWriter out)
	{
		File root = new File(rootDirectory), toServe;
		String fileName = "";
		
		if(!path.equals("/"))
		{
			fileName = path.substring(1);
			
		}
		else
		{
			boolean found = false;
			String[] fileList = root.list();
			ArrayList<String> files= new ArrayList<String>(Arrays.asList(fileList));
			for (String s : defaultServingList){
				if(files.contains(s)){
					fileName = s;
					found = true;
					break;
				}
			}
			if(!found)
			{
				out.append("HTTP/1.1 404 Not Found\n\r\n");
				System.out.println("Response: HTTP/1.1 404 Not Found");
				return;				
			}
		}
		
		toServe = new File(fileName);
		if(!toServe.exists())
		{
			out.append("HTTP/1.1 404 Not Found\n\r\n");
			System.out.println("Response: HTTP/1.1 404 Not Found");
			return;
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(toServe));) {
			
			System.out.println("Serving " + fileName);
			
			out.append("HTTP/1.1 200 OK\n\r\n");
			System.out.println("Response: HTTP/1.1 200 OK");
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
		        out.println(inputLine);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

