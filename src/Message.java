import java.util.ArrayList;
import java.util.Date;

public class Message {
    private final int MESSAGE_LENGTH = 140;
    private int id;
    private String message, author;
    private Date createdAt, updatedAt;
    private ArrayList<Update> updates;

    /* Update class to keep track of the history of a message */
    class Update {
        private String author, previousMessage;
        private Date updatedAt;

        /**
         * Initiate a new Update
         *
         * @param author          Author of the update
         * @param previousMessage The current message (! Not the new message, that is handled in update())
         */
        public Update(String author, String previousMessage) {
            this.author = author;
            this.previousMessage = previousMessage;
            this.updatedAt = new Date();
        }

        public Update(String author, String previousMessage, Date time) {
            this.author = author;
            this.previousMessage = previousMessage;
            this.updatedAt = time;
        }

        /**
         * Get prior message before this update
         *
         * @return String of only the message content
         */
        public String getPreviousMessage() {
            return this.previousMessage;
        }

        /**
         * Get author of this update
         *
         * @return Author username
         */
        public String getAuthor() {
            return this.author;
        }

        /**
         * Get the date of when this update was made
         *
         * @return
         */
        public Date getDate() {
            return this.updatedAt;
        }
    }

    /**
     * Create a new message
     *
     * @param message The contents of the message
     * @param id      ID of the message
     * @throws StringTooLongException If the message is longer than MESSAGE_LENGTH (140)
     */
    public Message(String message, int id) throws StringTooLongException {
        if (message.length() > MESSAGE_LENGTH)
            throw new StringTooLongException();

        this.id = id;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.message = message;
        this.author = System.getProperty("user.name");

        updates = new ArrayList<Update>();
    }

    public Message(String message, Date created, Date updated, String author, int id) throws StringTooLongException {
        if (message.length() > MESSAGE_LENGTH)
            throw new StringTooLongException();

        this.id = id;
        this.createdAt = created;
        this.updatedAt = updated;
        this.message = message;
        this.author = author;
        updates = new ArrayList<Update>();
    }

    /**
     * Updates the message content
     *
     * @param message New message
     */
    public void update(String message) throws StringTooLongException {
        if (message.length() > MESSAGE_LENGTH)
            throw new StringTooLongException();
        this.updates.add(new Update(author, this.message));
        this.message = message;
    }

    /**
     * Only used to load old messages, overwrites time and author for each edit
     * @param message
     * @param author
     * @param time
     * @throws StringTooLongException
     */
    public void updateBackLog(String message, String author, Date time) throws StringTooLongException {
        if (message.length() > MESSAGE_LENGTH)
            throw new StringTooLongException();
        this.updates.add(new Update(author, this.message, time));
    }

    /**
     * Get ID of message
     *
     * @return returns the ID's message
     */
    public int getId() {
        return this.id;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getMessage(){
        return this.message;
    }

    public Date getCreatedAt(){
        return this.createdAt;
    }

    public Date getUpdatedAt(){
        return this.updatedAt;
    }

    public ArrayList<Update> getUpdates(){
        return this.updates;
    }

    /**
     * Get the entire message, author and date + all revisions of the message-content.
     *
     * @return Message and all it's history in a String
     */
    @Override
    public String toString() {
        String build = "[ID: " + this.id + "] " + this.author + ": " + this.message + " CREATED: " + this.createdAt.toString();
        if (updates.size() > 0)
            build += "\n↪ ORIGINAL BY " + this.author + ": " + updates.get(0).getPreviousMessage() + " CREATED: " + this.createdAt.toString();
        for (int i = 0; i < updates.size(); i++) {
            Update update = updates.get(i);
            build += "\n    > REVISION BY " + update.getAuthor() + ": " + (updates.size() - 1 == i ? this.message : update.getPreviousMessage()) + " UPDATED: " + update.getDate();
        }
        return build;
    }
}
