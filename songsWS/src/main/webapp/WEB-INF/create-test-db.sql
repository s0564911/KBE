CREATE TABLE songs (
    id SERIAL PRIMARY KEY NOT NULL,
    title VARCHAR(100) NOT NULL,
    artist VARCHAR(100),
    label VARCHAR(100),
    released INTEGER);

CREATE TABLE `user` (
    `userId` VARCHAR(50) NOT NULL,
    `password` VARCHAR(50) NOT NULL,
    `firstName` VARCHAR(50) NOT NULL,
    `lastName` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`userId`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE songLists (
    id SERIAL NOT NULL,
    ownerId VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    isPrivate BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ownerId) REFERENCES user(userId)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE songLists_sons(
	songListsId INTEGER NOT NULL,
	songsId INTEGER NOT NULL,
	PRIMARY KEY (songListsId, songsId)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

INSERT INTO
    songs (title, artist, label, released)
VALUES
    ('Straight Outta Compton', 'N.W.A.', 'Ruthless', '1988'),
    ('Fuck tha Police', 'N.W.A.', 'Ruthless', '1988'),
    ('Gangsta Gangsta', 'N.W.A.', 'Ruthless', '1988'),
    ("If It Ain't Ruff", 'N.W.A.', 'Ruthless', '1988'),
    ('Parental Discretion Iz Advised', 'N.W.A.', 'Ruthless', '1988'),
    ('8 Ball (Remix)', 'N.W.A.', 'Ruthless', '1988'),
    ('Something Like That', 'N.W.A.', 'Ruthless', '1988'),
    ('Express Yourself', 'N.W.A.', 'Ruthless', '1988'),
    ("Compton's N the House (Remix)", 'N.W.A.', 'Ruthless', '1988'),
    ("I Ain't tha 1", 'N.W.A.', 'Ruthless', '1988');

INSERT INTO
    `user` (userId, password, firstName, lastName)
VALUES
    ('mmuster', 'pass1234', 'Maxime', 'Muster'),
    ('eschuler', 'pass1234', 'Elena', 'Schuler');
