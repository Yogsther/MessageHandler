import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String message, author;
    private Date createdAt, updatedAt;
    ArrayList<Update> updates;

    class Update{
        private String author, previousMessage;
        private Date updatedAt;
        public Update(String author, String previousMessage){
            this.author = author;
            this.previousMessage = previousMessage;
            this.updatedAt = new Date();
        }

        public String getPreviousMessage(){
            return this.previousMessage;
        }

        public String getAuthor(){
            return this.author;
        }

        public Date getDate(){
            return this.updatedAt;
        }
    }

    /**
     * Creates a new message
     * @param message
     */
    public Message(String message){
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.message = message;
        this.author = System.getProperty("user.name");

        updates = new ArrayList<Update>();
    }

    /**
     * Updates the message content
     * @param message New message
     */
    public void update(String message){
        String newAuthor = System.getProperty("user.name");
        this.updates.add(new Update(author, this.message));
        this.message = message;
        this.author = author;
    }

    @Override
    public String toString(){
        String build = this.author + ": " + this.message + " CREATED: " + this.createdAt.toString();
        if(updates.size() > 0) build += "\nORIGINAL BY  " + this.author + ": " + updates.get(0).getPreviousMessage() + " CREATED: " + this.createdAt.toString();
        for(int i = 0; i < updates.size(); i++){
            Update update = updates.get(i);
            build += "\nREVISION BY  " + update.getAuthor() + ": " + (updates.size()-1 == i ? this.message : update.getPreviousMessage()) + " UPDATED: " + update.getDate();
        }
        return  build;
    }
}
