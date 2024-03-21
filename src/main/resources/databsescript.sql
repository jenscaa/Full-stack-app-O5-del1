DROP TABLE IF EXISTS calculations;
DROP TABLE IF EXISTS users;

CREATE Table users(
    user_id INT UNSIGNED UNIQUE AUTO_INCREMENT NOT NULL,
    username VARCHAR(30) UNIQUE NOT NULL,
    user_password VARCHAR(30) NOT NULL,
    PRIMARY KEY(user_id)
)ENGINE=INNODB;

CREATE Table calculations(
    calculation_id INT UNSIGNED UNIQUE AUTO_INCREMENT NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    expression VARCHAR(30) NOT NULL,
    answer VARCHAR(30),
    dato DATE,
    PRIMARY KEY(calculation_id),
    CONSTRAINT FK_calculation FOREIGN KEY(user_id)
	REFERENCES users(user_id) ON DELETE CASCADE
)ENGINE=INNODB;

INSERT INTO users VALUES(DEFAULT, 'user1', 'password123');
INSERT INTO calculations VALUES(DEFAULT, 1, '1 + 1', '1 + 1 = 2', DEFAULT);
INSERT INTO users VALUES(DEFAULT, 'user2', 'password1234');
INSERT INTO calculations VALUES(DEFAULT, 1, '1 + 1', '1 + 1 = 2', DEFAULT);