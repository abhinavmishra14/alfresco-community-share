package org.alfresco.po.share.site.document;

import org.alfresco.po.share.enums.UserRole;
/**
 * Bean to hold the details of user.
 * 
 * @author Abhijeet Bharade
 * @version 1.7.0
 */
public class UserProfile
{
    private String fName;
    private String lName;
    private String username;
    private String password;
    private String emailId;

    private String groupName;

    private UserRole userRole;

    public UserRole getUserRole()
    {
        return userRole;
    }

    public void setUserRole(UserRole userRole)
    {
        this.userRole = userRole;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    /**
     * @return the fName
     */
    public String getfName()
    {
        return fName;
    }

    /**
     * @param fName the fName to set
     */
    public void setfName(String fName)
    {
        this.fName = fName;
    }

    /**
     * @return the lName
     */
    public String getlName()
    {
        return lName;
    }

    /**
     * @param lName the lName to set
     */
    public void setlName(String lName)
    {
        this.lName = lName;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the emailId
     */
    public String getEmailId()
    {
        return emailId;
    }

    /**
     * @param emailId the emailId to set
     */
    public void setEmailId(String emailId)
    {
        this.emailId = emailId;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("UserProfile [fName=");
        builder.append(fName);
        builder.append(", lName=");
        builder.append(lName);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append(", emailId=");
        builder.append(emailId);
        builder.append("]");
        return builder.toString();
    }

}
