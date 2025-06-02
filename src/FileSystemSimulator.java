// FileSystemSimulator.java

import java.io.*;
import java.util.Scanner;
import models.*;

public class FileSystemSimulator {

    private static Directory root = new Directory("root");
    private static Journal journal = new Journal();

    public static void main(String[] args) {
        journal.carregarDeArquivo();
        carregarSistemaDeArquivos();
        journal.iniciarSessao();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Simulador de Sistema de Arquivos - Digite comandos. Use 'exit' para sair.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                salvarSistemaDeArquivos();
                break;
            }

            String[] parts = input.split(" ");
            String comando = parts[0];

            switch (comando) {
                case "mkdir":
                    if (parts.length < 2) {
                        break;
                    }
                    Directory pai = navegarAteDiretorio(root, parts[1]);
                    if (pai == null) {
                        break;
                    }
                    pai.addDirectory(new Directory(obterNomeFinal(parts[1])));
                    journal.registrarOperacao("[MKDIR] " + parts[1]);
                    break;

                case "touch":
                    if (parts.length < 2) {
                        break;
                    }
                    Directory paiTouch = navegarAteDiretorio(root, parts[1]);
                    if (paiTouch == null) {
                        break;
                    }
                    String nomeArq = obterNomeFinal(parts[1]);
                    String conteudo = input.contains("\"")
                            ? input.substring(input.indexOf('"') + 1, input.lastIndexOf('"')) : "";
                    paiTouch.addFile(new FileItem(nomeArq, conteudo));
                    journal.registrarOperacao("[CREATE] " + parts[1] + " \"" + conteudo + "\"");
                    break;

                case "ls":
                    Directory dirLS = parts.length > 1 ? root.getDirectoryFromPath(parts[1]) : root;
                    if (dirLS != null) {
                        dirLS.listContents();
                    }
                    break;

                case "rm":
                    if (parts.length < 2) {
                        break;
                    }
                    Directory dirRM = navegarAteDiretorio(root, parts[1]);
                    if (dirRM == null) {
                        break;
                    }
                    FileItem arquivoRemover = dirRM.getFile(obterNomeFinal(parts[1]));
                    if (arquivoRemover != null && !arquivoRemover.canWrite()) {
                        System.out.println("Permissão negada para remoção (sem 'w').");
                        break;
                    }
                    dirRM.removeFile(obterNomeFinal(parts[1]));
                    journal.registrarOperacao("[DELETE] " + parts[1]);
                    break;

                case "rmdir":
                    if (parts.length < 2) {
                        break;
                    }
                    Directory dirPaiRmdir = navegarAteDiretorio(root, parts[1]);
                    if (dirPaiRmdir == null) {
                        break;
                    }
                    dirPaiRmdir.removeDirectory(obterNomeFinal(parts[1]));
                    journal.registrarOperacao("[RMDIR] " + parts[1]);
                    break;

                case "rename":
                    if (parts.length < 3) {
                        break;
                    }
                    Directory dirPai = navegarAteDiretorio(root, parts[1]);
                    if (dirPai == null) {
                        break;
                    }
                    String nomeAntigo = obterNomeFinal(parts[1]);
                    String novoNome = parts[2];
                    FileItem arq = dirPai.getFile(nomeAntigo);
                    if (arq != null) {
                        arq.setName(novoNome);
                        dirPai.removeFile(nomeAntigo);
                        dirPai.addFile(arq);
                        journal.registrarOperacao("[RENAME] " + parts[1] + " to " + novoNome);
                        break;
                    }
                    Directory dirAntigo = dirPai.getSubDirectory(nomeAntigo);
                    if (dirAntigo != null) {
                        Directory dirNovo = new Directory(novoNome);
                        for (String nomeArquivo : dirAntigo.getAllFiles().keySet()) {
                            dirNovo.addFile(dirAntigo.getFile(nomeArquivo));
                        }
                        for (String nomeSub : dirAntigo.getAllDirectories().keySet()) {
                            dirNovo.addDirectory(dirAntigo.getSubDirectory(nomeSub));
                        }
                        dirPai.removeDirectory(nomeAntigo);
                        dirPai.addDirectory(dirNovo);
                        journal.registrarOperacao("[RENAME] dir " + parts[1] + " to " + novoNome);
                    }
                    break;

                case "cp":
                    if (parts.length < 3) {
                        break;
                    }
                    Directory dirOrigem = navegarAteDiretorio(root, parts[1]);
                    Directory dirDestino = navegarAteDiretorio(root, parts[2]);
                    if (dirOrigem == null || dirDestino == null) {
                        break;
                    }
                    String nomeOrigem = obterNomeFinal(parts[1]);
                    String nomeDestino = obterNomeFinal(parts[2]);
                    FileItem arquivoOrigem = dirOrigem.getFile(nomeOrigem);
                    if (arquivoOrigem != null) {
                        FileItem arquivoCopia = new FileItem(nomeDestino, arquivoOrigem.getContent());
                        dirDestino.addFile(arquivoCopia);
                        journal.registrarOperacao("[COPY] " + parts[1] + " -> " + parts[2]);
                    }
                    break;

                case "cat":
                    if (parts.length < 2) {
                        break;
                    }
                    Directory dirCat = navegarAteDiretorio(root, parts[1]);
                    if (dirCat == null) {
                        break;
                    }
                    FileItem fileCat = dirCat.getFile(obterNomeFinal(parts[1]));
                    if (fileCat != null && fileCat.canRead()) {
                        System.out.println("Conteúdo de " + parts[1] + ":");
                        System.out.println(fileCat.getContent());
                    } else {
                        System.out.println("Permissão negada para leitura (sem 'r') ou arquivo não encontrado.");
                    }
                    break;

                case "chmod":
                    if (parts.length < 3) {
                        break;
                    }
                    Directory dirPerm = navegarAteDiretorio(root, parts[2]);
                    if (dirPerm == null) {
                        break;
                    }
                    FileItem arquivoPerm = dirPerm.getFile(obterNomeFinal(parts[2]));
                    if (arquivoPerm == null) {
                        break;
                    }
                    arquivoPerm.setPermission(parts[1]);
                    journal.registrarOperacao("[CHMOD] " + parts[1] + " " + parts[2]);
                    break;

                case "mv":
                    if (parts.length < 3) {
                        break;
                    }
                    Directory dirOrigemMV = navegarAteDiretorio(root, parts[1]);
                    Directory dirDestinoMV = navegarAteDiretorio(root, parts[2]);
                    if (dirOrigemMV == null || dirDestinoMV == null) {
                        break;
                    }
                    String nomeOrigemMV = obterNomeFinal(parts[1]);
                    String nomeDestinoMV = obterNomeFinal(parts[2]);
                    FileItem arqMV = dirOrigemMV.getFile(nomeOrigemMV);
                    if (arqMV != null) {
                        dirOrigemMV.removeFile(nomeOrigemMV);
                        arqMV.setName(nomeDestinoMV);
                        dirDestinoMV.addFile(arqMV);
                        journal.registrarOperacao("[MOVE] " + parts[1] + " -> " + parts[2]);
                        break;
                    }
                    Directory dirMV = dirOrigemMV.getSubDirectory(nomeOrigemMV);
                    if (dirMV != null) {
                        dirOrigemMV.removeDirectory(nomeOrigemMV);
                        Directory novoDir = new Directory(nomeDestinoMV);
                        for (String nomeArquivo : dirMV.getAllFiles().keySet()) {
                            novoDir.addFile(dirMV.getFile(nomeArquivo));
                        }
                        for (String nomeSub : dirMV.getAllDirectories().keySet()) {
                            novoDir.addDirectory(dirMV.getSubDirectory(nomeSub));
                        }
                        dirDestinoMV.addDirectory(novoDir);
                        journal.registrarOperacao("[MOVE] dir " + parts[1] + " -> " + parts[2]);
                    }
                    break;

                case "tree":
                    System.out.println("Estrutura do sistema de arquivos:");
                    root.exibirTree("");
                    journal.registrarOperacao("[TREE] Exibiu estrutura completa");
                    break;

                case "savefs":
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("filesystem.ser"))) {
                        oos.writeObject(root);
                        try (ObjectOutputStream backupStream = new ObjectOutputStream(new FileOutputStream("filesystem_backup.ser"))) {
                            backupStream.writeObject(root);
                        }
                        System.out.println("Sistema de arquivos salvo com sucesso.");
                    } catch (IOException e) {
                        System.out.println("Erro ao salvar sistema de arquivos.");
                    }
                    break;

                case "loadfs":
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("filesystem.ser"))) {
                        root = (Directory) ois.readObject();
                        System.out.println("Sistema de arquivos carregado com sucesso.");
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Erro ao carregar sistema de arquivos.");
                    }
                    break;

                case "log":
                    journal.exibirLog();
                    break;

                case "help":
                    System.out.println("Comandos disponíveis:");
                    System.out.println("  mkdir <caminho>\n  touch <caminho>\n  ls <dir>\n  rm <arq>\n  rmdir <dir>\n  rename <antigo> <novo>\n  cp <origem> <destino>\n  cat <arquivo>\n  chmod <permissao> <arquivo>\n  mv <origem> <destino>\n  tree\n  savefs\n  loadfs\n  log\n  rollback\n  help\n  exit");
                    break;

                default:
                    System.out.println("Comando inválido.");
            }
        }

        journal.encerrarSessao();
        scanner.close();
    }

    private static Directory navegarAteDiretorio(Directory root, String caminhoCompleto) {
        if (!caminhoCompleto.contains("/")) {
            return root;
        }
        int ultimaBarra = caminhoCompleto.lastIndexOf('/');
        String caminhoDir = caminhoCompleto.substring(0, ultimaBarra);
        return root.getDirectoryFromPath(caminhoDir);
    }

    private static String obterNomeFinal(String caminhoCompleto) {
        if (!caminhoCompleto.contains("/")) {
            return caminhoCompleto;
        }
        return caminhoCompleto.substring(caminhoCompleto.lastIndexOf('/') + 1);
    }

    private static void salvarSistemaDeArquivos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("filesystem.ser"))) {
            oos.writeObject(root);
            System.out.println("[EXIT] Sistema salvo com sucesso.");
        } catch (IOException e) {
            System.out.println("[EXIT] Erro ao salvar sistema de arquivos: " + e.getMessage());
        }
    }

    private static void carregarSistemaDeArquivos() {
        File arquivo = new File("filesystem.ser");
        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                root = (Directory) ois.readObject();
                System.out.println("[INIT] Sistema de arquivos carregado com sucesso.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[INIT] Erro ao carregar sistema de arquivos: " + e.getMessage());
            }
        }
    }

}
