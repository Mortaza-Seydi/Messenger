package client.data;

public class User
{
    private int id;
    private String userName;
    private String name;
    private String profilePhotoUrl;
    private boolean isOnline;

    public User(int id, String userName, String name, String profilePhotoUrl, boolean isOnline)
    {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isOnline = isOnline;
    }

    public int getId()
    {
        return id;
    }

    public String getUserName()
    {
        return userName;
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
}
