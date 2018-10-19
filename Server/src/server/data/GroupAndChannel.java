package server.data;

public class GroupAndChannel
{
    private int id;
    private int adminId;
    private String stringId;
    private String name;
    private String profilePhotoUrl;
    private String createdTime;
    private long members;

    public GroupAndChannel(int id, int adminId, String stringId, String name, String profilePhotoUrl, String createdTime)
    {
        this.id = id;
        this.adminId = adminId;
        this.stringId = stringId;
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.createdTime = createdTime;
    }

    public int getId()
    {
        return id;
    }

    public int getAdminId()
    {
        return adminId;
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

    public void setMembers(long members)
    {
        this.members = members;
    }
}
