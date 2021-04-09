CREATE TABLE MOVIES(
    id SERIAL PRIMARY KEY,
    imdb_id VARCHAR(15),
    title VARCHAR(50) NOT NULL,
    duration TIME NOT NULL
);


CREATE TABLE HALLS(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    row_num INT NOT NULL,
    column_num INT NOT NULL
);


CREATE TABLE SCREENINGS(
    id SERIAL PRIMARY KEY,
    hall_id INT NOT NULL REFERENCES HALLS(id),
    movie_id INT NOT NULL REFERENCES MOVIES(id),
    screening_time TIMESTAMP NOT NULL
);

CREATE TABLE RESERVATIONS(
    id SERIAL PRIMARY KEY,
    screening_id INT NOT NULL REFERENCES SCREENINGS(id),
    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    status VARCHAR(10) NOT NULL,
    reservation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SEATS(
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL REFERENCES RESERVATIONS(id),
    row_num INT NOT NULL,
    seat_index INT NOT NULL,
    price NUMERIC(6,2) NOT NULL);

INSERT INTO MOVIES (imdb_id, title, duration)
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
    ('Small hall', 5, 7);

INSERT INTO SCREENINGS (hall_id, movie_id, screening_time)
VALUES
    (1, 1,to_timestamp(1619701200)),
    (1, 1,to_timestamp(1619377200)),
    (2, 2,to_timestamp(1619366400)),
    (2, 2,to_timestamp(1619377200)),
    (3, 3,to_timestamp(1619366400)),
    (3, 3,to_timestamp(1619377200)),
    (1, 4,to_timestamp(1619366400)),
    (1, 4,to_timestamp(1619542800)),
    (2, 5,to_timestamp(1619532000)),
    (2, 5,to_timestamp(1619544600)),
    (3, 6,to_timestamp(1619447400)),
    (3, 6,to_timestamp(1619458200)),
    (2, 6,to_timestamp(1619542800)),
    (3, 6,to_timestamp(1619542800)),
    (1, 7,to_timestamp(1619607600)),
    (1, 8,to_timestamp(1619620200)),
    (1, 9,to_timestamp(1619632800)),
    (2, 8,to_timestamp(1619607600)),
    (2, 9,to_timestamp(1619620200)),
    (2, 7,to_timestamp(1619632800)),
    (3, 9,to_timestamp(1619607600)),
    (3, 7,to_timestamp(1619620200)),
    (3, 8,to_timestamp(1619632800)),
    (3, 8,to_timestamp(1606961600));

INSERT INTO RESERVATIONS (screening_id, name, surname, status)
VALUES
    (1, 'TestName1', 'TestName1', 'Paid'),
    (1, 'TestName2', 'TestName2', 'Paid'),
    (1, 'TestName3', 'TestName3', 'Paid'),
    (1, 'TestName4', 'TestName4', 'Paid');

INSERT INTO SEATS (reservation_id, row_num, seat_index, price)
VALUES
    (1, 0, 0, 18.0),
    (1, 0, 1, 18.0),
    (1, 0, 2, 18.0),
    (1, 0, 3, 18.0),
    (2, 1, 3, 25.0),
    (2, 1, 4, 12.5),
    (2, 1, 5, 25.0),
    (3, 7, 8, 25.0),
    (3, 7, 9, 25.0),
    (3, 7, 10, 25.0),
    (4, 4, 7, 25.0),
    (4, 4, 8, 25.0),
    (4, 4, 9, 25.0),
    (4, 4, 10, 25.0);

