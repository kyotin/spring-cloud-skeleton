-- Add new user for authentication
INSERT INTO USERS(FIRST_NAME, LAST_NAME, USER_NAME, ROLE_ID, PASSWORD)
VALUES('global', 'admin', 'global', 1, '$2a$08$fL7u5xcvsZl78su29x1ti.dxI.9rYO8t0q5wk2ROJ.1cdR53bmaVG');

INSERT INTO USERS(FIRST_NAME, LAST_NAME, USER_NAME, ROLE_ID, PASSWORD)
VALUES('admin', 'admin1', 'admin', 2, '$2a$08$fL7u5xcvsZl78su29x1ti.dxI.9rYO8t0q5wk2ROJ.1cdR53bmaVG');

INSERT INTO USERS(FIRST_NAME, LAST_NAME, USER_NAME, ROLE_ID, PASSWORD)
VALUES('user', 'user1', 'user', 3, '$2a$08$fL7u5xcvsZl78su29x1ti.dxI.9rYO8t0q5wk2ROJ.1cdR53bmaVG');