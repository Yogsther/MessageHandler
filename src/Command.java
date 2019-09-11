public class Command {
    private CommandCallback callback;
    private String command, description;

    interface CommandCallback {
        void call(String input);
    }

    public Command(String command, CommandCallback callback, String description){
        this.command = command;
        this.callback = callback;
        this.description = description;
    }

    public Command(String command, CommandCallback callback){
        this.command = command;
        this.callback = callback;
        this.description = "This command is missing a description";
    }

    public void call(String input){
        this.callback.call(input);
    }

    public String getFullCommand(){
        return this.command;
    }

    public String getDescription(){
        return this.description;
    }

    public String getCommand(){
        return this.command.substring(0, this.command.contains(" ") ? this.command.indexOf(" ") : this.command.length());
    }
}
