import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGameServer {
    private static int lowerLimit;
    private static int upperLimit;
    public static void main(String [] args){
        int port = 4444;
        if(args.length == 1){
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Number Game Server started");
        // create serverSocket to listen on
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try (BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))) {

                    System.out.println("Client Accepted");
                    // send initial prompt to client
                    sendMessageToClient(os, "Server Saying Hello"); // send initial message to client
                     lowerLimit = parseToInt(is.readLine());
                     upperLimit = parseToInt(is.readLine());
                     int clientGuess = parseToInt(is.readLine());
                     int numberOfTries = checkNumber(os,is, clientGuess);
                     sendMessageToClient(os, "true");
                     sendMessageToClient(os, "Congrats! You got it! It took you " + numberOfTries + " attempts");
                } catch (IOException e) {
                    System.out.println("IOException:" + e.getMessage());
                }
            } // end while true
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } // end catch

    }


    public static void sendMessageToClient(ObjectOutputStream os, Object msg) throws Exception {
        os.writeObject(msg);

        os.flush();
    }

    public static int parseToInt(String s) {
        int clientNumber = 0;
        try {
            clientNumber = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Unable to read client command");
        }
        return clientNumber;
    }

    public static int checkNumber(ObjectOutputStream os,BufferedReader is, int num){
       int randomNumber = ThreadLocalRandom.current().nextInt(lowerLimit, upperLimit);
        int numberOfTries = 1;
       try{
           while(randomNumber != num){
               if(num > randomNumber){
                   sendMessageToClient(os, "Lower than " + num);
                   numberOfTries++;
               } else{
                   sendMessageToClient(os, "Higher than " + num);
                   numberOfTries++;
               }
               num = Integer.parseInt(is.readLine());
           }
       }catch (Exception e){
           System.out.println(e);
       }
        return numberOfTries;
    }
}
