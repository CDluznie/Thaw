# Thaw

A Java REST server for real-time chatting.

## Presentation

todo
The server is accessible from any web browser and work with a system of channels.
The users can join simultaneously many channels and create their own.
Some channels have bots who can talk and interact with present users.

### Todo sub

At the root, the server will redirect users to login page and ask for a basic HTTP authentification before joining the channels.

### REST API

todo
The details of the API REST of the server is given in file [docs/rest_api.txt](./docs/rest_api.txt)

## Requirements

* Java 9
* Vert.x
* SQLite

See *libs* for all dependences

## Usage

* Compilation : `ant`
* Run : `java -jar thaw.jar`
* Generate javadoc : `ant javadoc`
* Delete binaries : `ant clean`
