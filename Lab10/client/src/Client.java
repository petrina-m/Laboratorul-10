import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    Socket socket = null;

    public static void main(String[] args) throws IOException {
        String serverAddress = "127.0.0.1"; // The server's IP address
        int PORT = 8100; // The server's port

        Client client = new Client();
        client.connect(serverAddress, PORT);
        client.run();
    }
    //incerc sa conectez clientul la serverul respectiv
    public void connect(String addr, int port) throws IOException {
        try {
            socket = new Socket(addr, port);
        } catch (UnknownHostException e) {
            System.err.println("No server listening: " + e);
        } catch (Exception e) {
            System.err.println("Could not connect to server: " + e);
            System.exit(-1);
        }
    }

    public void run() throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader ( new InputStreamReader(socket.getInputStream()));

        Scanner scanner = new Scanner(System.in);
        String inputString;
        String response;

        do {
            inputString = scanner.nextLine();
            out.println(inputString);
            response = in.readLine();
            System.out.println(response);
        }
        while(!"exit".equals(inputString));
    }
}
