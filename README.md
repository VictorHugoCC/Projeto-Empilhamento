# Projeto: Ball Sort Game

Este projeto implementa o jogo "Ball Sort Puzzle", um jogo de lógica onde o objetivo é organizar bolas coloridas em tubos, colocando bolas da mesma cor em um único tubo. O projeto é dividido em duas partes principais: um **back-end** em Java e um **front-end** em Angular/TypeScript.

## 🚀 Tecnologias Utilizadas

### Back-end
*   **Java 17+**
*   **Apache Maven**
*   **Spark Framework**
*   **Gson**
*   **SLF4J Simple**

### Front-end
*   **HTML5**
*   **CSS3**
*   **TypeScript**
*   **Node.js e NPM**

## 📁 Estrutura do Projeto

```
.
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── service/
│                   ├── Ball.java
│                   ├── GameBoard.java
│                   ├── Main.java
│                   ├── Move.java
│                   └── Tube.java
└── jogo-bolas/
    ├── node_modules/
    ├── package.json
    ├── package-lock.json
    ├── src/
    │   ├── app/
    │   │   ├── app.config.server.ts
    │   │   ├── app.config.ts
    │   │   ├── app.css
    │   │   ├── app.html
    │   │   ├── app.routes.server.ts
    │   │   └── app.ts
    │   ├── index.html
    │   ├── main.server.ts
    │   ├── main.ts
    │   ├── server.ts
    │   └── styles.css
```


## ⚙️ Como Rodar o Projeto

### Pré-requisitos Gerais

Certifique-se de ter instalado em sua máquina:
- Git
- Java Development Kit (JDK) 17 ou superior
- Apache Maven
    - O diretório `bin` da sua instalação do Maven deve estar no `PATH` do sistema, ou use o caminho completo para o executável `mvn.cmd`.
- Node.js e npm

### 1. Clonar o Repositório

Abra seu terminal e clone o projeto:

```bash
git clone https://github.com/VictorHugoCC/Projeto-Empilhamento.git
cd Projeto-Empilhamento
```

### 2. Iniciar o Back-end (Servidor Java)

O back-end é um servidor Java que expõe uma API REST.

1.  **Navegue até o diretório raiz do back-end:** (assumindo que você já está no diretório `Projeto-Empilhamento` após clonar)
    ```bash
    # Permaneça no diretório atual (Projeto-Empilhamento)
    ```
2.  **Compile o projeto e inicie o servidor:**
    ```bash
    # Se o Maven não estiver no seu PATH, use o caminho completo (ajuste o caminho do Maven conforme necessário):
    .\apache-maven-3.9.10\bin\mvn.cmd -Dexec.mainClass="org.service.Main" exec:java

    # Se o Maven estiver no PATH globalmente:
    # mvn -Dexec.mainClass="org.service.Main" exec:java
    ```
    O servidor iniciará e você verá uma mensagem no console indicando que ele está rodando em `http://localhost:4567`.

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
    ```
    Isso iniciará o servidor de desenvolvimento do front-end (geralmente em `http://localhost:4200` ou similar) e abrirá o navegador automaticamente.
