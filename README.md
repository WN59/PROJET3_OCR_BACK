# ChaTop – API Backend (Spring Boot)

API REST Java / Spring Boot du projet **ChaTop** (OpenClassRooms).  
Elle remplace **Mockoon** pour parler avec le front **Angular** (locations / rentals).

---

## État actuel du projet

Fonctionnel aujourd’hui :

| Méthode | URL | Auth | Rôle |
|---------|-----|------|------|
| `POST` | `/api/auth/register` | Non | Créer un compte → renvoie un JWT |
| `POST` | `/api/auth/login` | Non | Se connecter → renvoie un JWT |
| `GET` | `/api/auth/me` | Oui (Bearer JWT) | Profil de l’utilisateur connecté |

---

## Prérequis

Avant de lancer l’API, installe / démarre :

1. **JDK 21** (Java)
2. **Maven** (ou utilise le wrapper `mvnw` déjà dans le projet)
3. **MySQL** via **XAMPP** (ou équivalent)
4. (Optionnel) **Postman** pour tester
5. (Optionnel) Front Angular du projet, port `4200`

---

## Installation et lancement

### 1. Cloner / ouvrir le projet

Ouvre le dossier `PROJET3_OCR_BACK` dans votre IDE.

### 2. Créer la base MySQL

1. Démarre **MySQL** dans XAMPP  
2. Crée une base, par exemple `chatop_db`  
3. Exécute le script SQL de référence (tables `USERS`, `RENTALS`, `MESSAGES`) :


### 3. Créer le fichier des secrets `env.properties`

À la **racine** du projet (même niveau que `pom.xml`), copie `env.properties.example` vers **`env.properties`**, puis renseigne tes valeurs :

```properties
DB_URL=jdbc:mysql://localhost:3306/chatop_db
DB_USER=ton_utilisateur_mysql
DB_PASSWORD=ton_mot_de_passe_mysql
JWT_SECRET=une-cle-secrete-d-au-moins-32-caracteres
JWT_EXPIRATION_MS=3600000
```

| Variable | Signification |
|----------|----------------|
| `DB_URL` | Adresse JDBC de ta base |
| `DB_USER` / `DB_PASSWORD` | Identifiants MySQL |
| `JWT_SECRET` | Clé pour signer les tokens (ne jamais la committer) |
| `JWT_EXPIRATION_MS` | Durée de vie du token en ms (`3600000` = 1 h) |

Ce fichier est dans le **`.gitignore`** : il ne part pas sur Git.

### 4. Lancer l’API

**Depuis l’IDE** : lancer la classe `P3BackApplication` (bouton Run).

**Ou en terminal** (à la racine du projet) :

```bash
./mvnw spring-boot:run
```

Sous Windows (Invite de commandes) :

```bash
mvnw.cmd spring-boot:run
```

### 5. Vérifier que ça tourne

Dans la console, tu dois voir notamment :

- connexion MySQL OK (`HikariPool` started)
- `Tomcat started on port 3001`
- `Started P3BackApplication`

L’API écoute sur : **http://localhost:3001**

---

## Tester avec Postman

### Register

- **POST** `http://localhost:3001/api/auth/register`
- Body → raw → JSON :

```json
{
  "name": "Test TEST",
  "email": "test@test.com",
  "password": "test!31"
}
```

Réponse attendue : `200` + `{ "token": "..." }`

### Login

- **POST** `http://localhost:3001/api/auth/login`

```json
{
  "email": "test@test.com",
  "password": "test!31"
}
```

### Me (profil connecté)

- **GET** `http://localhost:3001/api/auth/me`
- Header : `Authorization` = `Bearer <ton_token>`

Réponse attendue (sans password) :

```json
{
  "id": 1,
  "name": "Test TEST",
  "email": "test@test.com",
  "created_at": "2026-10-27T12:00:00",
  "updated_at": "2026-10-27T12:00:00"
}
```

---

## Brancher le front Angular

1. Lance **cette API** sur le port `3001`
2. **Coupe Mockoon** (sinon conflit de port)
3. Lance Angular (`ng serve`, port `4200`)
4. Le proxy Angular envoie déjà les appels vers `localhost:3001`

Pas besoin de modifier le front.

---

## Stack technique

| Techno | Rôle |
|--------|------|
| Java 21 | Langage |
| Spring Boot 4 | Framework |
| Spring Web | API HTTP REST |
| Spring Data JPA | Accès base de données |
| MySQL | Base SQL |
| Spring Security + JWT | Authentification |
| Maven | Build / dépendances |

---

## Structure du projet (vue débutant)

```text
PROJET3_OCR_BACK/
├── pom.xml                          # Dépendances Maven du projet
├── mvnw / mvnw.cmd                  # Maven wrapper (lance Maven sans l’installer globalement)
├── env.properties                   # Secrets locaux (HORS Git)
├── .gitignore                       # Fichiers que Git ne doit pas versionner
├── README.md                        # Ce fichier
│
└── src/
    ├── main/
    │   ├── java/com/ocr/p3_back/
    │   │   ├── P3BackApplication.java   # Point d’entrée : démarre Spring
    │   │   ├── config/                  # Réglages globaux (sécurité…)
    │   │   ├── controller/              # URLs HTTP (ce que Postman/Angular appellent)
    │   │   ├── service/                 # Logique métier
    │   │   ├── repository/              # Accès BDD (requêtes)
    │   │   ├── entity/                  # Tables SQL en objets Java
    │   │   ├── dto/                     # Forme des JSON entrants/sortants
    │   │   ├── security/                # JWT (création + lecture du token)
    │   │   └── exception/               # Traduction des erreurs en réponses HTTP
    │   └── resources/
    │       └── application.properties   # Config publique (port, liens vers env…)
    └── test/                            # Tests automatisés (plus tard)
```

---

## À quoi servent les dossiers Java ?

Imagine une requête : *« Je veux m’inscrire »*.

```text
Angular / Postman
       ↓
  controller/     ← reçoit la requête HTTP
       ↓
  service/        ← décide quoi faire (hash password, créer user, générer token)
       ↓
  repository/     ← parle à MySQL
       ↓
  entity/         ← représente une ligne de table (USERS)
```

### `controller/` — la porte d’entrée HTTP

Fichier actuel : `AuthController.java`

- Définit les URLs : `/api/auth/register`, `/login`, `/me`
- Reçoit le JSON, appelle le service, renvoie la réponse
- **Ne contient pas** la logique métier complexe

### `service/` — le cerveau métier

Fichier actuel : `AuthService.java`

- Register : vérifier l’email, hasher le mot de passe, sauver, créer un token
- Login : vérifier email/password, créer un token
- Me : retrouver l’utilisateur à partir de l’email du token

### `repository/` — l’accès à la base

Fichier actuel : `UserRepository.java`

- Interface Spring Data JPA
- Méthodes du type `findByEmail`, `existsByEmail`
- Spring génère le SQL pour toi

### `entity/` — le miroir d’une table SQL

Fichier actuel : `User.java`

- Correspond à la table `USERS`
- Champs : id, name, email, password, dates…
- **On ne renvoie pas** l’entité telle quelle au front (à cause du password)

### `dto/` — les objets JSON pour le front

DTO = *Data Transfer Object* = « objet de transfert ».

| Fichier | Rôle |
|---------|------|
| `RegisterRequest` | JSON reçu à l’inscription |
| `LoginRequest` | JSON reçu au login |
| `AuthResponse` | JSON renvoyé avec `{ "token": "..." }` |
| `UserResponse` | JSON du profil `/me` (sans password) |

Pourquoi ? Pour **ne pas exposer** l’entité BDD et coller exactement au contrat Angular / Mockoon (`created_at`, etc.).

### `config/` — la configuration de l’appli

Fichier actuel : `SecurityConfig.java`

- Active le hash **BCrypt** des mots de passe
- Dit quelles routes sont **publiques** (`register`, `login`, Swagger plus tard)
- Dit que le reste demande d’être **connecté** (JWT)
- Branche le filtre JWT

### `security/` — tout ce qui concerne le token JWT

| Fichier | Rôle |
|---------|------|
| `JwtService` | Crée un token, le lit, vérifie s’il est encore valide |
| `JwtAuthenticationFilter` | À chaque requête : lit le header `Authorization: Bearer ...`, vérifie le token, dit à Spring « cet utilisateur est connecté » |

Sans le filtre, `/api/auth/me` resterait bloqué même avec un bon token.

### `exception/` — la gestion des erreurs HTTP

Fichier actuel : `GlobalExceptionHandler.java`

Quand quelque chose se passe mal dans le service (mauvais mot de passe, champs manquants…), on « lance une exception ».  
Ce fichier **écoute** ces exceptions et renvoie la bonne réponse au front :

- champs invalides → `400` + `{}`
- mauvais identifiants → `401` + `{ "message": "error" }`

Comme ça, le controller reste simple.

### `P3BackApplication.java`

La classe avec `main` : c’est elle qui **démarre** tout Spring Boot.

### `application.properties`

Config versionnée sur Git :

- port `3001`
- import de `env.properties`
- réglages JPA (`ddl-auto=validate` = les tables doivent déjà exister via le script SQL)

Les mots de passe / secrets sont dans `env.properties`, pas ici.

---

## Flux d’une connexion (pour bien visualiser)

```text
1) POST /api/auth/login  { email, password }
2) AuthService vérifie en BDD + BCrypt
3) JwtService crée un token signé avec JWT_SECRET
4) Réponse : { "token": "eyJ..." }

5) GET /api/auth/me
   Header: Authorization: Bearer eyJ...
6) JwtAuthenticationFilter valide le token → email connu
7) AuthService charge l’utilisateur → UserResponse (sans password)
```

---

## Sécurité – points importants

- Les mots de passe sont stockés **hashés** (BCrypt), jamais en clair
- Les secrets BDD / JWT sont dans `env.properties` (**jamais** commités)
- Seuls `register` et `login` sont publics ; `/me` et le reste exigent un JWT
- Ne jamais renvoyer le champ `password` dans un JSON

---

## Commandes Git utiles (rappel)

```bash
git status
git add .
git commit -m "message clair"
git push
```

Vérifie toujours que `env.properties` n’apparaît **pas** dans les fichiers à committer.


## Aide / dépannage rapide

| Problème | Piste |
|----------|--------|
| Erreur DataSource / URL | `env.properties` manquant ou mauvais `DB_*` |
| Table / colonne incorrecte | Relancer `script.sql`, vérifier `ddl-auto=validate` |
| `403` sur `/me` | Relancer l’API, refaire un login, header `Bearer <token>` correct |
| Port 3001 déjà pris | Couper Mockoon ou l’autre process |
| Angular ne voit pas l’API | API démarrée ? Mockoon arrêté ? Proxy vers `:3001` ? |
