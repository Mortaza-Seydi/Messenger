package client.data;

public class SuperUser
{
    protected int id;
    private String phone;
    protected String userName;
    private String password;
    protected String name;
    protected String profilePhotoUrl;
    protected boolean isOnline;
    protected boolean isReported;
    protected String createdTime;
    protected String lastSeen;

    public SuperUser(int id, String phone, String userName, String password, String name, String profilePhotoUrl, boolean isOnline, boolean isReported, String createdTime, String lastSeen)
    {
        this.id = id;
        this.phone = phone;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isOnline = isOnline;
        this.isReported = isReported;
        this.createdTime = createdTime;
        this.lastSeen = lastSeen;
    }

    public int getId()
    {
        return id;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public String getName()
    {
        return name;
    }

    public String getProfilePhotoUrl()
    {
        return profilePhotoUrl;
    }

    public boolean isOnline()
    {
        return isOnline;
    }

    public boolean isReported()
    {
        return isReported;
    }

    public String getCreatedTime()
    {
        return createdTime;
    }

    public String getLastSeen()
    {
        return lastSeen;
    }
}