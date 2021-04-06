CREATE TABLE MOVIES(
    ID SERIAL PRIMARY KEY,
    imdbID VARCHAR(15),
    title VARCHAR(50) NOT NULL,
    duration TIME NOT NULL
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
    screening_time TIMESTAMP NOT NULL
);

CREATE TABLE RESERVATIONS(
    ID SERIAL PRIMARY KEY,
    screeningID INT NOT NULL REFERENCES SCREENINGS(ID),
    name TEXT NOT NULL, surname TEXT NOT NULL,
    status VARCHAR(10) NOT NULL,
    reservation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SEATS(
    ID SERIAL PRIMAR KEY,
    reservationID INT NOT NULL REFERENCES RESERVATIONS(ID),
    row_num INT NOT NULL,
    seat_index INT NOT NULL,
    price FLOAT NOT NULL);

INSERT INTO MOVIES (imdbID, title, duration)
VALUES
      ('tt1375666', 'Inception', '2:28'),
      ('tt0076759', 'Star Wars: Episode IV - A New Hope', '2:01'),
      ('tt0083658', 'Blade Runner', '1:57'),
      ('tt1856101', 'Blade Runner 2049', '2:44'),
      ('tt0110912', 'Pulp Fiction', '2:34'),
      ('tt0120737', 'The Lord of the Rings: The Fellowship of the Ring','2:58'),
      ('tt0060196', 'The Good, the Bad and the Ugly', '2:58'),
      ('tt6751668', 'Parasite', '2:12'),
      ('tt3748528', 'Rouge One: A Star Wars Story', '2:13');

INSERT INTO HALLS (name, row_num, column_num)
VALUES
    ('Main hall', 18, 30),
    ('Studio hall', 12, 20),
    ('Small hall', 10, 15);

INSERT INTO SCREENINGS (hallID, movieID, screening_time)
VALUES
    (1, 1,to_timestamp(1619701200)),
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
    (3, 8,to_timestamp(1616961600)),
    (3, 8,to_timestamp(1606961600));

INSERT INTO RESERVATIONS (screeningid, name, surname, status)
VALUES
    (1, 'TestName1', 'TestName1', 'PAID'),
    (1, 'TestName2', 'TestName2', 'PAID'),
    (1, 'TestName3', 'TestName3', 'PAID'),
    (1, 'TestName4', 'TestName4', 'PAID');

INSERT INTO SEATS (reservationid, row_num, seat_index, price)
VALUES
    (1, 0, 0, 18.0),
    (1, 0, 1, 18.0),
    (1, 0, 2, 18.0),
    (1, 0, 3, 18.0),
    (2, 1, 3, 25.0),
    (2, 1, 4, 15.0),
    (2, 1, 5, 25.0),
    (3, 7, 8, 25.0),
    (3, 7, 9, 25.0),
    (3, 7, 10, 25.0),
    (4, 4, 7, 25.0),
    (4, 4, 8, 25.0),
    (4, 4, 9, 25.0),
    (4, 4, 10, 25.0);

