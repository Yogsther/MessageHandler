import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
                                    System.out.println("Updated!");
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
            new Command("save (path/file)", new Command.CommandCallback() {
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
                    try {
                        Files.write(Paths.get(input.length() > 0 ? input : "save.json"), save.toJSONString().getBytes());
                        System.out.println("Saved!");
                    } catch (IOException e) {
                        System.out.println("Please enter a valid path or nothing at all.");
                    }
                }
            }),
            new Command("load [path/file]", new Command.CommandCallback() {
                @Override
                public void call(String input) throws WrongFormatException {
                    try {
                        FileReader reader = new FileReader(input);
                        JSONParser parser = new org.json.simple.parser.JSONParser();
                        JSONArray save = (JSONArray) parser.parse(reader);

                        for(Object savedMessage : save){
                            JSONObject message = (JSONObject) savedMessage;
                            messages.add(new Message((String) message.get("message"), new Date((long) message.get("created")), new Date((long) message.get("updated")), (String) message.get("author"), messages.size()));
                            for(Object updateSaved : (JSONArray) message.get("updates")){
                                JSONObject update = (JSONObject) updateSaved;
                                messages.get(messages.size()-1).updateBackLog((String) update.get("previous_message"), (String) update.get("author"), new Date((long) update.get("created")));
                            }
                        }

                        System.out.println("Loaded " + save.size() + " messages!");
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found");
                    } catch (ParseException | IOException e) {
                        System.out.println("Could not read file");
                    } catch (StringTooLongException e) {
                        System.out.println("Message length is too long");
                    }

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
