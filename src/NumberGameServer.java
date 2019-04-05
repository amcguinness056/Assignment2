import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGameServer {
    private static int lowerLimit; //global variable for holding lower limit of number range
    private static int upperLimit; //global variable for holding upper limit of number range
    public static void main(String [] args){
        int port = 4444; // assign port number
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

                    System.out.println("Client Accepted");// send initial prompt to client
                    sendMessageToClient(os, "Server Saying Hello"); // send initial message to client
                     lowerLimit = parseToInt(is.readLine()); //read lowerLimit from client
                     upperLimit = parseToInt(is.readLine()); //read upperLimit from client
                     int clientGuess = parseToInt(is.readLine()); //read first guess from client
                     int numberOfTries = checkNumber(os,is, clientGuess); //check clients guess until random number is guessed and return attempts taken
                     sendMessageToClient(os, "true"); //send message to client to inform that the client guessed number correctly
                     sendMessageToClient(os, "Congrats! You got it! It took you " + numberOfTries + " attempts"); //send message to client to inform how many attempts taken
                } catch (IOException e) {
                    System.out.println("IOException:" + e.getMessage());
                }
            } // end while true
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } // end catch

    }

    //helper method to send messages to client
    public static void sendMessageToClient(ObjectOutputStream os, Object msg) throws Exception {
        os.writeObject(msg);

        os.flush();
    }

    //helper method to parse strings to integers
    public static int parseToInt(String s) {
        int clientNumber = 0;
        try {
            clientNumber = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Unable to read client command");
        }
        return clientNumber;
    }

    //method that checks the clients guess and loops until clients guess equals random number
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
