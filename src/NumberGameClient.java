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
            lowerLimit = checkUserInput(userInput.readLine(), userInput);
            //check the number is positive
           while(!checkLimitNumber(lowerLimit)){
               System.out.print("Enter a positive number ");
               lowerLimit = checkUserInput(userInput.readLine(), userInput);
           }
           //send lowerlimit to server
           sendMessageToServer(os, Integer.toString(lowerLimit));
           //get upper limit from user
            System.out.print("Enter Upper Limit ");
            String upperLimitString = userInput.readLine();
            upperLimit = checkUserInput(upperLimitString, userInput);
           //check the number is positive
           while (!checkLimitNumber(upperLimit) || !checkAboveLowerLimit(upperLimit)){
               System.out.print("Enter a positive number greater than lower limit " + lowerLimit + ": ");
               upperLimit = checkUserInput(userInput.readLine(), userInput);
           }
           //send upperLimit to server
            sendMessageToServer(os, Integer.toString(upperLimit));

           //ask user for their guess
           System.out.println("Enter Guess");
           String clientGuessString = userInput.readLine();
           int clientNumber = checkUserInput(clientGuessString, userInput);
           //check the number guessed is between the limits specified previously before sending to server
           while(!checkUserGuessBetweenLimits(clientNumber)){
               System.out.println("Please enter a number between the limits " + lowerLimit + " and " + upperLimit);
               clientNumber = checkUserInput(userInput.readLine(), userInput);
           }
           //send first guess to server
           sendMessageToServer(os, Integer.toString(clientNumber));

           //loop until guess equals number
            do{
                //read response from server
                String response = is.readObject().toString();

                //if response equals true then guess is correct so exit loop
                if(response.equals("true")){
                    break;
                } else{
                    //print server response
                    System.out.println(response);
                    //enter next guess
                    int clientGuess = checkUserInput(userInput.readLine(), userInput);
                    //check if guess is between limits
                    if(!checkUserGuessBetweenLimits(clientGuess)){
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
    public static boolean checkLimitNumber(int limit){
        boolean response;
        if(limit > 0){
            response = true;
        } else {
            response = false;
        }
        return response;
    }
    public static boolean checkAboveLowerLimit(int number){
        if(number < lowerLimit){
            return false;
        }
        else{
            return true;
        }
    }

    //checks if users guessed number is between limits
    public static boolean checkUserGuessBetweenLimits(int guess){
        boolean response;
        if(guess < lowerLimit || guess > upperLimit ){
            response = false;
        }
        else{
            response = true;
        }
        return response;
    }
    public static int checkUserInput(String input, BufferedReader userInput){
        while(!isNumeric(input)){
            try{
                System.out.println("Please enter a whole number");
                input = userInput.readLine();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return Integer.parseInt(input);
    }

    public static boolean isNumeric(String number){
        try{
            Integer.parseInt(number);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

}
