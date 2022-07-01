CREATE TABLE IF NOT EXISTS "users" (
  "user_id" SERIAL PRIMARY KEY NOT NULL,
  "email" varchar UNIQUE NOT NULL,
  "login" varchar NOT NULL,
  "name" varchar,
  "birthday" date NOT NULL
);

CREATE TABLE IF NOT EXISTS "films" (
  "film_id" SERIAL PRIMARY KEY NOT NULL,
  "name" varchar NOT NULL,
  "description" varchar NOT NULL,
  "release_date" date NOT NULL,
  "duration" int NOT NULL
);

CREATE TABLE IF NOT EXISTS "film_genre" (
  "film_id" long PRIMARY KEY,
  "genre_id" int NOT NULL
);

CREATE TABLE IF NOT EXISTS "genres" (
  "genre_id" int PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS "film_rating" (
  "film_id" long PRIMARY KEY,
  "rating_id" int
);

CREATE TABLE IF NOT EXISTS "rating" (
  "rating_id" int PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS "film_liked_users" (
  "film_id" long PRIMARY KEY,
  "user_id" long
);

CREATE TABLE IF NOT EXISTS "friendship" (
  "user_id" long,
  "friend_id" long,
  "status_id" int
);

CREATE TABLE IF NOT EXISTS "status_friendship" (
  "status_id" int PRIMARY KEY,
  "name" varchar NOT NULL
);

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("genre_id");

ALTER TABLE "film_rating" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "film_rating" ADD FOREIGN KEY ("rating_id") REFERENCES "rating" ("rating_id");

ALTER TABLE "film_liked_users" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "film_liked_users" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friendship" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friendship" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("user_id");

ALTER TABLE "friendship" ADD FOREIGN KEY ("status_id") REFERENCES "status_friendship" ("status_id");
