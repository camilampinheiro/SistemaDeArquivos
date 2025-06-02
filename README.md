# Simulador de Sistema de Arquivos com Journaling

## Resumo

Este projeto apresenta o desenvolvimento de um simulador de sistema de arquivos em Java, com suporte ao modelo de **Journaling**. Através dele, é possível realizar operações fundamentais de gerenciamento de arquivos e diretórios, com persistência e registro das ações realizadas.

---

## Introdução

O gerenciamento eficiente de arquivos é essencial para o funcionamento dos sistemas operacionais. Compreender como os arquivos e diretórios são organizados e manipulados fornece uma base sólida para o entendimento das estruturas de um SO. Este simulador visa representar, de forma didática, o funcionamento interno de um sistema de arquivos simplificado.

---

## Objetivo

Desenvolver um simulador de sistema de arquivos em **Java**, capaz de:

- Criar, remover e renomear arquivos e diretórios;
- Copiar e mover arquivos e diretórios;
- Exibir o conteúdo de arquivos e a estrutura completa;
- Controlar permissões de leitura e escrita;
- Utilizar **Journaling** para registrar operações;
- **Persistir automaticamente** o estado do sistema ao encerrar;
- Permitir comandos manuais de `savefs` e `loadfs`.

---

## Metodologia

### Linguagem utilizada

- **Java 17+**
- **Serialização** (`ObjectOutputStream`) para persistência
- Interface de texto simulando **Shell** de comandos

### Estrutura de execução

- O sistema é executado em **modo Shell**
- O usuário digita comandos no terminal
- As ações são processadas e logadas via **journaling**
- O estado é salvo em disco ao digitar `exit`

---

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### O que é um Sistema de Arquivos?

É a estrutura usada pelo sistema operacional para armazenar, organizar e acessar arquivos e diretórios no armazenamento persistente.

### O que é Journaling?

Journaling é uma técnica usada para garantir a **integridade dos dados** em caso de falhas (como queda de energia). As operações são primeiro **registradas em um log (journal)** antes de serem executadas, permitindo recuperação em caso de erro.

#### Tipos:
- **Write-ahead Logging (WAL)**: as ações são gravadas no log antes de serem aplicadas.
- **Log-structured FS**: o sistema inteiro é tratado como um log sequencial.

---

## Parte 2: Arquitetura do Simulador

### Classes e Estruturas

- `FileSystemSimulator`: shell principal do sistema
- `Directory`: representa diretórios (possui subdiretórios e arquivos)
- `FileItem`: representa arquivos (com nome, conteúdo e permissões)
- `Journal`: registra as operações com timestamp

### Journaling

- Cada ação relevante é registrada no arquivo `journal_log.txt`
- Os logs podem ser visualizados com o comando `log`

---

## Parte 3: Implementação Java

### Principais Comandos:

| Comando | Ação |
|--------|------|
| `mkdir <caminho>` | Cria um diretório |
| `touch <caminho> "conteúdo"` | Cria um arquivo com conteúdo |
| `ls <dir>` | Lista arquivos e diretórios |
| `rm <arquivo>` | Remove um arquivo |
| `rmdir <diretório>` | Remove um diretório |
| `rename <antigo> <novo>` | Renomeia arquivos ou diretórios |
| `cp <origem> <destino>` | Copia arquivos |
| `mv <origem> <destino>` | Move arquivos ou diretórios |
| `chmod <permissão> <arquivo>` | Altera permissões de leitura/escrita |
| `cat <arquivo>` | Exibe conteúdo do arquivo |
| `tree` | Exibe estrutura hierárquica |
| `log` | Exibe o histórico de ações com timestamp |
| `savefs` | Salva manualmente o estado do sistema |
| `loadfs` | Carrega o estado salvo anteriormente |
| `exit` | Salva automaticamente e encerra o sistema |

---

## Parte 4: Instalação e Execução

### Requisitos

- Java JDK 17+
- IDE ou terminal com suporte a compilação

### Executar o sistema

```bash
javac -d bin src/models/*.java src/FileSystemSimulator.java
cd bin
java FileSystemSimulator
