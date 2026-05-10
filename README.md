# Gerenciador de Biblioteca Pessoal

Projeto semestral desenvolvido em Spring Boot com MongoDB para cadastro, autenticação e gerenciamento de livros de uma biblioteca pessoal.

## Tecnologias

- Java 17
- Spring Boot 3.2.0
- Spring MVC
- Spring Data MongoDB
- Thymeleaf
- Spring Security
- MongoDB
- JUnit 5
- Testcontainers
- JaCoCo
- SonarQube
- GitHub Actions

## Funcionalidades

- Cadastro de usuários
- Login e logout com gerenciamento de sessão
- Cadastro de livros
- Listagem de livros por usuário logado
- Edição de livros
- Exclusão de livros
- API REST para usuários
- API REST para livros
- Persistência em MongoDB
- Testes unitários, parametrizados, integração e controller/E2E

## Arquitetura

O projeto segue arquitetura MVC:

- `entity`: entidades de domínio `Usuario` e `Livro`
- `repository`: interfaces de persistência MongoDB
- `service`: regras de negócio e validações
- `api.controller`: endpoints REST
- `front.controller`: controllers MVC para telas Thymeleaf
- `templates`: páginas HTML Thymeleaf
- `static`: arquivos CSS e imagens
- `test`: testes automatizados com Testcontainers

## Como executar localmente

### Pré-requisitos

- Java 17 instalado
- Maven instalado
- Docker Desktop ou Docker no WSL ativo
- MongoDB local ou URI configurada no `application.properties`

### Rodar aplicação

```bash
mvn spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

## Como rodar os testes

```bash
mvn clean verify
```

Os testes usam Testcontainers para subir um MongoDB real em container.

## Relatório de cobertura

Após executar:

```bash
mvn clean verify
```

Abra o relatório em:

```text
target/site/jacoco/index.html
```

## SonarQube

Para rodar análise local, mantenha o SonarQube ativo e execute:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=projeto_livraria \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=SEU_TOKEN
```

## CI/CD

O projeto possui pipeline em:

```text
.github/workflows/ci.yml
```

O pipeline executa:

- Build do projeto
- Testes automatizados
- Geração de cobertura JaCoCo
- Upload do relatório de cobertura
- Análise SonarQube
- Verificação de cobertura mínima de 80%

## Observação sobre VCR

O projeto não possui chamadas reais para APIs externas. Por isso, não há fluxo externo para gravar/reproduzir com VCR. Caso uma API externa seja adicionada, os testes deverão usar VCR para registrar e reproduzir as respostas, sem mocks manuais.
