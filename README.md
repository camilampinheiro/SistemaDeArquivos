# Simulador de Sistema de Arquivos com Journaling

## Metodologia

O simulador foi desenvolvido em linguagem de programação **Java**, utilizando uma arquitetura orientada a objetos. O programa simula o comportamento de um sistema de arquivos via **modo Shell (linha de comando)**, permitindo ao usuário executar operações como criação, remoção, leitura, renomeação e movimentação de arquivos e diretórios.

Cada operação é registrada em um arquivo de log (journaling) para garantir **rastreamento e integridade dos dados**. Os dados persistem entre sessões por meio de serialização.

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### O que é um sistema de arquivos?

Um **sistema de arquivos** é o componente do sistema operacional responsável por organizar e gerenciar dados armazenados em dispositivos de armazenamento, como HDs ou SSDs. Ele fornece uma estrutura hierárquica para salvar, acessar e modificar arquivos e diretórios.

### O que é Journaling?

**Journaling** é um mecanismo usado para garantir a consistência dos dados. Antes de uma operação ser realizada, ela é registrada em um log (journal). Se ocorrer uma falha, como queda de energia, o sistema pode recuperar a integridade dos dados com base nesse log.

#### Tipos de Journaling:
- **Write-Ahead Logging (WAL):** Registra operações antes de aplicá-las.
- **Metadata Journaling:** Apenas alterações nos metadados são registradas.
- **Full Journaling:** Tanto dados quanto metadados são registrados.

Neste projeto, utilizamos o modelo de journaling com log de texto estruturado, com **timestamp** e descrição da operação.

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

O simulador utiliza as seguintes estruturas de dados:

- **Classe `Directory`:** Representa um diretório e contém listas de arquivos e subdiretórios.
- **Classe `FileItem`:** Representa um arquivo com nome, conteúdo, e permissões (`r/w`).
- **Classe `Journal`:** Registra todas as operações em um arquivo `journal_log.txt`, com marcação de horário.
- **Classe `FileSystemSimulator`:** Responsável pela interface em modo Shell e execução das operações.

### Implementação do Journaling

O log de operações (`journal_log.txt`) armazena cada comando executado com seu respectivo horário. Exemplo:

- [2025-06-01T14:20:45] [MKDIR] /projetos
- [2025-06-01T14:21:10] [CREATE] /projetos/relatorio.txt "Conteúdo inicial"

## Parte 3: Implementação em Java

### Classes principais

- **`FileSystemSimulator`**: Contém o loop principal do Shell, processamento dos comandos e serialização dos dados (`savefs`, `loadfs`).
- **`Directory`**: Permite adicionar, remover, renomear e navegar por diretórios.
- **`FileItem`**: Manipula arquivos e seu conteúdo, além de permissões.
- **`Journal`**: Gerencia o journaling com gravação persistente das operações e visualização (`log`).

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

## Parte 4: Instalação e Funcionamento

### Requisitos

- Java 11 ou superior
- Terminal ou IDE (como VS Code, IntelliJ ou Eclipse)

### Como executar

1. **Clone o repositório no GitHub**:
   ```bash
   git clone https://github.com/camilampinheiro/SistemaDeArquivos
   cd SistemaDeArquivos

   javac -d bin src/models/*.java src/FileSystemSimulator.java
   java FileSystemSimulator
