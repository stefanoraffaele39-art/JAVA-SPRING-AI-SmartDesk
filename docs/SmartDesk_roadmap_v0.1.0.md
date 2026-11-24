# SmartDesk – Roadmap v0.1.0 (Backend base)

> Roadmap preparata il 17 novembre 2026 per completare la v0.1.0.
> Stato aggiornato al 20 novembre 2026: passi 1 e 2 completati (pull request #2 unita su `main`), lavoro in corso sul branch `feature/ticket-api`.

**Obiettivo della v0.1.0:** con `docker compose up -d` e l'avvio dell'app si provano tutte le API da Swagger, con i dati di esempio già caricati.

---

## Correzioni preliminari ✅

- [x] `PageResponse<T>` con `List<T> content`, `int page`, `int size`, `long totalElements`, `int totalPages` e metodo `from(Page<T>)`
- [x] `ErrorResponse`: campo `timeStamp` rinominato in `timestamp`
- [x] Commit del lavoro non salvato

```java
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
```

---

## Passo 1 – API delle categorie ✅

Il modello per tutto il resto.

- [x] `CategoryRepository`: `existsByName(String name)` ed `existsByNameAndIdNot(String name, Long id)`
- [x] `CategoryService` con `@Transactional`: `findAll`, `findById`, `create`, `update`, `delete`
  - [x] `create` e `update`: nome già esistente → `DuplicateResourceException` (409)
  - [x] `delete`: categoria con ticket → `BusinessRuleException` (409), tramite `TicketRepository.existsByCategoryId`
- [x] `CategoryController` con `@RestController` e `@RequestMapping("/api/v1/categories")`
  - [x] `POST` → 201 con header `Location`, `DELETE` → 204, il resto → 200
- [x] Prove da Swagger: successo, 404, 400, 409
- [x] Commit: `feat: API REST categorie`

---

## Passo 2 – API degli utenti ✅

- [x] `UserMapper`: `toResponse`, `toSummary`, `toEntity`, `updateEntity` (con `createdAt` ignorato)
- [x] `UserService`
  - [x] email duplicata: `existsByEmail` in creazione, `existsByEmailAndIdNot` in modifica → 409
  - [x] eliminazione di un utente autore/assegnatario di ticket (`existsByAuthorIdOrAssigneeId`) o autore di commenti (`existsByAuthorId`) → `BusinessRuleException`
- [x] `UserController`: `GET /users` paginato con `@ParameterObject Pageable` → `PageResponse<UserResponse>`; `GET /users/by-role?role=AGENT`
- [x] Commit: `feat: API REST utenti e correzioni ai controller`
- [x] Pull request di `feature/user-category-api` e merge su `main`
- [x] Nuovo branch `feature/ticket-api`

---

## Passo 3 – API dei ticket (la parte più ricca)

- [ ] `TicketMapper` con `uses = {UserMapper.class, CategoryMapper.class}`
  - `toEntity(CreateTicketRequest)` ignora `status`, `createdAt`, `updatedAt`, `closedAt`, `author`, `assignee`, `category`, `comments`
- [ ] `TicketService`
  - [ ] `create`: carica autore e categoria dai repository (404 se non esistono), poi `ticket.setAuthor(...)` e `ticket.setCategory(...)`
  - [ ] `findById`, `update`, `delete`
  - [ ] lista paginata con filtri facoltativi `status`, `priority`, `categoryId`, `assigneeId`, per esempio con `@Query` e parametri nulli: `(:status IS NULL OR t.status = :status)`
  - [ ] `@EntityGraph(attributePaths = {"author", "assignee", "category"})` sui metodi di lettura del repository, contro il problema N+1
- [ ] Cambio di stato (`PATCH /tickets/{id}/status`), con la regola nell'enum:

  ```java
  public boolean canTransitionTo(TicketStatus next) {
      return switch (this) {
          case OPEN        -> next == IN_PROGRESS;
          case IN_PROGRESS -> next == RESOLVED;
          case RESOLVED    -> next == CLOSED || next == OPEN;
          case CLOSED      -> false;
      };
  }
  ```

  - passaggio non consentito → `BusinessRuleException` (409)
  - stato `CLOSED` → valorizzare `closedAt`
- [ ] Assegnazione (`PATCH /tickets/{id}/assignee`): utente non `AGENT` → `BusinessRuleException`
- [ ] Statistiche (`GET /tickets/stats`): due query `GROUP BY` (per stato e per priorità) → `TicketStatsResponse` con due `Map`
- [ ] Commit, uno per blocco:
  - `feat: CRUD ticket`
  - `feat: cambio di stato e assegnazione`
  - `feat: statistiche ticket`

---

## Passo 4 – Commenti

- [ ] `CommentMapper`: `toResponse`, con l'autore tramite `UserMapper.toSummary`
- [ ] `CommentRepository`: `findByTicketIdOrderByCreatedAtAsc`, con `@EntityGraph` su `author`
- [ ] Endpoint `GET` e `POST /api/v1/tickets/{id}/comments`; il `POST` usa `ticket.addComment(...)`
- [ ] Commit: `feat: API commenti`
- [ ] Pull request di `feature/ticket-api` e merge su `main`

---

## Passo 5 – Documentazione e consegna

- [ ] Collection Postman: una richiesta per endpoint, variabile `baseUrl` = `http://localhost:8080/api/v1`, esportata in `docs/smartdesk.postman_collection.json`
  - scorciatoia: Import in Postman di `http://localhost:8080/v3/api-docs`
- [ ] `README.md` con:
  - a cosa serve il progetto
  - lo stack
  - come avviarlo (`docker compose up -d` e `.\mvnw.cmd -pl smartdesk-api spring-boot:run`)
  - il link a Swagger (`http://localhost:8080/swagger-ui.html`)
  - la tabella degli endpoint
- [ ] Facoltativo: `@Tag` e `@Operation` di springdoc sui controller

---

## Passo 6 – Rilascio v0.1.0

- [ ] Nei due `pom.xml`: versione da `0.1.0-SNAPSHOT` a `0.1.0`
- [ ] Commit `chore: release v0.1.0`
- [ ] Tag e push:

  ```bash
  git tag -a v0.1.0 -m "Backend base"
  git push origin v0.1.0
  ```

- [ ] GitHub: **Releases → Draft a new release** dal tag, con "Generate release notes"
- [ ] Versione a `0.2.0-SNAPSHOT` e commit `chore: inizio sviluppo v0.2.0`

---

## Promemoria utili

- Costruire ogni funzionalità dal basso: enum → entità → migrazione → repository → service → DTO e mapper → controller.
- Il service restituisce sempre DTO, convertiti dentro un metodo `@Transactional` (con `open-in-view: false` le relazioni `LAZY` si leggono solo nella transazione). Per le sole letture: `@Transactional(readOnly = true)`.
- Nei controller: `@Valid` su ogni `@RequestBody`, percorsi scritti `"/{id}"`, `PUT` per le modifiche.
- Dopo errori di compilazione strani: `.\mvnw.cmd clean` prima di avviare. Usare sempre `.\mvnw.cmd`, non `mvn`.
- Primo push di un branch nuovo: `git push -u origin <branch>` (oppure, una volta sola, `git config --global push.autoSetupRemote true`).
