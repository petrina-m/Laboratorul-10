import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread //in momentul in care incep un server nou, se porneste si thread-ul serverului
 {
    ServerSocket serverSocket;
    SocialNetwork network = new SocialNetwork();

    public static final int PORT = 8100;

    private int countClient=0;
    private boolean stopped=false;

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
        server.waitForStop();
        server.waitToFinishClients();
    }

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
    }

    public synchronized void run() {
        System.out.println("Waiting for a client ...");
        try {
            while (!stopped) {
                //cat timp programul nu a fost oprit, inca mai accepta clienti {
                Socket socket = serverSocket.accept();

                System.out.println("Accepted a client");

                new ClientThread(socket, network,this).start();
            }
        }
         catch (IOException e) {
            System.err.println("Oops... " + e);
        } finally {
            try {
                serverSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public synchronized void stopRunning() {
        stopped=true;
        notifyAll();
    }
    public synchronized void waitForStop() {
        while(!stopped)
        {
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }

        }
    }
    public synchronized void waitToFinishClients(){
        while(countClient>0){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
   public synchronized void changeClientCount(int num){
        countClient=countClient+num;
        notifyAll();
   }

}


