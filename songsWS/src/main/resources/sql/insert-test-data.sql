-- SET DATABASE SQL SYNTAX ORA TRUE;
DROP TABLE IF EXISTS "songs";
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "songLists";
DROP TABLE IF EXISTS "songLists_songs";
CREATE TABLE "songs" ("id" INTEGER IDENTITY PRIMARY KEY,"title" VARCHAR(100) NOT NULL,"artist" VARCHAR(100),"label" VARCHAR(100),"released" INTEGER);
CREATE TABLE "user" ("userId" VARCHAR(50) NOT NULL,"password" VARCHAR(50) NOT NULL,"firstName" VARCHAR(50) NOT NULL,"lastName" VARCHAR(50) NOT NULL,PRIMARY KEY ("userId"));
CREATE TABLE "songLists" ("id" INTEGER IDENTITY NOT NULL,"ownerId" VARCHAR(50) NOT NULL,"name" VARCHAR(50) NOT NULL,"isPrivate" BOOLEAN NOT NULL,PRIMARY KEY ("id"),FOREIGN KEY ("ownerId") REFERENCES "user"("userId"));
CREATE TABLE "songLists_songs"("songListsId" INTEGER NOT NULL,"songsId" INTEGER NOT NULL,PRIMARY KEY ("songListsId", "songsId"));

INSERT INTO "songs" ("title", "artist", "label", "released") VALUES ('Straight Outta Compton', 'N.W.A.', 'Ruthless', '1988'),('Fuck tha Police', 'N.W.A.', 'Ruthless', '1988'),('Gangsta Gangsta', 'N.W.A.', 'Ruthless', '1988'),('If It Aint Ruff', 'N.W.A.', 'Ruthless', '1988'),('Parental Discretion Iz Advised', 'N.W.A.', 'Ruthless', '1988'),('8 Ball (Remix)', 'N.W.A.', 'Ruthless', '1988'),('Something Like That', 'N.W.A.', 'Ruthless', '1988'),('Express Yourself', 'N.W.A.', 'Ruthless', '1988'),('Comptons N the House (Remix)', 'N.W.A.', 'Ruthless', '1988'),('I Aint tha 1', 'N.W.A.', 'Ruthless', '1988');
INSERT INTO "user" ("userId", "password", "firstName", "lastName") VALUES ('mmuster', 'pass1234', 'Maxime', 'Muster'),('eschuler', 'pass1234', 'Elena', 'Schuler');
INSERT INTO "songLists" ("ownerId", "name", "isPrivate") VALUES ('mmuster', 'list1', true),('mmuster', 'list1', true),('mmuster', 'list2', true),('eschuler', 'list3', true),('eschuler', 'list4', true);
INSERT INTO "songLists_songs" ("songListsId", "songsId") VALUES (1,3), (1,5), (2,1), (2,2), (3,3), (3,6), (4,1), (4,4);

-- SHUTDOWN IMMEDIATELY;
COMMIT ;