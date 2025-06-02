package models;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Journal {
    private List<String> logs = new ArrayList<>();
    private final String FILE_NAME = "journal_log.txt";

    public void registrarOperacao(String operacao) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entrada = "[" + timestamp + "] " + operacao;
        logs.add(entrada);
        System.out.println("[JOURNAL] " + entrada);
        salvarNoArquivo(entrada);
    }

    public void exibirLog() {
        System.out.println("=== LOG DE OPERAÇÕES ===");
        for (String log : logs) {
            System.out.println(log);
        }
        System.out.println("=========================");
    }

    private void salvarNoArquivo(String log) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(log + "\n");
        } catch (IOException e) {
            System.out.println("Erro ao salvar no arquivo de log.");
        }
    }

    public void carregarDeArquivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                logs.add(linha);
            }
        } catch (IOException e) {
            System.out.println("Arquivo de log não encontrado.");
        }
    }

    public void iniciarSessao() {
        String inicio = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        salvarNoArquivo("===== Início da sessão: " + inicio + " =====");
    }

    public void encerrarSessao() {
        String fim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        salvarNoArquivo("===== Fim da sessão: " + fim + " =====\n");
    }

}
