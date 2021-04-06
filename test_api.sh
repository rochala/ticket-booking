echo "TICKET-BOOKING API TEST\n"

echo "User selects day and time when she would like to see the movies in range 2021-04-27 to 2021-04-28 (range in unix timestamp in milis)\n"
echo "curl  http://localhost:8080/api/screenings/1616695200000/1616695200000"

curl  http://localhost:8080/api/screenings/1619474400000/1619560800000| python3 -m json.tool

echo "\nUser chooses LOTR: The Fellowship of the Ring with screeningID: 14 and screeningTime: 2021-04-27 19:00:00.0\n"
echo "curl  http://localhost:8080/api/screenings/details/14"
curl  http://localhost:8080/api/screenings/details/14 | python3 -m json.tool

echo "\nUser chooses seats and gives the name of the person doing the reservation\n"
echo '{"screeningID": 14, "name": "Gandalf", "surname": "Szary", "seats": [
    {"row": 3, "index": 1, "ticketType": "adult"},
    {"row": 3, "index": 2, "ticketType": "child"},
    {"row": 3, "index": 3, "ticketType": "child"},
    {"row": 3, "index": 4, "ticketType": "adult"}
    ]}'

curl -H "Content-type: application/json" -X POST -d '{"screeningID": 14, "name": "Gandalf", "surname": "Szary", "seats": [ {"row": 3, "index": 1, "ticketType": "adult"}, {"row": 3, "index": 2, "ticketType": "child"}, {"row": 3, "index": 3, "ticketType": "child"}, {"row": 3, "index": 4, "ticketType": "adult"} ]}' http://localhost:8080/api/reservations | python3 -m json.tool
echo "Seats are reserved"
