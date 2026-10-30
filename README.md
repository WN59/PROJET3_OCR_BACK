# ChaTop – API Backend (Spring Boot)

API REST Java / Spring Boot du projet **ChaTop** (OpenClassRooms).  
Elle remplace **Mockoon** pour parler avec le front **Angular** (locations / rentals).

> **Important** : le front Angular ne doit pas être modifié.  
> Cette API respecte le même contrat HTTP (URLs, JSON, codes de réponse).

## État actuel du projet

### Routes opérationnelles

| Méthode | URL | Auth | Body | Réponse |
|---------|-----|------|------|---------|
| `POST` | `/api/auth/register` | Non | JSON `{ name, email, password }` | `{ "token": "..." }` |
| `POST` | `/api/auth/login` | Non | JSON `{ email, password }` | `{ "token": "..." }` |
| `GET` | `/api/auth/me` | Oui | — | profil user (sans password) |
| `GET` | `/api/rentals` | Oui | — | `{ "rentals": [ ... ] }` |
| `GET` | `/api/rentals/{id}` | Oui | — | un rental |
| `POST` | `/api/rentals` | Oui | **multipart/form-data** + image | `{ "message": "Rental created !" }` |
| `GET` | `/api/user/{id}` | Oui | — | même forme que `/auth/me` |

### Routes restantes (à venir)

| Méthode | URL | Auth | Notes |
|---------|-----|------|--------|
| `PUT` | `/api/rentals/{id}` | Oui | multipart **sans** `picture` → `"Rental updated !"` |
| `POST` | `/api/messages` | Oui | JSON `{ rental_id, user_id, message }` |

### Accès public (sans JWT)

- `/api/auth/register`, `/api/auth/login`
- `/uploads/**` (fichiers images uploadés)
- Swagger plus tard : `/swagger-ui/**`, `/v3/api-docs/**`

---

## Prérequis

1. **JDK 21** (Java)
2. **Maven** (ou le wrapper `mvnw` du projet)
3. **MySQL** via **XAMPP** (ou équivalent)
4. (Optionnel) **Postman** pour tester
5. (Optionnel) Front Angular, port `4200`

---

## Installation et lancement

### 1. Cloner / ouvrir le projet

Ouvre le dossier `PROJET3_OCR_BACK` dans ton IDE.

### 2. Créer la base MySQL

1. Démarre **MySQL** dans XAMPP  
2. Crée une base, par exemple `chatop_db`  
3. Exécute le script SQL de référence (tables `USERS`, `RENTALS`, `MESSAGES`) :  
   `ressources/sql/script.sql` (dossier local / hors Git selon ta config)

### 3. Créer `env.properties`

À la **racine** du projet, copie `env.properties.example` → **`env.properties`** :

```properties
DB_URL=jdbc:mysql://localhost:3306/chatop_db
DB_USER=ton_utilisateur_mysql
DB_PASSWORD=ton_mot_de_passe_mysql
JWT_SECRET=une-cle-secrete-d-au-moins-32-caracteres
JWT_EXPIRATION_MS=3600000
UPLOAD_DIR=uploads
UPLOAD_BASE_URL=http://localhost:3001/uploads
```

| Variable | Signification |
|----------|----------------|
| `DB_URL` | Adresse JDBC de ta base |
| `DB_USER` / `DB_PASSWORD` | Identifiants MySQL |
| `JWT_SECRET` | Clé pour signer les tokens (**jamais** committer) |
| `JWT_EXPIRATION_MS` | Durée de vie du token en ms (`3600000` = 1 h) |
| `UPLOAD_DIR` | Dossier local où sont sauvés les fichiers (créé auto) |
| `UPLOAD_BASE_URL` | Préfixe d’URL publique des images |

`env.properties` est dans le **`.gitignore`**.

### 4. Lancer l’API

**IDE** : Run sur `P3BackApplication`.

**Terminal** :

```bash
./mvnw spring-boot:run
```

Windows :

```bash
mvnw.cmd spring-boot:run
```

Tu dois voir : MySQL OK, `Tomcat started on port 3001`, `Started P3BackApplication`.

API : **http://localhost:3001**

---

## Upload d’images (comment ça marche)

Sur `POST /api/rentals`, le front envoie un fichier `picture` en **multipart**.

```text
Postman / Angular (form-data + fichier)
        ↓
RentalController
        ↓
RentalService
   ├─ retrouve le user connecté (JWT) → owner_id
   ├─ FileStorageService.store(picture)
   │     → fichier sauvé dans uploads/xxxx.jpg
   │     → URL : http://localhost:3001/uploads/xxxx.jpg
   └─ enregistre la rental en BDD (colonne picture = cette URL)
```

| Élément | Rôle |
|---------|------|
| `FileStorageService` | Écrit le fichier sur le disque, génère un nom unique (UUID) |
| `WebConfig` | Expose le dossier `uploads/` en URL `/uploads/**` |
| `SecurityConfig` | Laisse `/uploads/**` **public** (sinon les `<img>` du front seraient bloqués) |
| Dossier `uploads/` | Hors Git (`.gitignore`) |

Formats acceptés en pratique : `.jpg`, `.png`, `.gif`, `.webp`… (pas de filtre MIME strict pour l’instant).  
Taille max : **5 MB** (`spring.servlet.multipart` dans `application.properties`).

---

## Tester avec Postman

Astuce : après login/register, enregistre le token dans une variable d’environnement `{{token}}` (script post-response) et mets l’auth **Bearer Token** au niveau de la collection.

### Auth

**Register** — `POST {{baseUrl}}/api/auth/register` (JSON) :

```json
{
  "name": "Test TEST",
  "email": "test@test.com",
  "password": "test!31"
}
```

**Login** — `POST {{baseUrl}}/api/auth/login` → `{ "token": "..." }`

**Me** — `GET {{baseUrl}}/api/auth/me` + Bearer → profil sans password.

### Rentals

**Liste** — `GET {{baseUrl}}/api/rentals` + Bearer  
→ `{ "rentals": [ ... ] }`

**Détail** — `GET {{baseUrl}}/api/rentals/1` + Bearer

**Créer** — `POST {{baseUrl}}/api/rentals` + Bearer  
Body → **form-data** (pas raw JSON) :

| Key | Type | Exemple |
|-----|------|---------|
| `name` | Text | Maison test |
| `surface` | Text | 50 |
| `price` | Text | 800 |
| `description` | Text | Belle maison |
| `picture` | **File** | une image |

Réponse : `{ "message": "Rental created !" }`

Puis `GET /api/rentals` : la location apparaît avec une URL `picture`.  
Ouvre l’URL dans le navigateur pour voir l’image.

### User

`GET {{baseUrl}}/api/user/1` + Bearer → même shape que `/auth/me`.

---

## Brancher le front Angular

1. Lance cette API sur `:3001`
2. **Coupe Mockoon**
3. Lance Angular (`ng serve`, `:4200`)
4. Le proxy Angular pointe déjà vers `localhost:3001`

Pas besoin de modifier le front.

---

## Stack technique

| Techno | Rôle |
|--------|------|
| Java 21 | Langage |
| Spring Boot 4 | Framework |
| Spring Web | API HTTP REST + multipart |
| Spring Data JPA | Accès base de données |
| MySQL | Base SQL |
| Spring Security + JWT | Authentification |
| Maven | Build / dépendances |

---

## Structure du projet

```text
PROJET3_OCR_BACK/
├── pom.xml
├── mvnw / mvnw.cmd
├── env.properties              # Secrets locaux (HORS Git)
├── env.properties.example      # Modèle à copier
├── uploads/                    # Images uploadées (HORS Git, créé au runtime)
├── README.md
│
└── src/main/
    ├── java/com/ocr/p3_back/
    │   ├── P3BackApplication.java
    │   ├── config/             # SecurityConfig, WebConfig
    │   ├── controller/         # Auth, Rental, User
    │   ├── service/            # Auth, Rental, User, FileStorage
    │   ├── repository/         # User, Rental
    │   ├── entity/             # User, Rental
    │   ├── dto/                # Request / Response JSON
    │   ├── security/           # JwtService, JwtAuthenticationFilter
    │   └── exception/          # GlobalExceptionHandler, ResourceNotFound…
    └── resources/
        └── application.properties
```

---

## À quoi servent les dossiers Java ? (débutant)

```text
Angular / Postman
       ↓
  controller/     ← URLs HTTP
       ↓
  service/        ← logique métier (+ upload)
       ↓
  repository/     ← MySQL
       ↓
  entity/         ← tables SQL
```

### `controller/`

| Fichier | Routes |
|---------|--------|
| `AuthController` | register, login, me |
| `RentalController` | liste, détail, create (multipart) |
| `UserController` | `GET /api/user/{id}` |

### `service/`

| Fichier | Rôle |
|---------|------|
| `AuthService` | inscription, connexion, profil `/me` |
| `RentalService` | CRUD rentals (lecture + create) |
| `UserService` | récupérer un user par id |
| `FileStorageService` | sauvegarde des images sur le disque |

### `repository/` / `entity/`

- `User` / `UserRepository` → table `USERS`
- `Rental` / `RentalRepository` → table `RENTALS`

Les entités ne sont **pas** renvoyées telles quelles au front (DTO à la place).

### `dto/`

| Fichier | Rôle |
|---------|------|
| `RegisterRequest` / `LoginRequest` | JSON entrants auth |
| `AuthResponse` | `{ "token" }` |
| `UserResponse` | profil (`created_at` / `updated_at` en snake_case) |
| `RentalResponse` | une location |
| `RentalsResponse` | wrapper `{ "rentals": [...] }` |
| `ApiResponseMessage` | `{ "message": "..." }` (ex. create rental) |

### `config/`

| Fichier | Rôle |
|---------|------|
| `SecurityConfig` | BCrypt, routes publiques, JWT filter, `/uploads/**` public |
| `WebConfig` | mappe `/uploads/**` → dossier local `uploads/` |

### `security/`

| Fichier | Rôle |
|---------|------|
| `JwtService` | crée / lit / valide le token |
| `JwtAuthenticationFilter` | lit `Authorization: Bearer ...` à chaque requête |

### `exception/`

| Fichier | Rôle |
|---------|------|
| `GlobalExceptionHandler` | transforme les exceptions en 400 / 401 / 404 |
| `ResourceNotFoundException` | ressource introuvable (rental, user…) |

---

## Flux utiles

### Connexion

```text
POST /login → token
GET /me + Bearer → profil
```

### Création d’une location

```text
POST /api/rentals (multipart + Bearer)
  → image dans uploads/
  → ligne dans RENTALS (picture = URL)
  → { "message": "Rental created !" }
GET /api/rentals → la location apparaît
```

---

## Sécurité

- Mots de passe **BCrypt** en base
- Secrets dans `env.properties` (hors Git)
- JWT obligatoire sauf register / login / fichiers `/uploads/**`
- `owner_id` à la création = utilisateur du token (pas une valeur client)
- Jamais renvoyer `password` dans un JSON

---

## Dépannage rapide

| Problème | Piste |
|----------|--------|
| Erreur DataSource | `env.properties` / variables `DB_*` |
| Schéma JPA | tables créées via `script.sql` ? `ddl-auto=validate` |
| `403` sur routes protégées | Bearer manquant / `Bearer Bearer` / mauvais environnement Postman |
| `POST is not supported` | relancer l’API après ajout du `@PostMapping` |
| Image inaccessible | `/uploads/**` public ? `WebConfig` OK ? fichier bien dans `uploads/` ? |
| Port 3001 pris | couper Mockoon |
| Angular ne parle pas à l’API | API up ? Mockoon off ? proxy `:3001` ? |

---

## Prochaines étapes

1. `PUT /api/rentals/{id}` (multipart sans picture, contrôle propriétaire)
2. `POST /api/messages`
3. CORS si besoin
4. Swagger / springdoc (étape OCR suivante)
5. Validation mentor
