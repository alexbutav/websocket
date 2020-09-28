import java.net.*;
import java.io.*;

public class server {
	public static void main(String []args) throws IOException {
		int portNumber = Integer.parseInt(args[0]);

		try (
			ServerSocket serverSocket = new ServerSocket(portNumber);
			Socket clienSocket        = serverSocket.accept();
			PrintWriter out           = new PrintWriter(clienSocket.getOutputStream(), true);
			BufferedReader in         = new BufferedReader( new InputStreamReader( clienSocket.getInputStream()));
		) {
			String inputLine, outputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
		}

		System.out.println("Hello World"); // prints Hello World
   }
}
