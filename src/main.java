import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class main {

    /* List of all messages in memory */
    private static ArrayList<Message> messages;
    /* Scanner to read input from the user */
    private static Scanner scanner;
    /* All commands */
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

            new Command("view (message id)", new Command.CommandCallback() {
                @Override
                public void call(String input) {
                    int id = -1;
                    if (input.length() > 0) {
                        try {
                            id = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            System.out.println("ID must be an int");
                            return;
                        }
                    }

                    for (Message message : messages) {
                        if (id == message.getId() || id == -1) System.out.println(message.toString());
                    }
                }
            }, "View all messages in memory"),
            new Command("update [message id] [new message]", new Command.CommandCallback() {
                @Override
                public void call(String input) throws WrongFormatException {
                    if (input.length() > 3 && input.contains(" ")) {
                        try {
                            int id = Integer.parseInt(input.substring(0, input.indexOf(" ")));
                            String newMessage = input.substring(input.indexOf(" ") + 1);
                            for (Message message : messages) {
                                if (message.getId() == id) {
                                    message.update(newMessage);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("ID must be an int");
                        } catch (StringTooLongException e) {
                            System.out.println("Message length is too long!");
                        }
                    } else {
                        throw new WrongFormatException();
                    }
                }
            }, "Update an existing message"),
            new Command("save (path)", new Command.CommandCallback() {
                @Override
                public void call(String input) throws WrongFormatException {
                    JSONArray save = new JSONArray();
                    for(Message message : messages){
                        JSONObject messageSaved = new JSONObject();
                        messageSaved.put("message", message.getMessage());
                        messageSaved.put("author", message.getAuthor());
                        messageSaved.put("created", message.getCreatedAt().getTime());
                        messageSaved.put("updated", message.getUpdatedAt().getTime());
                        JSONArray updates = new JSONArray();
                        for(Message.Update update : message.getUpdates()){
                            JSONObject updateSaved = new JSONObject();
                            updateSaved.put("author", update.getAuthor());
                            updateSaved.put("previous_message", update.getPreviousMessage());
                            updateSaved.put("created", update.getDate().getTime());
                            updates.add(updateSaved);
                        }
                        messageSaved.put("updates", updates);
                        save.add(messageSaved);
                    }
                    System.out.println(save.toString());
                }
            }),
            new Command("exit", new Command.CommandCallback() {
                @Override
                public void call(String input) throws WrongFormatException {
                    System.exit(0);
                }
            }, "Terminates the application")
    };

    public static void main(String[] args) throws StringTooLongException {

        messages = new ArrayList<Message>();
        scanner = new Scanner(System.in);

        System.out.println("Welcome to Message creator, here are the commands. [required] (optional)");
        for (Command command : commands) {
            System.out.println(command.getFullCommand() + "\n      " + command.getDescription());
        }

        System.out.println("Please enter a command: ");
        awaitInput();
    }

    private static void awaitInput() {
        System.out.print(": ");
        String input = scanner.nextLine();

        int first_space = (input.contains(" ") ? input.indexOf(" ") : input.length());
        String input_command = input.substring(0, first_space);
        String input_value = input.substring(input.length() >= first_space + 1 ? first_space + 1 : first_space);

        boolean command_found = false;
        for (Command command : commands) {
            if (command.getCommand().equals(input_command)) {
                command_found = true;
                try {
                    command.call(input_value);
                } catch (WrongFormatException e) {
                    System.out.println("Wrong format: " + command.getFullCommand());
                }
                break;
            }
        }
        if (!command_found) System.out.println("Command '" + input_command + "' is not recognized");
        awaitInput();
    }

    public static void newMessage(String message) throws StringTooLongException {
        messages.add(new Message(message, messages.size()));
        System.out.println("Created message!");
    }

}
