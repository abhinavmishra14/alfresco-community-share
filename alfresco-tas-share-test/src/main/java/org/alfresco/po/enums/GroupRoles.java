package org.alfresco.po.enums;

public enum GroupRoles
{
    MANAGER("Manager"),
    COLLABORATOR("Collaborator"),
    CONTRIBUTOR("Contributor"),
    CONSUMER("Consumer");

    String value;
    GroupRoles(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
