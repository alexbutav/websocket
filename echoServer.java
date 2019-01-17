import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class echoServer {
	public static void main(String []args) throws IOException {
		//starting server
		try(ServerSocket server = new ServerSocket(80)) {
			System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection...");
			//waiting for connections
			while(true) {
				Socket client = server.accept();
				//working with connection
				new Thread(new ConnectionHandler(client)).start();
			}
		}
	}
}

class ConnectionHandler implements Runnable {
	//connection socket
	private Socket client;

	public ConnectionHandler(Socket client) {
		this.client = client;
	}

	public void run() {
		System.out.println("A client connected.");
		
		try(				
			InputStream in   = client.getInputStream();
			OutputStream out = client.getOutputStream();
		) {
			String data      = new Scanner(in, "UTF-8").useDelimiter("\r\n\r\n").next();
			Matcher get      = Pattern.compile("^GET").matcher(data);
			if (get.find()) {
	    		Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
			    match.find();
			    try {
				    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
						            + "Connection: Upgrade\r\n"
						            + "Upgrade: websocket\r\n"
						            + "Sec-WebSocket-Accept: "
						            + new String(
						            	Base64.getEncoder().encode(
						                    MessageDigest
						                    .getInstance("SHA1")
						                    .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
						                            .getBytes("UTF-8"))))
						            + "\r\n\r\n")
				    					.getBytes("UTF-8");

			    	out.write(response, 0, response.length);
				} catch (NoSuchAlgorithmException e) {

				}
				int d;
				
				while((d = in.read()) != -1) {
					System.out.println(Integer.toString(d));
				}
				System.out.println("Data stopped");
				System.out.println("Client closed");
			} else {

			}
		} catch (IOException e) {

		}
	}
}

/*
ExtendsThread et = new ExtendsThread();
Thread tc1 = new Thread(et);
tc1.start();
Thread.sleep(1000);
Thread tc2 = new Thread(et);
tc2.start();
Thread.sleep(1000);
Thread tc3 = new Thread(et);
tc3.start();
*/