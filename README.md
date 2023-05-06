# battleships
Modern Java Technologies Course Project 📚

The project is a multiplayer online game for the famous battleship game. The game also supports a spectator mode, so you can spectate any game that is being played in real time.

Ideas and Tech used:
Java NIO API is chosen as the network communication API for the reason of scalability. The API uses the concepts of selector and channels and performs better than java.net for small and short communications between the server and the clients.

All passwords are encrypted using salt in one of the files and are never exposed as strings in the String Pool, so you do not have to worry about having your password stolen from this app and taken to your bank app.

The project is not fully tested currently, as it needs integration tests and some methods with dependencies have not been mock tested.

The server is secured to not crash by any disconnections or attemps by any client, the reverse functionality has not been developed.
