CREATE TABLE IF NOT EXISTS "USERS"
(
    "USER_ID"  int auto_increment primary key unique not null,
    "EMAIL"    varchar(20) UNIQUE                    NOT NULL,
    "LOGIN"    varchar(20)                           NOT NULL,
    "NAME"     varchar(20)                           NOT NULL,
    "BIRTHDAY" date                                  NOT NULL
);

CREATE TABLE IF NOT EXISTS "FILMS"
(
    "FILM_ID"      int auto_increment primary key not null,
    "NAME"         varchar(20)                    NOT NULL,
    "DESCRIPTION"  varchar(200) unique            NOT NULL,
    "RELEASE_DATE" date                           NOT NULL,
    "DURATION"     int                            NOT NULL
);

CREATE TABLE IF NOT EXISTS "FILM_GENRE"
(
    "FILM_ID"  int PRIMARY KEY,
    "GENRE_ID" int
);

CREATE TABLE IF NOT EXISTS "GENRES"
(
    "GENRE_ID" int auto_increment primary key not null,
    "NAME"     varchar(20)                    NOT NULL
);

CREATE TABLE IF NOT EXISTS "FILM_RATING"
(
    "FILM_ID"   int PRIMARY KEY,
    "RATING_ID" int not null
);

CREATE TABLE IF NOT EXISTS "RATING"
(
    "RATING_ID" int auto_increment primary key not null,
    "NAME"      varchar(20)                    NOT NULL
);

CREATE TABLE IF NOT EXISTS "FILM_LIKED_USERS"
(
    "FILM_ID" int PRIMARY KEY,
    "USER_ID" int
);

CREATE TABLE IF NOT EXISTS "FRIENDSHIP"
(
    "USER_ID"   int,
    "FRIEND_ID" int,
    "STATUS_ID" int
);

CREATE TABLE IF NOT EXISTS "STATUS_FRIENDSHIP"
(
    "STATUS_ID" int auto_increment primary key not null,
    "NAME"      varchar                        NOT NULL
);


ALTER TABLE "FILM_GENRE"
    ADD FOREIGN KEY ("FILM_ID") REFERENCES "FILMS" ("FILM_ID");

ALTER TABLE "FILM_GENRE"
    ADD FOREIGN KEY ("GENRE_ID") REFERENCES "GENRES" ("GENRE_ID");

ALTER TABLE "FILM_RATING"
    ADD FOREIGN KEY ("FILM_ID") REFERENCES "FILMS" ("FILM_ID");

ALTER TABLE "FILM_RATING"
    ADD FOREIGN KEY ("RATING_ID") REFERENCES "RATING" ("RATING_ID");

ALTER TABLE "FILM_LIKED_USERS"
    ADD FOREIGN KEY ("FILM_ID") REFERENCES "FILMS" ("FILM_ID");

ALTER TABLE "FILM_LIKED_USERS"
    ADD FOREIGN KEY ("USER_ID") REFERENCES "USERS" ("USER_ID");

ALTER TABLE "FRIENDSHIP"
    ADD FOREIGN KEY ("USER_ID") REFERENCES "USERS" ("USER_ID");

ALTER TABLE "FRIENDSHIP"
    ADD FOREIGN KEY ("FRIEND_ID") REFERENCES "USERS" ("USER_ID");

ALTER TABLE "FRIENDSHIP"
    ADD FOREIGN KEY ("STATUS_ID") REFERENCES "STATUS_FRIENDSHIP" ("STATUS_ID");

INSERT INTO GENRES(NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');
INSERT INTO RATING(NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
INSERT INTO STATUS_FRIENDSHIP(NAME)
VALUES ('Неподтверждена'),
       ('Подтверждена');