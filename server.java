import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.net.*;
import java.io.*;

public class server {
	public static void main(String []args) throws IOException, NoSuchAlgorithmException  {
		int portNumber = (args.length > 0 ? Integer.parseInt(args[0]) : 80);

		try (
			ServerSocket serverSocket = new ServerSocket(portNumber);
			Socket clienSocket        = serverSocket.accept();
			PrintWriter out           = new PrintWriter(clienSocket.getOutputStream(), true);
			BufferedReader in         = new BufferedReader( new InputStreamReader( clienSocket.getInputStream()));
			BufferedInputStream input = new BufferedInputStream(clienSocket.getInputStream());
		) {						
			String inputLine, outputLine, inputHash = "";
			String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
			String[] httpHeader = new String[10];
			byte[] outputB64Hash = new byte[40];
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.length() == 0) break;
				System.out.println(inputLine);
				httpHeader = inputLine.split(":");
				if(httpHeader[0].trim().equals("Sec-WebSocket-Key")) {
					inputHash = httpHeader[1].trim() + GUID;
					MessageDigest md = MessageDigest.getInstance("SHA-1");
					byte[] inputHashBytes = inputHash.getBytes("UTF-8");
					byte[] digest = md.digest(inputHashBytes);
					outputB64Hash = Base64.getEncoder().encode(digest);
				}
			}

			System.out.println("--------------------------------");
			System.out.println("HTTP/1.1 101 Switching Protocols");
			System.out.println("Upgrade: websocket");
			System.out.println("Connection: Upgrade");
			System.out.println("Sec-WebSocket-Accept: " + new String(outputB64Hash));
			//answer to client
			out.println("HTTP/1.1 101 Switching Protocols");
			out.println("Upgrade: websocket");
			out.println("Connection: Upgrade");
			out.println("Sec-WebSocket-Accept: " + new String(outputB64Hash));
			out.println();

			//frame messaging
			int i = 0;
			byte b = 0;
			String s1;
			while ((b = (byte) input.read()) != -1) {
				if(i == 4) {
					i = 0;
					System.out.println("New frame");
				}
				s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
				System.out.println("i = " + Integer.toString(i++) + " input byte = " +s1);
			}
		} 

		//System.out.println("Hello World"); // prints Hello World
   }
}