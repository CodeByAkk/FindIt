# Campus Lost & Found Portal — Smart Item Recovery System

A responsive Spring Boot 3 / Java 17 web application with Thymeleaf, Spring Security, Spring Data JPA, Hibernate, and MySQL. It supports student accounts, lost/found submissions, item search, image uploads, matching suggestions, claims, moderation, and database-backed notifications with optional email delivery.

## Run locally

Requirements: JDK 17+, Maven 3.8+, MySQL 8+. Create a database (or let the configured JDBC URL create it):

```sql
CREATE DATABASE campus_lost_found;
```

Set database credentials in your shell (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) or edit `src/main/resources/application.properties`, then run:

```bash
mvn spring-boot:run
```

Open <http://localhost:8081>. Hibernate creates/updates the schema. Uploaded images are stored in `uploads/`. `ddl-auto=update` is convenient for a college demo; use migrations for production.

### Docker

```bash
docker compose up --build
```

The compose setup starts MySQL and the app on port 8080. Its demo database password is for local development only.

## Demo accounts

The first startup seeds these if they do not exist:

- Student: `student@campus.edu` / `Student@123`
- Admin: `admin@campus.edu` / `Admin@123`

Change/remove demo credentials before deployment. Student registration is open; admin account creation is intentionally not exposed publicly.

## Main workflows

1. Register/sign in, submit a lost or found report, optionally attach a JPG/PNG image. New reports enter `PENDING`.
2. Admin opens **Admin console** and approves or rejects reports. Only approved reports appear in the community search.
3. Students filter by name, category, and location, open an item, and submit an ownership explanation as a claim.
4. Admin reviews claims and sets `APPROVED`/`REJECTED`. An approved claim sets its item to `CLAIMED`; admin can set it to `RETURNED` after handover.
5. Dashboard matching compares each of your approved reports with opposite-type approved items.

## Email

Notifications are always recorded in MySQL and displayed in the student's dashboard. SMTP delivery is attempted when configured; set `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, and `MAIL_STARTTLS`. Delivery failure does not discard the in-app notice.

## Matching algorithm

`MatchingService` computes a transparent 0–100 score: item-name similarity up to 45 points (token Jaccard overlap and normalized Levenshtein similarity), category exact match 25, location similarity up to 20, and date proximity up to 10. Exact dates receive 10, within two days 8, within a week 5, within two weeks 2. Only opposite-type approved items are considered; candidate scores of at least 25 are returned, ranked highest first. This is deterministic rule-based matching, not AI.

## OOP concepts for viva

- **Encapsulation:** entity fields are private; access goes through methods, and validation constrains input.
- **Inheritance:** `Student` and `Admin` extend abstract `User`; `LostItem` and `FoundItem` extend abstract `Item`. JPA maps each pair with single-table inheritance.
- **Abstraction:** abstract `User`/`Item` define shared state and contract; controllers call services rather than persistence details.
- **Polymorphism:** `Item.getType()` is implemented differently by `LostItem` and `FoundItem`; matching handles both through the common `Item` abstraction.
- **Interfaces:** Spring Data repository interfaces inherit CRUD/query behavior from `JpaRepository`; `UserDetailsService` is implemented for authentication.
- **Exception handling:** upload/date validations surface readable messages; security and MVC handle invalid access and missing resources.
- **Collections:** services use lists, sets, and streams to score, filter, and rank candidate matches.

## Project layout

- `controller`: MVC routes and form handling
- `service`: matching, item workflow, notifications, user lookup
- `entity`: users, item hierarchy, claims, notifications and enums
- `repository`: Spring Data JPA interfaces
- `config`: security, file resources and demo seed data
- `src/main/resources/templates`: Thymeleaf pages
- `src/main/resources/static`: CSS and JavaScript

## Notes

- Spring Security protects student routes; `/admin/**` requires the `ADMIN` role. Passwords use BCrypt.
- For a production deployment, add database migrations, CSRF-aware API integrations, email retry handling, rate limits, and stronger upload content inspection.
- Sample item data is seeded once when the item table is empty.
