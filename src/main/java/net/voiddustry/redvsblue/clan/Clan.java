package net.voiddustry.redvsblue.clan;

public class Clan {

    private final String name;
    private final String ownerUuid;

    public Clan(String name, String owner_uuid) {
        this.name = name;
        this.ownerUuid = owner_uuid;
    }

    public String getOwner() {
        return ownerUuid;
    }

}
