package client.data;

public class GroupAndChannel
{
    private int id;
    private String stringId;
    private String name;
    private String profilePhotoUrl;
    private String createdTime;
    private long members;
    private String type;

    public GroupAndChannel(int id, String stringId, String name, String profilePhotoUrl, String createdTime, long members, String type)
    {
        this.id = id;
        this.stringId = stringId;
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.createdTime = createdTime;
        this.members = members;
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public String getStringId()
    {
        return stringId;
    }

    public String getName()
    {
        return name;
    }

    public String getProfilePhotoUrl()
    {
        return profilePhotoUrl;
    }

    public String getCreatedTime()
    {
        return createdTime;
    }

    public long getMembers()
    {
        return members;
    }

    public String getType()
    {
        return type;
    }
}
