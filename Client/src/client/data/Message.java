package client.data;

public class Message
{
    private int id;
    private int conversationId;
    private int senderId;
    private String message;
    private String attachUrl;
    private String createdAt;
    private String editedAt;

    public Message(int id, int conversationId, int senderId, String message, String attachUrl, String createdAt, String editedAt)
    {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.message = message;
        this.attachUrl = attachUrl;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }

    public int getId()
    {
        return id;
    }

    public int getConversationId()
    {
        return conversationId;
    }

    public int getSenderId()
    {
        return senderId;
    }

    public String getMessage()
    {
        return message;
    }

    public String getAttachUrl()
    {
        return attachUrl;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public String getEditedAt()
    {
        return editedAt;
    }
}
