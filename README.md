# Chess
Online chess game between 2 players or a player and a computer.

## Requirements
JRE/JDK 8+
## How to use
Download the .jar file in the /dist folder, double click or run via command prompt

## How it works
When the game starts, you have an option to proceed as a guest or to sign up or sign in (Guest has limited features).
When proceeding as a registered user, a listening thread is initiated, taking in strings from the server (hosted on a remote machine), translating them and performing the actions needed.
Chess piece coordinates are sent as a string of numbers, translated by the server and forwarded to the other player, or operated on directly on local machine when it's versus the computer.
