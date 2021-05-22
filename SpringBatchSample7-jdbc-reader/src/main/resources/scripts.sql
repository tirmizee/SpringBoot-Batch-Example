CREATE TABLE accounts (
	user_id serial PRIMARY KEY,
	username VARCHAR ( 50 ) UNIQUE NOT NULL,
	password VARCHAR ( 50 ) NOT NULL,
	email VARCHAR ( 255 ) UNIQUE NOT NULL,
	created_on TIMESTAMP NOT NULL,
        last_login TIMESTAMP 
);

INSERT INTO accounts(username, password, email, created_on) VALUES ('pratya', 'pratya', 'pratya@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('titmia', 'titmia', 'titmia@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('yeekha', 'yeekha', 'yeekha@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('muhamh', 'muhamh', 'muhamh@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('faluxe', 'faluxe', 'faluxe@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('asians', 'asians', 'asians@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('singai', 'singai', 'singai@hotmail.com', CURRENT_TIMESTAMP);
INSERT INTO accounts(username, password, email, created_on) VALUES ('tester', 'tester', 'tester@hotmail.com', CURRENT_TIMESTAMP);