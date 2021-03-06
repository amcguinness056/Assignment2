import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGameServerMultiThreaded extends Thread {
    private final Socket clientSocket;

    NumberGameServerMultiThreaded(Socket socket){
        this.clientSocket= socket;
    }

    @Override
    public void run(){
        try (BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))) {

            System.out.println("Client Accepted");// send initial prompt to client
            sendMessageToClient(os, "Server Saying Hello"); // send initial message to client private static int lowerLimit;int upperLimit;
            int lowerLimit = parseToInt(is.readLine()); //read lowerLimit from client
            int upperLimit = parseToInt(is.readLine()); //read upperLimit from client
            int clientGuess = parseToInt(is.readLine()); //read guess from client
            int numberOfTries = checkNumber(os,is, clientGuess, lowerLimit, upperLimit); //check client guess against random number
            sendMessageToClient(os, "true"); //send message to client to inform that number has been guessed correctly
            sendMessageToClient(os, "Congrats! You got it! It took you " + numberOfTries + " attempts"); //send message to client informing how many attemps taken
        } catch (Exception e) {
            System.out.println("IOException:" + e.getMessage());
        }
    }


    public static void main(String[] args){
        int port = 4444; // specify port
        //check if port is provided in arguments
        if(args.length == 1){
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Number Game Server started");
        // create serverSocket to listen on
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Multi Threaded Server Waiting");
                Socket clientSocket = serverSocket.accept(); //accept client
                System.out.println("Client Accepted from " + clientSocket.getInetAddress());
                // spawn a new thread to handle client
                NumberGameServerMultiThreaded numberGameServerMultiThreaded = new NumberGameServerMultiThreaded(clientSocket);
                System.out.println("About to start new thread");
                numberGameServerMultiThreaded.start();
            } // end while true
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } // end catch
    } // end main

    //helper method to send messages to client
    public static void sendMessageToClient(ObjectOutputStream os, Object msg) throws Exception {
        os.writeObject(msg);
        os.flush();
    }

    //helper method to parse strings to integeres
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
    public static int checkNumber(ObjectOutputStream os,BufferedReader is, int num, int lowerLimit, int upperLimit){
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
