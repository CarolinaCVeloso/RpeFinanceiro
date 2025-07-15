# Sistema Financeiro

Este projeto é um sistema financeiro para gestão de clientes, faturas e pagamentos, desenvolvido em Java (Spring Boot) com frontend web estático e banco de dados PostgreSQL.

## Funcionalidades Implementadas

- Cadastro, listagem, atualização e bloqueio/desbloqueio de clientes
- Cadastro, listagem e pagamento de faturas
- Bloqueio automático de clientes com faturas atrasadas há mais de 3 dias
- Desbloqueio automático após quitação de todas as faturas atrasadas
- Atualização automática do limite disponível do cliente 
- Carga de dados modelo via `data.sql`
- API documentada via Swagger (acesso em `/swagger-ui.html`)
- Frontend web estático (HTML/JS/CSS) para interação com a API

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- PostgreSQL
- Docker & Docker Compose
- HTML, CSS, JavaScript (frontend estático)

## Como Executar

### 1. Usando Docker Compose (recomendado)

1. Gere o JAR do backend:
   ```sh
   ./mvnw clean package -DskipTests
   # ou
   mvn clean package -DskipTests
   ```
2. Suba a aplicação e o banco:
   ```sh
   docker-compose up --build
   ```
3. Acesse a API em [http://localhost:8080](http://localhost:8080)
4. Acesse o Swagger em [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
5. O banco estará disponível em `localhost:5432`

### 2. Execução Local (sem Docker)

1. Configure o PostgreSQL localmente (crie o banco `fintech`)
2. Ajuste o `application.properties` se necessário
3. Rode a aplicação:
   ```sh
   ./mvnw spring-boot:run
   # ou
   mvn spring-boot:run
   ```

### 3. Frontend

O frontend estático está em `src/main/resources/static`. Basta acessar [http://localhost:8080](http://localhost:8080) após subir o backend.

## Observações

- O sistema executa a rotina de bloqueio/desbloqueio automaticamente após criação de fatura e pagamento.
- Datas são tratadas no timezone America/Sao_Paulo.
- O volume `pgdata` garante persistência dos dados do banco no Docker.

## Contato

Dúvidas ou sugestões? Abra uma issue ou entre em contato!
