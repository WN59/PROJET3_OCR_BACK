# ChaTop – API Backend

API REST **Spring Boot** du projet ChaTop (OpenClassRooms).  
Elle remplace Mockoon pour le front Angular (contrat HTTP identique).

> Guide détaillé (architecture, flux, dépannage) : **[GUIDE_PROJET.md](GUIDE_PROJET.md)**

---

## Prérequis

- JDK **21**
- Maven (ou le wrapper `mvnw` du projet)
- MySQL (ex. via **XAMPP**)

---

## Configuration de la base de données

1. Démarre MySQL (XAMPP ou équivalent).
2. Crée une base, par exemple `chatop_db`.
3. Exécute le script SQL fourni (tables `USERS`, `RENTALS`, `MESSAGES`) :  
   `ressources/sql/script.sql`
4. À la **racine** du projet, copie `env.properties.example` → **`env.properties`** :

```properties
DB_URL=jdbc:mysql://localhost:3306/chatop_db
DB_USER=ton_utilisateur_mysql
DB_PASSWORD=ton_mot_de_passe_mysql
JWT_SECRET=une-cle-secrete-d-au-moins-32-caracteres
JWT_EXPIRATION_MS=3600000
UPLOAD_DIR=uploads
UPLOAD_BASE_URL=http://localhost:3001/uploads
```

`env.properties` contient les secrets : il ne doit **pas** être versionné (déjà dans `.gitignore`).

---

## Installation et lancement

```bash
# À la racine du projet
./mvnw spring-boot:run
```

Sous Windows :

```bash
mvnw.cmd spring-boot:run
```

Ou lance la classe `P3BackApplication` depuis ton IDE.

L’API écoute sur : **http://localhost:3001**

Vérifie dans les logs : connexion MySQL OK, `Started P3BackApplication`.

---

## Documentation Swagger

| Ressource | Lien |
|-----------|------|
| **Swagger UI** | [http://localhost:3001/swagger-ui.html](http://localhost:3001/swagger-ui.html) |
| Spec OpenAPI (JSON) | [http://localhost:3001/v3/api-docs](http://localhost:3001/v3/api-docs) |

### Utiliser le JWT dans Swagger

1. Ouvre Swagger UI.
2. Appelle **POST `/api/auth/login`** (ou register) et copie le `token`.
3. Clique sur **Authorize** → colle le token **sans** le mot `Bearer`.
4. Teste les routes protégées (`/api/auth/me`, rentals, messages…).

Toutes les routes sont documentées via annotations OpenAPI (`@Tag`, `@Operation`, `@ApiResponse`).

---

## Routes principales

| Méthode | URL | Auth |
|---------|-----|------|
| `POST` | `/api/auth/register` | Non |
| `POST` | `/api/auth/login` | Non |
| `GET` | `/api/auth/me` | Oui |
| `GET` | `/api/rentals` | Oui |
| `GET` | `/api/rentals/{id}` | Oui |
| `POST` | `/api/rentals` | Oui (multipart + image) |
| `PUT` | `/api/rentals/{id}` | Oui (multipart sans image) |
| `GET` | `/api/user/{id}` | Oui |
| `POST` | `/api/messages` | Oui |

---

## Liens utiles

- Guide complet : [GUIDE_PROJET.md](GUIDE_PROJET.md)
- Cours commenté local : `ressources/cours-api-chatop/` (serveur HTTP local, voir le README du dossier)
