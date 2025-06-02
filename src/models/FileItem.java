package models;

import java.io.Serializable;

public class FileItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String content;
    private String permission; // "r", "w", "rw"

    public FileItem(String name) {
        this.name = name;
        this.content = "";
        this.permission = "rw";
    }

    public FileItem(String name, String content) {
        this.name = name;
        this.content = content;
        this.permission = "rw";
    }

    public String getName() { return name; }
    public String getContent() { return content; }
    public String getPermission() { return permission; }

    public void setName(String name) { this.name = name; }
    public void setContent(String content) { this.content = content; }
    public void setPermission(String permission) { this.permission = permission; }

    public boolean canRead() {
        return permission.contains("r");
    }

    public boolean canWrite() {
        return permission.contains("w");
    }
}
