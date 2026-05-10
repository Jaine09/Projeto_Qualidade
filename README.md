# Gerenciador de Biblioteca Pessoal

![CI](https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/ci.yml/badge.svg)

Projeto semestral desenvolvido em Spring Boot com MongoDB para cadastro, autenticação e gerenciamento de livros de uma biblioteca pessoal.

---

# Visão Geral

O sistema permite que usuários realizem cadastro, autenticação e gerenciamento completo de livros de sua biblioteca pessoal.

O projeto foi desenvolvido com foco em:

- Qualidade de software
- Arquitetura MVC
- Testabilidade
- Integração contínua
- Cobertura de testes
- Persistência real sem mocks

---

# Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring MVC
- Spring Data MongoDB
- Thymeleaf
- Spring Security
- MongoDB
- Maven
- JUnit 5
- Testcontainers
- JaCoCo
- SonarQube
- GitHub Actions

---

# Funcionalidades

## Usuários

- Cadastro de usuários
- Login e logout
- Gerenciamento de sessão
- Validação de autenticação
- Persistência segura de usuários

## Livros

- Cadastro de livros
- Listagem de livros por usuário
- Edição de livros
- Exclusão de livros
- Associação de livros ao usuário autenticado

## API REST

### Usuários

- Criar usuário
- Listar usuários
- Buscar usuário por ID
- Atualizar usuário
- Deletar usuário
- Login via API

### Livros

- Criar livro
- Listar livros por usuário
- Buscar livro por ID
- Atualizar livro
- Deletar livro

---

# Arquitetura

O projeto segue arquitetura MVC organizada em camadas:

## Camadas

### `entity`

Entidades de domínio:

- `Usuario`
- `Livro`

### `repository`

Persistência MongoDB com Spring Data.

### `service`

Regras de negócio e validações do sistema.

### `api.controller`

Endpoints REST da aplicação.

### `front.controller`

Controllers MVC responsáveis pelas telas Thymeleaf.

### `templates`

Páginas HTML renderizadas com Thymeleaf.

### `static`

Arquivos estáticos:

- CSS
- imagens

### `test`

Testes automatizados:

- integração
- parametrizados
- REST
- MVC/E2E

---

# Qualidade e Testabilidade

O projeto foi desenvolvido com foco em qualidade de software utilizando:

- Testes de integração reais com Testcontainers
- Persistência real em MongoDB
- Cobertura de código com JaCoCo
- Pipeline automatizado com GitHub Actions
- Arquitetura MVC
- Separação de responsabilidades
- Testes REST e MVC
- Testes parametrizados
- Sem utilização de mocks

---

# Estratégia de Testes

O projeto possui:

## Testes Unitários

Validação das regras de negócio.

## Testes de Integração

Executados com:

- MongoDB real via Testcontainers
- Spring Boot Test

## Testes Parametrizados

Cobertura de múltiplos cenários.

## Testes Caixa Branca

Validação da lógica interna dos services.

## Testes Caixa Preta

Validação dos fluxos completos:

- controllers REST
- controllers MVC
- autenticação
- sessão

---

# Cobertura de Código

Cobertura atual do projeto:

# ✅ 97%

Relatório gerado com JaCoCo.

Após executar:

```bash
mvn clean verify
```

Abra:

```text
target/site/jacoco/index.html
```

---

# Como Executar Localmente

## Pré-requisitos

- Java 17 instalado
- Maven instalado
- Docker Desktop ou Docker via WSL
- MongoDB local ou URI configurada

---

# Configuração

Configure o arquivo:

```text
src/main/resources/application.properties
```

Exemplo:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/livraria
```

---

# Rodar Aplicação

```bash
mvn spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

---

# Rodar Testes

```bash
mvn clean verify
```

Os testes utilizam:

- Testcontainers
- MongoDB real
- Spring Boot Test

---

# SonarQube

Para executar análise local:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=projeto_livraria \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=SEU_TOKEN
```

---

# CI/CD

O projeto possui pipeline automatizado utilizando GitHub Actions.

Arquivo:

```text
.github/workflows/ci.yml
```

O pipeline executa automaticamente:

- Build da aplicação
- Execução de testes
- Geração de cobertura JaCoCo
- Upload de relatórios
- Verificação de qualidade

---

# Banco de Dados

Banco utilizado:

- MongoDB

Persistência realizada com:

- Spring Data MongoDB

---

# Segurança

O sistema possui:

- autenticação de usuários
- gerenciamento de sessão
- proteção de rotas
- validação de login
- controle de acesso por usuário

---

# Observação sobre VCR

O projeto não possui integração com APIs externas reais no escopo atual.

Por isso, não há fluxo externo para gravação/reprodução utilizando VCR.

Caso integrações externas sejam adicionadas futuramente, os testes deverão utilizar VCR para registro e reprodução das respostas sem utilização de mocks manuais.

---

# Estrutura do Projeto

```text
src
├── main
│   ├── java/com/livraria
│   │   ├── api
│   │   │   ├── controller
│   │   │   ├── repository
│   │   │   └── service
│   │   ├── config
│   │   ├── entity
│   │   └── front/controller
│   │
│   └── resources
│       ├── static
│       └── templates
│
└── test
    └── java/com/livraria
```

---

# Integrantes

- Giulia Alciati
- Camile Rosa
- Jaine Santos

---

# Disciplina

Projeto desenvolvido para a disciplina de Qualidade de Software.

Centro Universitário Senac.
