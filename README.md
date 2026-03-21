# Restaurant Service

## Descrição

Este serviço é responsável pelo gerenciamento de restaurantes, permitindo o cadastro e visualização de restaurantes e itens de seus respectivos menus.

## Tecnologias Utilizadas

*   **Java 21**: Versão mais recente do Java, garantindo performance e acesso a features modernas da linguagem.
*   **Spring Boot 4.0.3**: Framework principal para a construção da aplicação, facilitando a configuração e o desenvolvimento de serviços RESTful.
*   **Spring Data JPA**: Para persistência de dados em banco de dados relacional.
*   **Spring Security**: Para autenticação e autorização.
*   **PostgreSQL**: Banco de dados relacional utilizado para persistir os dados da aplicação.
*   **H2 Database**: Banco de dados em memória para testes automatizados.
*   **Flyway**: Ferramenta para versionamento e migração de banco de dados.
*   **RabbitMQ**: Message broker para comunicação assíncrona entre serviços.
*   **MapStruct**: Para mapeamento de DTOs e entidades.
*   **SpringDoc (OpenAPI)**: Para documentação da API.
*   **Maven**: Gerenciador de dependências e build do projeto.
*   **JUnit 5 e Mockito**: Para testes unitários e de integração.
*   **Jacoco**: Para análise de cobertura de testes.

## Como Executar o Projeto

### Pré-requisitos

*   Java 21
*   Maven 3.9+
*   Docker e Docker Compose (para o banco de dados e RabbitMQ)

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/KervinCandido/restaurant.git
    cd restaurant
    ```

2.  **Configure as variáveis de ambiente:**
    O serviço é configurado através do arquivo `src/main/resources/application.yaml` e seus perfis (`dev`, `prod`). As seguintes variáveis de ambiente são necessárias para o perfil `dev`:

    - **Banco de Dados:**
      - `DB_HOST`: Host do banco de dados (padrão: `localhost`)
      - `DB_PORT`: Porta do banco de dados (padrão: `5432`)
      - `DB_NAME`: Nome do banco de dados (padrão: `restaurant-db`)
      - `DB_USER`: Usuário do banco de dados (padrão: `restaurant-user`)
      - `DB_PASSWORD`: Senha do banco de dados (padrão: `restaurant-password`)

    - **RabbitMQ:**
      - `MQ_HOST`: Host do RabbitMQ (padrão: `localhost`)
      - `MQ_PORT`: Porta do RabbitMQ (padrão: `5672`)
      - `MQ_USER`: Usuário do RabbitMQ (padrão: `restaurant-mq`)
      - `MQ_PASSWORD`: Senha do RabbitMQ (padrão: `password-mq`)

    Você pode criar um arquivo `.env` na raiz do projeto ou configurar essas variáveis diretamente no seu ambiente de execução.

3.  **Inicie os serviços de dependência (usando Docker):**
    Se houver um arquivo `docker-compose.yml` na raiz do projeto, você pode iniciar o PostgreSQL e o RabbitMQ com:
    ```bash
    docker-compose up -d
    ```

### Execução

1.  **Compile e execute a aplicação com o Maven:**
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

2.  A aplicação estará disponível em `http://localhost:8080`.

## Endpoints da API

A documentação completa da API está disponível via Swagger UI.

- **URL da Documentação (Swagger UI):** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **URL da Definição OpenAPI:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **URL do Actuator:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Principais Endpoints

- **Restaurantes:**
  - `GET /restaurants`: Lista todos os restaurantes cadastrados.
  - `GET /restaurants/management`: Lista todos os restaurantes visão de gerenciamento.
  - `POST /restaurants`: Cadastra um novo restaurante.
  - `GET /restaurants/{id}`: Detalha as informações de um restaurante específico.
  - `PUT /restaurants/{id}`: Atualiza os dados de um restaurante.
- **Menu (Cardápio):**
  - `GET /restaurants/{id}/menu`: Lista os itens do cardápio de um restaurante.
  - `POST /restaurants/{id}/menu`: Adiciona um novo item ao cardápio.
  - `GET /restaurants/{id}/menu/{itemId}`: Detalha um item específico do cardápio.
  - `PUT /restaurants/{id}/menu/{itemId}`: Atualiza um item do cardápio.

### Filas do RabbitMQ

- **Emite**
  - `order.restaurant.created`: Enviado quando uma pedido é criada.
  - `order.restaurant.updated`: Enviado quando uma pedido existente é atualizada.
  - `order.restaurant.deleted`: Enviado quando uma pedido existente é deletada.

  - `order.menuitem.created`: Enviado quando um item de menu é criado.
  - `order.menuitem.updated`: Enviado quando um item de menu é atualizado.
  - `order.menuitem.deleted`: Enviado quando um item de menu é deletado.

- **Consome**
  - `restaurant.user.created`: Evento para quando um usuário é criado.
  - `restaurant.user.updated`: Evento para quando um usuário existente é atualizado.
  - `restaurant.user.deleted`: Evento para quando um usuário é deletado