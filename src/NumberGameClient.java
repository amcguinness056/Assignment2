import java.io.*;
import java.net.Socket;


public class NumberGameClient {
    private static int lowerLimit; //global variable for holding lower limit of number range
    private static int upperLimit; //global variable for holding upper limit of number range

    public static void main(String[] args) {
        String hostName = "localhost"; // default host name
        int hostPort = 4444; // default host port


        // assign host machine name and port to connect tos
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

            //Get lower range limit from user
            System.out.print("Enter Lower Limit ");
            lowerLimit = Integer.parseInt(userInput.readLine());
            //check the number is positive
           while(!checkLimitNumber(lowerLimit)){
               System.out.print("Enter a positive number ");
               lowerLimit = Integer.parseInt(userInput.readLine());
           }
           //send lowerlimit to server
           sendMessageToServer(os, Integer.toString(lowerLimit));
           //get upper limit from user
            System.out.print("Enter Upper Limit ");
           upperLimit = Integer.parseInt(userInput.readLine());
           //check the number is positive
           while (!checkLimitNumber(upperLimit)){
               System.out.print("Enter a positive number ");
               upperLimit = Integer.parseInt(userInput.readLine());
           }
           //send upperLimit to server
            sendMessageToServer(os, Integer.toString(upperLimit));

           //ask user for their guess
           System.out.println("Enter Guess");
           int clientNumber = Integer.parseInt(userInput.readLine());
           //check the number guessed is between the limits specified previously before sending to server
           while(!checkUserGuess(clientNumber)){
               System.out.println("Please enter a number between the limits " + lowerLimit + " and " + upperLimit);
               clientNumber = Integer.parseInt(userInput.readLine());
           }
           //send first guess to server
           sendMessageToServer(os, Integer.toString(clientNumber));

           //loop until guess equals number
            do{
                //read response froms server
                String response = is.readObject().toString();

                //if response equals true then guess is correct so exit loop
                if(response.equals("true")){
                    break;
                } else{
                    //print server response
                    System.out.println(response);
                    //enter next guess
                    int clientGuess = Integer.parseInt(userInput.readLine());
                    //check if guess is between limits
                    if(!checkUserGuess(clientGuess)){
                        System.out.println("Please guess a NUMBER between the limit " + lowerLimit + " and " + upperLimit);
                    }
                    else {
                        //send next guess to server
                        sendMessageToServer(os, Integer.toString(clientGuess));
                    }
                }
            } while (true);
            //read response from server
            System.out.println(is.readObject());
        } catch (NumberFormatException e) {
            System.err.println("Only whole integeres are accepted");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //helper function to send messages to the server
    public static void sendMessageToServer(PrintWriter os, String msg)
    {
        os.println(msg);
        os.flush();
    }

    //checks if limit number is positive
    public static boolean checkLimitNumber(int number){
        int limit = number;
        boolean response;
        if(limit > 0){
            response = true;
        } else {
            response = false;
        }
        return response;
    }

    //checks if users guessed number is between limits
    public static boolean checkUserGuess(int number){
        int guess = number;
        boolean response;
        if(guess < lowerLimit || guess > upperLimit ){
            response = false;
        }
        else{
            response = true;
        }
        return response;
    }

//    public static boolean isNumeric(String number){
//        try{
//            Integer.parseInt(number);
//            return true;
//        }catch (NumberFormatException e){
//            return false;
//        }
//    }

}
