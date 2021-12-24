package me.happybavarian07.main;

public enum MessageType {

    PLUGIN("§r[§5Plugin§r] §a", true, "ca.admin.messagelevel.plugin"),
    SERVER("§7[Server] §4", true, "ca.admin.messagelevel.server"),
    ERROR("§r§l[§4§lERROR§r§l] §c§l", true, "ca.admin.messagelevel.error"),
    FINE("§r[§aFine§r] §a", true, "ca.admin.messagelevel.fine");

    private String prefix;
    private boolean requiresPermission;
    private String requiredPermission;

    MessageType(String prefix, boolean requiresPermission, String requiredPermission) {
        this.prefix = prefix;
        this.requiresPermission = requiresPermission;
        this.requiredPermission = requiredPermission;
    }

    public boolean requiresPermission() { return requiresPermission; }

    public String getPrefix() { return prefix; }

    public String getRequiredPermission() { return requiredPermission; }

    private void setPrefix(String prefix) { this.prefix = prefix; }

    private void setRequiresPermission(boolean requiresPermission) { this.requiresPermission = requiresPermission; }

    private void setRequiredPermission(String requiredPermission) { this.requiredPermission = requiredPermission; }
}
