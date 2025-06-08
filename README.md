# Projeto: Ball Sort Game

Este projeto implementa o jogo "Ball Sort Puzzle", um jogo de lógica onde o objetivo é organizar bolas coloridas em tubos, colocando bolas da mesma cor em um único tubo. O projeto é dividido em duas partes principais: um **back-end** em Java (Spark Framework) e um **front-end** (provavelmente Angular/TypeScript).

## 🚀 Tecnologias Utilizadas

### Back-end
*   **Java 17+**
*   **Apache Maven** (para gerenciamento de projeto e dependências)
*   **Spark Framework** (para criação da API REST)
*   **Gson** (para serialização/desserialização JSON)
*   **SLF4J Simple** (para logging)

### Front-end
*   **HTML5**
*   **CSS3**
*   **TypeScript** (com estrutura que sugere Angular ou similar)
*   **Node.js e NPM** (para gerenciar dependências e executar o servidor de desenvolvimento do front-end)

## 📁 Estrutura do Projeto

```
.
├── pom.xml                                  # Configuração do Maven para o back-end
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── service/                 # Código fonte Java do back-end
│                   ├── Ball.java
│                   ├── GameBoard.java
│                   ├── Main.java            # Ponto de entrada do back-end
│                   ├── Move.java
│                   └── Tube.java
└── jogo-bolas/                              # Pasta do projeto front-end
    ├── node_modules/                        # Dependências do Node.js (geradas pelo npm install)
    ├── package.json                         # Dependências e scripts do front-end
    ├── package-lock.json
    ├── src/
    │   ├── app/                             # Componentes e lógica principal do front-end
    │   │   ├── app.config.server.ts
    │   │   ├── app.config.ts
    │   │   ├── app.css                      # Estilos CSS do componente principal
    │   │   ├── app.html                     # Template HTML do componente principal
    │   │   ├── app.routes.server.ts
    │   │   └── app.ts                       # Lógica (TypeScript) do componente principal
    │   ├── index.html                       # Ponto de entrada HTML do front-end
    │   ├── main.server.ts
    │   ├── main.ts                          # Ponto de entrada TypeScript do front-end
    │   ├── server.ts
    │   └── styles.css                       # Estilos CSS globais do front-end
    └── ... (outros arquivos de configuração do front-end)
```

## ⚙️ Como Rodar o Projeto

### Pré-requisitos Gerais

Certifique-se de ter instalado em sua máquina:
*   **Git**
*   **Java Development Kit (JDK) 17 ou superior**
*   **Apache Maven 3.x**
    *   **Importante:** Certifique-se de que o diretório `bin` da sua instalação do Maven esteja adicionado à variável de ambiente `PATH` do seu sistema, ou use o caminho completo para o executável `mvn.cmd` como demonstrado abaixo.
*   **Node.js e npm** (geralmente vêm juntos)

### 1. Clonar o Repositório

Abra seu terminal e clone o projeto:

```bash
git clone https://github.com/VictorHugoCC/Projeto-Empilhamento.git
cd Projeto-Empilhamento
```

### 2. Iniciar o Back-end (Servidor Java)

O back-end é um servidor Java que expõe uma API REST.

1.  **Navegue até o diretório raiz do back-end:**
    ```bash
    cd /c/Users/victor/Desktop/projeto # (Se você está neste diretório, pule este comando)
    ```
2.  **Compile o projeto e inicie o servidor:**
    Como o `pom.xml` não será modificado (conforme sua preferência) para apontar para a classe `org.service.Main`, usaremos o plugin `exec:java` para especificar a classe principal diretamente.

    ```bash
    # Se o Maven não estiver no seu PATH, use o caminho completo (ajuste o caminho do Maven conforme necessário):
    .\apache-maven-3.9.10\bin\mvn.cmd -Dexec.mainClass="org.service.Main" exec:java

    # OU, se você configurou o Maven no PATH globalmente:
    # mvn -Dexec.mainClass="org.service.Main" exec:java
    ```
    Você deverá ver uma mensagem no console indicando que o servidor está rodando em `http://localhost:4567`.

### 3. Iniciar o Front-end

O front-end é uma aplicação web que interage com o back-end.

1.  **Navegue até o diretório do front-end:**
    ```bash
    cd jogo-bolas
    ```
2.  **Instale as dependências do Node.js:**
    ```bash
    npm install
    ```
    Isso baixará todas as bibliotecas JavaScript e TypeScript necessárias para o front-end.
3.  **Inicie o servidor de desenvolvimento do front-end:**
    ```bash
    npm start
    # OU, se for um projeto Angular e você tiver o Angular CLI instalado:
    # ng serve --open
    ```
    Isso iniciará o servidor de desenvolvimento do front-end (geralmente em `http://localhost:4200` ou similar) e abrirá o navegador automaticamente.

## 🔑 Lógica de Vitória (Back-end)

A lógica para determinar se o jogo foi vencido está implementada no back-end, principalmente nas classes `GameBoard.java` e `Tube.java`.

*   **`GameBoard.java` (`checkVictory()`):**
    Este método verifica se *todos* os tubos no tabuleiro de jogo satisfazem a condição de serem "homogêneos ou vazios".

*   **`Tube.java` (`isHomogeneousOrEmpty()`):**
    **Atenção:** Como discutido, a lógica atual neste método considera um tubo "homogêneo" mesmo que ele não esteja completamente cheio, desde que as bolas presentes sejam da mesma cor.
    **Para uma lógica de vitória mais alinhada com o jogo "Ball Sort Puzzle" (onde tubos devem estar cheios de uma única cor ou vazios para a vitória), este método precisaria ser ajustado para verificar a capacidade do tubo.**

    **Lógica Atual:**
    ```java
    public boolean isHomogeneousOrEmpty() {
        if (isEmpty()) {
            return true; // Tubo vazio é considerado ordenado.
        }
        if (balls.size() == 1) { // Ligeiramente problemático: um tubo com 1 bola é considerado ordenado.
            return true;
        }
        String firstColor = balls.peek().getColor();
        for (Ball ball : balls) {
            if (!ball.getColor().equals(firstColor)) {
                return false;
            }
        }
        return true; // Homogêneo (mas não necessariamente cheio).
    }
    ```
    **Lógica Sugerida (para vitória completa):**
    Um tubo é considerado "ordenado" apenas se estiver completamente vazio OU completamente cheio com bolas da mesma cor.
