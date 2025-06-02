package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Directory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Map<String, FileItem> files = new HashMap<>();
    private Map<String, Directory> directories = new HashMap<>();

    public Directory(String name) {
        this.name = name;
    }

    public void addFile(FileItem file) {
        files.put(file.getName(), file);
    }

    public void removeFile(String name) {
        files.remove(name);
    }

    public void addDirectory(Directory dir) {
        directories.put(dir.getName(), dir);
    }

    public void removeDirectory(String name) {
        directories.remove(name);
    }

    public void listContents() {
        System.out.println("Conteúdo de: " + name);
        if (files.isEmpty() && directories.isEmpty()) {
            System.out.println("  (vazio)");
            return;
        }

        for (String dirName : directories.keySet()) {
            System.out.println("  [DIR] " + dirName);
        }

        for (String fileName : files.keySet()) {
            System.out.println("  [ARQ] " + fileName);
        }
    }

    public void exibirTree(String prefixo) {
        System.out.println(prefixo + name + "/");

        for (String nomeDir : directories.keySet()) {
            directories.get(nomeDir).exibirTree(prefixo + "│   ");
        }

        for (String nomeArq : files.keySet()) {
            System.out.println(prefixo + "│   " + nomeArq);
        }
    }

    public FileItem getFile(String name) {
        return files.get(name);
    }

    public Directory getSubDirectory(String name) {
        return directories.get(name);
    }

    public Map<String, FileItem> getAllFiles() {
        return files;
    }

    public Map<String, Directory> getAllDirectories() {
        return directories;
    }

    public String getName() {
        return name;
    }

    public Directory getDirectoryFromPath(String path) {
        if (path.equals("") || path.equals("/")) return this;

        String[] parts = path.split("/");
        Directory current = this;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            current = current.getSubDirectory(part);
            if (current == null) return null;
        }

        return current;
    }
}
