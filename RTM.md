# RTM - Matriz de Rastreabilidade de Requisitos

Projeto: Gerenciador de Biblioteca Pessoal
Objetivo: mapear os requisitos funcionais aos testes automatizados e aos fluxos UML de sequência.

Cobertura atual do projeto: **97%**

---

# Tecnologias de Qualidade Utilizadas

- JUnit 5
- Spring Boot Test
- Testcontainers
- JaCoCo
- GitHub Actions
- SonarQube

---

# Matriz de Rastreabilidade

| ID   | Requisito Funcional                    | Implementação                                                                       | Testes Relacionados                                                                                                     | Tipo de Teste                                   | Status  |
| ---- | -------------------------------------- | ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------- | ------- |
| RF01 | Cadastrar usuário                      | `UsuarioController`, `UsuarioRestController`, `UsuarioService`, `UsuarioRepository` | `UsuarioControllerTest`, `UsuarioRestControllerTest`, `UsuarioServiceIntegrationTest`, `UsuarioServiceParametrizedTest` | Controller/E2E, REST, Integração, Parametrizado | Coberto |
| RF02 | Realizar login                         | `UsuarioController`, `UsuarioRestController`, `UsuarioService`                      | `UsuarioControllerTest`, `UsuarioRestControllerTest`, `UsuarioServiceIntegrationTest`, `UsuarioServiceParametrizedTest` | Controller/E2E, REST, Integração, Parametrizado | Coberto |
| RF03 | Realizar logout                        | `UsuarioController`                                                                 | `UsuarioControllerTest`                                                                                                 | Controller/E2E                                  | Coberto |
| RF04 | Cadastrar livro                        | `LivroController`, `LivroRestController`, `LivroService`, `LivroRepository`         | `LivroControllerTest`, `LivroRestControllerTest`, `LivroServiceIntegrationTest`, `LivroServiceParametrizedTest`         | Controller/E2E, REST, Integração, Parametrizado | Coberto |
| RF05 | Listar livros do usuário logado        | `LivroController`, `LivroRestController`, `LivroService`, `LivroRepository`         | `LivroControllerTest`, `LivroRestControllerTest`, `LivroServiceIntegrationTest`                                         | Controller/E2E, REST, Integração                | Coberto |
| RF06 | Buscar livro por ID                    | `LivroRestController`, `LivroService`, `LivroRepository`                            | `LivroRestControllerTest`, `LivroServiceIntegrationTest`                                                                | REST, Integração                                | Coberto |
| RF07 | Editar livro                           | `LivroController`, `LivroRestController`, `LivroService`                            | `LivroControllerTest`, `LivroRestControllerTest`, `LivroServiceIntegrationTest`                                         | Controller/E2E, REST, Integração                | Coberto |
| RF08 | Excluir livro                          | `LivroController`, `LivroRestController`, `LivroService`                            | `LivroControllerTest`, `LivroRestControllerTest`, `LivroServiceIntegrationTest`                                         | Controller/E2E, REST, Integração                | Coberto |
| RF09 | Impedir acesso à biblioteca sem sessão | `LivroController`                                                                   | `LivroControllerTest`                                                                                                   | Caixa Preta / E2E                               | Coberto |
| RF10 | Validar campos obrigatórios            | `UsuarioService`, `LivroService`                                                    | `UsuarioServiceParametrizedTest`, `LivroServiceParametrizedTest`                                                        | Parametrizado / Caixa Branca                    | Coberto |

---

# RF01 - Cadastro de Usuário

```mermaid
sequenceDiagram
    actor Usuario
    participant TelaCadastro as Tela de Cadastro
    participant UsuarioController
    participant UsuarioService
    participant UsuarioRepository
    participant MongoDB

    Usuario->>TelaCadastro: Preenche nome, email e senha
    TelaCadastro->>UsuarioController: POST /cadastro
    UsuarioController->>UsuarioService: salvar(usuario)
    UsuarioService->>UsuarioService: validar campos obrigatórios
    UsuarioService->>UsuarioRepository: findByEmail(email)
    UsuarioRepository->>MongoDB: consulta email
    MongoDB-->>UsuarioRepository: resultado
    UsuarioService->>UsuarioService: criptografar senha
    UsuarioService->>UsuarioRepository: save(usuario)
    UsuarioRepository->>MongoDB: persistir usuário
    MongoDB-->>UsuarioRepository: usuário salvo
    UsuarioRepository-->>UsuarioService: usuário salvo
    UsuarioService-->>UsuarioController: usuário salvo
    UsuarioController-->>TelaCadastro: redirect:/login
```

---

# RF02 - Login

```mermaid
sequenceDiagram
    actor Usuario
    participant TelaLogin as Tela de Login
    participant UsuarioController
    participant UsuarioService
    participant UsuarioRepository
    participant Sessao as HttpSession
    participant MongoDB

    Usuario->>TelaLogin: Informa email e senha
    TelaLogin->>UsuarioController: POST /login
    UsuarioController->>UsuarioService: login(email, senha)
    UsuarioService->>UsuarioRepository: findByEmail(email)
    UsuarioRepository->>MongoDB: consulta usuário
    MongoDB-->>UsuarioRepository: usuário encontrado
    UsuarioService->>UsuarioService: comparar senha com BCrypt
    UsuarioService-->>UsuarioController: usuário válido
    UsuarioController->>Sessao: salvar usuarioLogado
    UsuarioController-->>TelaLogin: redirect:/home
```

---

# RF03 - Logout

```mermaid
sequenceDiagram
    actor Usuario
    participant Sistema
    participant UsuarioController
    participant Sessao as HttpSession

    Usuario->>Sistema: Clica em sair
    Sistema->>UsuarioController: GET /logout
    UsuarioController->>Sessao: invalidate()
    UsuarioController-->>Sistema: redirect:/login
```

---

# RF04 - Cadastro de Livro

```mermaid
sequenceDiagram
    actor Usuario
    participant TelaLivro as Tela Cadastrar Livro
    participant LivroController
    participant Sessao as HttpSession
    participant LivroService
    participant LivroRepository
    participant MongoDB

    Usuario->>TelaLivro: Preenche dados do livro
    TelaLivro->>LivroController: POST /salvar
    LivroController->>Sessao: obter usuarioLogado
    LivroController->>LivroController: setar usuarioId no livro
    LivroController->>LivroService: salvar(livro)
    LivroService->>LivroService: validar título e usuário
    LivroService->>LivroRepository: save(livro)
    LivroRepository->>MongoDB: persistir livro
    MongoDB-->>LivroRepository: livro salvo
    LivroRepository-->>LivroService: livro salvo
    LivroService-->>LivroController: livro salvo
    LivroController-->>TelaLivro: redirect:/home
```

---

# RF05 - Listagem de Livros do Usuário

```mermaid
sequenceDiagram
    actor Usuario
    participant TelaHome as Tela Minha Biblioteca
    participant LivroController
    participant Sessao as HttpSession
    participant LivroService
    participant LivroRepository
    participant MongoDB

    Usuario->>TelaHome: Acessa /home
    TelaHome->>LivroController: GET /home
    LivroController->>Sessao: obter usuarioLogado
    LivroController->>LivroService: listarPorUsuario(usuarioId)
    LivroService->>LivroRepository: findByUsuarioId(usuarioId)
    LivroRepository->>MongoDB: buscar livros do usuário
    MongoDB-->>LivroRepository: lista de livros
    LivroRepository-->>LivroService: lista de livros
    LivroService-->>LivroController: lista de livros
    LivroController-->>TelaHome: renderizar pagina-inicial
```

---

# RF06 - Buscar Livro por ID

```mermaid
sequenceDiagram
    participant ClienteAPI
    participant LivroRestController
    participant LivroService
    participant LivroRepository
    participant MongoDB

    ClienteAPI->>LivroRestController: GET /api/livros/{id}
    LivroRestController->>LivroService: buscarPorId(id)
    LivroService->>LivroRepository: findById(id)
    LivroRepository->>MongoDB: consultar livro
    MongoDB-->>LivroRepository: livro encontrado ou vazio
    LivroRepository-->>LivroService: resultado
    LivroService-->>LivroRestController: resultado
    LivroRestController-->>ClienteAPI: 200 OK ou 404 Not Found
```

---

# RF07 - Editar Livro

```mermaid
sequenceDiagram
    actor Usuario
    participant TelaEdicao as Tela Editar Livro
    participant LivroController
    participant Sessao as HttpSession
    participant LivroService
    participant LivroRepository
    participant MongoDB

    Usuario->>TelaEdicao: Altera dados do livro
    TelaEdicao->>LivroController: POST /editar/{id}
    LivroController->>Sessao: obter usuarioLogado
    LivroController->>LivroService: buscarPorId(id)
    LivroService->>LivroRepository: findById(id)
    LivroRepository->>MongoDB: consultar livro
    MongoDB-->>LivroRepository: livro encontrado
    LivroController->>LivroController: validar se livro pertence ao usuário
    LivroController->>LivroService: atualizar(id, livroAtualizado)
    LivroService->>LivroService: validar dados
    LivroService->>LivroRepository: save(livro)
    LivroRepository->>MongoDB: atualizar livro
    MongoDB-->>LivroRepository: livro atualizado
    LivroController-->>TelaEdicao: redirect:/home
```

---

# RF08 - Excluir Livro

```mermaid
sequenceDiagram
    actor Usuario
    participant TelaHome as Tela Minha Biblioteca
    participant LivroController
    participant Sessao as HttpSession
    participant LivroService
    participant LivroRepository
    participant MongoDB

    Usuario->>TelaHome: Clica em excluir livro
    TelaHome->>LivroController: POST /deletar/{id}
    LivroController->>Sessao: obter usuarioLogado
    LivroController->>LivroService: buscarPorId(id)
    LivroService->>LivroRepository: findById(id)
    LivroRepository->>MongoDB: consultar livro
    MongoDB-->>LivroRepository: livro encontrado
    LivroController->>LivroController: validar se livro pertence ao usuário
    LivroController->>LivroService: deletar(id)
    LivroService->>LivroRepository: delete(livro)
    LivroRepository->>MongoDB: remover livro
    LivroController-->>TelaHome: redirect:/home
```

---

# RF09 - Proteção de Rotas por Sessão

```mermaid
sequenceDiagram
    actor UsuarioNaoLogado
    participant Navegador
    participant LivroController
    participant Sessao as HttpSession

    UsuarioNaoLogado->>Navegador: Acessa /home, /novo ou /editar/{id}
    Navegador->>LivroController: Requisição HTTP
    LivroController->>Sessao: buscar usuarioLogado
    Sessao-->>LivroController: null
    LivroController-->>Navegador: redirect:/login
```

---

# RF10 - Validação de Campos Obrigatórios

```mermaid
sequenceDiagram
    participant Controller
    participant Service
    participant Repository

    Controller->>Service: salvar/atualizar entidade
    Service->>Service: validar campos obrigatórios

    alt Dados inválidos
        Service-->>Controller: lança IllegalArgumentException
    else Dados válidos
        Service->>Repository: persistir dados
        Repository-->>Service: entidade salva
        Service-->>Controller: entidade salva
    end
```
