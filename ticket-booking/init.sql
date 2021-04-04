
CREATE TABLE MOVIES(
    ID SERIAL PRIMARY KEY,
    imdbID VARCHAR(15),
    title VARCHAR(50) NOT NULL,
    duration INT NOT NULL
);


CREATE TABLE HALLS(
    ID SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    row_num INT NOT NULL,
    column_num INT NOT NULL
);


CREATE TABLE SCREENINGS(
    ID SERIAL PRIMARY KEY,
    hallID INT NOT NULL REFERENCES HALLS(ID),
    movieID INT NOT NULL REFERENCES MOVIES(ID),
    screening_time INT NOT NULL
);

CREATE TABLE RESERVATIONS(
    ID SERIAL PRIMARY KEY,
    screeningID INT NOT NULL REFERENCES SCREENINGS(ID),
    name TEXT NOT NULL, surname TEXT NOT NULL,
    status CHAR(10) NOT NULL,
    reservation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SEATS(
    ID SERIAL PRIMARY KEY,
    reservationID INT NOT NULL REFERENCES RESERVATIONS(ID),
    row_num INT NOT NULL,
    seat_index INT NOT NULL,
    price FLOAT NOT NULL
);

INSERT INTO MOVIES (imdbID, title, duration)
VALUES
      ('tt1375666', 'Inception', 8880000),
      ('tt0076759', 'Star Wars: Episode IV - A New Hope', 7260000),
      ('tt0083658', 'Blade Runner', 7020000),
      ('tt1856101', 'Blade Runner 2049', 9840000),
      ('tt0110912', 'Pulp Fiction', 9240000),
      ('tt0120737', 'The Lord of the Rings: The Fellowship of the Ring',10680000),
      ('tt0060196', 'The Good, the Bad and the Ugly', 10680000),
      ('tt6751668', 'Parasite', 7920000),
      ('tt3748528', 'Rouge One: A Star Wars Story', 7980000);

INSERT INTO HALLS (name, row_num, column_num)
VALUES
    ('Main hall', 18, 30),
    ('Studio hall', 12, 20),
    ('Small hall', 10, 15);

INSERT INTO SCREENINGS (hallID, movieID, screening_time)
VALUES
    (1, 1,to_timestamp(1616695200)),
    (1, 1,to_timestamp(1616706000)),
    (2, 2,to_timestamp(1616695200)),
    (2, 2,to_timestamp(1616706000)),
    (3, 3,to_timestamp(1616695200)),
    (3, 3,to_timestamp(1616706000)),
    (1, 4,to_timestamp(1616785200)),
    (1, 4,to_timestamp(1616871600)),
    (2, 5,to_timestamp(1616774400)),
    (2, 5,to_timestamp(1616787000)),
    (3, 6,to_timestamp(1616774400)),
    (3, 6,to_timestamp(1616787000)),
    (2, 6,to_timestamp(1616868000)),
    (3, 6,to_timestamp(1616868000)),
    (1, 7,to_timestamp(1616936400)),
    (1, 8,to_timestamp(1616949000)),
    (1, 9,to_timestamp(1616961600)),
    (2, 8,to_timestamp(1616936400)),
    (2, 9,to_timestamp(1616949000)),
    (2, 7,to_timestamp(1616961600)),
    (3, 9,to_timestamp(1616936400)),
    (3, 7,to_timestamp(1616949000)),
    (3, 8,to_timestamp(1616961600));

