import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class ClientThread extends Thread {
    private final Server server;
    private Socket socket = null;
    SocialNetwork network;
    boolean running;

    String username;
    boolean loggedIn = false;

    public ClientThread(Socket socket, SocialNetwork network, Server server) {
        this.server=server;
        this.socket = socket;
        this.network = network;
    }

    public void run() {
        server.changeClientCount(1);
        running = true;
        while (running) {
            String request = "";

            BufferedReader in = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                assert in != null;
                socket.setSoTimeout(5*60*1000); //5 min
                request = in.readLine();
            } catch (SocketTimeoutException e) {
                running=false;
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            try {
                handleRequest(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!socket.isClosed()){
            try{
                socket.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        server.changeClientCount(-1);
    }

    private void handleRequest(String request) throws IOException {
        if (request.length() == 0) return;
        String[] tokens = request.split(" ");

        PrintWriter out = new PrintWriter(socket.getOutputStream());
        StringBuilder response = new StringBuilder();

        switch (tokens[0]) {
            case "register":
                if (tokens.length > 1) {
                    network.register(tokens[1]);
                    response = new StringBuilder ("Registered user: " + tokens[1]);
                }
                break;
            case "login":
                if (tokens.length > 1) {
                    if (network.isUserRegistered(tokens[1]))
                    {
                        if (loggedIn) {
                            username = tokens[1];
                            response = new StringBuilder("Switched account to: " + username);
                        } else {
                            loggedIn = true;
                            username = tokens[1];
                            response =new StringBuilder( "Logged in as: " + username);
                        }
                    } else {
                        response =new StringBuilder( "User is not registered yet");
                    }
                } else {
                    response = new StringBuilder("Not enough credentials ");
                }
                break;
            case "friend":
                if (loggedIn) {
                    List<String> friends = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));
                    List<String> addedFriends = network.addFriends(username, friends);
                    response = new StringBuilder("You are now friends with: " + addedFriends);
                    if (friends.size() > addedFriends.size()) {
                        response.append( " some friends could not be added; not found in network (not registered)!");
                    }
                } else {
                    response = new StringBuilder("Please log in before ");
                }
                break;
            case "send":
                if (loggedIn) {
                    String message = request.substring(tokens[0].length() + 1);
                    network.sendMessage(username, message);
                    response =new StringBuilder( "Your message was sent");
                } else {
                    response =new StringBuilder( "Please log in before ");
                }
                break;
            case "read":
                if (loggedIn) {
                    List<String> messages = network.readMessage(username);
                    if (messages == null) {
                        response = new StringBuilder("No more messages.");
                    } else {
                        response =new StringBuilder( "New messages:");
                        for (String message : messages) {
                            response.append("%NEWLINE%" + message);
                        }
                    }
                } else {
                    response = new StringBuilder("Please log in before ");
                }
                break;
            case "stop":
                server.stopRunning();
                running = false;
                response = new StringBuilder("Server stopped");
                break;
            case "exit":
                running=false;
                break;
            default:
                response =new StringBuilder( "Wrong action given");
        }

        out.println(response);
        out.flush();
    }
}
