import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class NumberGameClient {
    static int lowerLimit;
    static int upperLimit;

    public static void main(String[] args) {
        String hostName = "localhost"; // default host name
        int hostPort = 4444; // default host port


        // assign host machine name and port to connect to
        if (args.length != 0) {
            if (args[0] != null) {
                hostName = args[0]; // user specified machine
            }
            if (args[1] != null) {
                hostPort = Integer.parseInt(args[1]); // user specified port
            }
        }

        System.out.println("Tyring to Connect to Number Game Server");
        // connect to server and extract input and output streams
        try (Socket serverSocket = new Socket(hostName, hostPort);
             PrintWriter os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())));
             ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()))) {

            // read and display opening message from server
            System.out.println("Server: " + is.readObject());

            // create client input stream
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter Lower Limit ");
            lowerLimit = Integer.parseInt(userInput.readLine());
           while(!checkLimitNumber(lowerLimit)){
               System.out.print("Enter a positive number ");
               lowerLimit = Integer.parseInt(userInput.readLine());
           }
           sendMessageToServer(os, Integer.toString(lowerLimit));
            System.out.print("Enter Upper Limit ");
           upperLimit = Integer.parseInt(userInput.readLine());
           while (!checkLimitNumber(upperLimit)){
               System.out.print("Enter a positive number ");
               upperLimit = Integer.parseInt(userInput.readLine());
           }
            sendMessageToServer(os, Integer.toString(upperLimit));

           System.out.println("Enter Guess");
           int clientNumber = Integer.parseInt(userInput.readLine());
           while(!checkUserGuess(clientNumber)){
               System.out.println("Please enter a number between the limits " + lowerLimit + " and " + upperLimit);
               clientNumber = Integer.parseInt(userInput.readLine());
           }
           sendMessageToServer(os, Integer.toString(clientNumber));

            do{
                String response = is.readObject().toString();
                if(response.equals("true")){
                    break;
                } else{
                    System.out.println(response);
                    int clientGuess = Integer.parseInt(userInput.readLine());
                    if(!checkUserGuess(clientGuess)){
                        System.out.println("Please guess a number between the limit " + lowerLimit + " and " + upperLimit);
                    }
                    else {
                        sendMessageToServer(os, Integer.toString(clientGuess));
                    }
                }
            } while (true);
            System.out.println(is.readObject());
        } catch (Exception e) {
            System.err.println("Exception:  " + e.getMessage());
        }
    }

    public static void sendMessageToServer(PrintWriter os, String msg)
    {
        os.println(msg);
        os.flush();
    }

    public static boolean checkLimitNumber(int number){
        boolean response;
        if(number > 0){
            response = true;
        } else {
            response = false;
        }
        return response;
    }

    public static boolean checkUserGuess(int number){
        boolean response;
        if(number < lowerLimit || number > upperLimit ){
            response = false;
        }
        else{
            response = true;
        }
        return response;
    }

//    public static void handleResponse(StudentDetailsType sDetails) {
//        // attempt to read the StudentDetailsType object
//        if (sDetails != null) {
//            System.out.println("Server: " + formatStudentDetails(sDetails));
//        } else {
//            System.out.println("Server: Invalid Student Number\n");
//        }
//    }

//    public static String formatStudentDetails(StudentDetailsType s) {
//        StringBuilder b = new StringBuilder();
//        b.append("STUDENT NAME: " + s.getStudentName()+"\n");
//        b.append(String.format("\tMark1 = " + "%.2f\n", s.getResult1()));
//        b.append(String.format("\tMark2 = " + "%.2f\n", s.getResult2()));
//        b.append(String.format("\tMark3 = " + "%.2f\n\n", s.getResult3()));
//        return b.toString();
//    }

}
