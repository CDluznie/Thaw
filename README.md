# Thaw

A Java REST server for real-time chatting.

## Presentation

Thaw is an application of real-time chatting with a conversation channel system accessible from a web browser.
Users can join multiple channels simultaneously, create their own channels, ...
Thaw also has a bot management system who can interact with users of the channel.

### User

Arriving at the root, the server will redirect you to the home page if you are connected, or to the login page otherwise.
To register on Thaw, you just have to enter a nickname and a password, then, you will be redirected to the home page.

The home page shows all the available channels, with their creator and the number of users connected on it.
When clicking on the name of a channel, a new tab will open with the chat window for that channel.
You can join multiple channels. 
When the page of the channel is closed, or changing website, you will be disconnected from the channel.

When clicking on the create channel button, a dialogue box will ask you to choose a name for the channel.
When validating, you will see your channel appear in the list at the homepage.

### Channel

In the home page you will find two types of channels: permanent channels, create by Thaw, that will always be present, and the temporary channels, create by users, which are deleted when the channel is empty.

When you join a channel, you have a chat window that contains the history of messages, and a window that contains the online users on the channel.

The server will periodically delete the empty channels every minutes.

A channel can contain several bot but only one of the same type. 
For example, a channel can contain only one instance of the rss-bot.

### Bots

There are 4 types of bots by default:
* thaw: Bot used to create the channels, he does not speak.
* rss-bot: Displays the RSS feed of a certain URL, define at the creation, in response to the keyword 'RSS'.
* git-bot: Displays the commits of a certain repository, define at the creation, in response to the keyword 'commits'.
* github-bot: Displays information about a github project, define at the creation. The
keyword 'github' displays all the available commands of the bot and their return values.

Bots can not interact with each other.

### REST API

Because server resources are REST-organized, you can interact with the server (retrieve or post data) through HTTP requests. 
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
* Delete binaries and doc : `ant clean`
