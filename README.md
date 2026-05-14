# 📚 Gerenciador de Biblioteca Pessoal

![CI](https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/ci.yml/badge.svg)

Sistema web desenvolvido em Spring Boot com MongoDB para gerenciamento de bibliotecas pessoais.

O projeto permite cadastro de usuários, autenticação segura e gerenciamento completo de livros, incluindo capas personalizadas e integração automática com ViaCEP.

---

# ✨ Funcionalidades

## 👤 Usuários

- Cadastro de usuários
- Login e logout
- Sessão autenticada
- Proteção de rotas
- Busca automática de endereço via CEP
- Integração com ViaCEP

## 📚 Livros

- Cadastro de livros
- Edição de livros
- Exclusão de livros
- Associação de livros por usuário
- Busca dinâmica em tempo real
- Cadastro de capas por URL
- Pré-visualização automática da capa
- Interface responsiva e moderna

## 🌐 API REST

### Usuários

- Criar usuário
- Listar usuários
- Buscar usuário por ID
- Atualizar usuário
- Remover usuário
- Login via API

### Livros

- Criar livro
- Buscar livros do usuário
- Atualizar livro
- Deletar livro

### CEP

- Busca de endereço por CEP utilizando ViaCEP

---

# 🛠 Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2
- Spring MVC
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- MongoDB
- Maven
- HTML5
- CSS3
- JavaScript
- JUnit 5
- Testcontainers
- JaCoCo
- SonarQube
- GitHub Actions
- OkHttp
- MockWebServer

---

# 🏗 Arquitetura

O projeto segue arquitetura MVC organizada em camadas.

## Estrutura

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

# 🧪 Qualidade de Software

O projeto foi desenvolvido com foco em qualidade e testabilidade.

## Recursos implementados

- Testes de integração reais
- Persistência MongoDB real
- Cobertura com JaCoCo
- Pipeline CI/CD
- Testcontainers
- Sem utilização de mocks
- Separação de responsabilidades
- Arquitetura em camadas

---

# 🔬 Estratégia de Testes

## ✔ Testes de Integração

Executados com:

- MongoDB real via Testcontainers
- Spring Boot Test

## ✔ Testes Parametrizados

Cobertura de múltiplos cenários e validações.

## ✔ Testes REST

Validação dos endpoints da API.

## ✔ Testes MVC

Validação das páginas e fluxos completos.

## ✔ Testes VCR

Integração com ViaCEP utilizando:

- MockWebServer
- respostas gravadas em JSON
- sem utilização de mocks manuais

---

# 📮 Integração ViaCEP

O sistema realiza preenchimento automático de endereço utilizando a API pública do ViaCEP.

## Funcionalidades

- Busca automática por CEP
- Preenchimento de:
  - endereço
  - cidade
  - estado
- Tratamento de CEP inválido
- Testes automatizados da integração

---

# 🎨 Interface

A interface foi desenvolvida com foco em:

- experiência do usuário
- responsividade
- design moderno
- cards dinâmicos
- busca em tempo real
- preview automático de capas

---

# 📈 Cobertura de Código

## ✅ Cobertura atual: 97%

Relatórios gerados com JaCoCo.

Após executar:

```bash
mvn clean verify
```

Abra:

```text
target/site/jacoco/index.html
```

---

# ▶ Como Executar

## Pré-requisitos

- Java 17
- Maven
- Docker Desktop ou WSL
- MongoDB

---

# ⚙ Configuração

Configure:

```properties
src/main/resources/application.properties
```

Exemplo:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/livraria
```

---

# 🚀 Executar Aplicação

```bash
mvn spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

---

# 🧪 Executar Testes

```bash
mvn clean verify
```

---

# 📊 SonarQube

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=projeto_livraria \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=SEU_TOKEN
```

---

# 🔄 CI/CD

Pipeline automatizado com GitHub Actions.

Executa automaticamente:

- build
- testes
- cobertura
- validações
- relatórios

Arquivo:

```text
.github/workflows/ci.yml
```

---

# 🔐 Segurança

O sistema possui:

- autenticação
- gerenciamento de sessão
- proteção de rotas
- controle de acesso
- validação de login

---

# 👩‍💻 Integrantes

- Giulia Alciati
- Camile Rosa
- Jaine Santos

---

# 🎓 Disciplina

Projeto desenvolvido para a disciplina de Qualidade de Software.

Centro Universitário Senac.
