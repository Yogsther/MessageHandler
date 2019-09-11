import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class main {

    private static ArrayList<Message> messages;
    private static Scanner scanner;
    private static Command[] commands = {
            new Command("new [message text]", new Command.CommandCallback() {
                @Override
                public void call(String input) {
                    try {
                        newMessage(input);
                    } catch (StringTooLongException e) {
                        System.out.println("Error: Message length is too long");
                    }
                }
            }, "Create a new message"),
            new Command("view", new Command.CommandCallback() {
                @Override
                public void call(String input) {
                    for(Message message: messages){
                        System.out.println(message.toString());
                    }
                }
            }, "View all messages in memory"),
            new Command("update [messag id] [new message]", new Command.CommandCallback() {
                @Override
                public void call(String input) {
                    System.out.println("INPUT GOT:" + input);
                }
            })
    };

    public static void main(String[] args) throws StringTooLongException {

        messages = new ArrayList<Message>();
        scanner = new Scanner(System.in);

        System.out.println("Welcome to Message creator, here are the commands");
        for(Command command : commands){
            System.out.println(command.getFullCommand() + "\n     " + command.getDescription());
        }

        System.out.println("Please enter a command: ");
        awaitInput();
    }

    private static void awaitInput(){
        System.out.print(": ");
        String input = scanner.nextLine();

        int first_space = (input.contains(" ") ? input.indexOf(" "): input.length());
        String input_command = input.substring(0, first_space);
        String input_value = input.substring(first_space);

        boolean command_found = false;
        for(Command command : commands){
            if(command.getCommand().equals(input_command)){
                command_found = true;
                command.call(input_value);
                break;
            }
        }
        if(!command_found) System.out.println("Command " + input_command + " is not recognized");
        awaitInput();
    }

    public static void newMessage(String message) throws StringTooLongException {
        messages.add(new Message(message, message.length()));
        System.out.println("Created message!");
    }
}
