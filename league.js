const io = require('socket.io').listen(3000);
const https = require('https');
const figlet = require('figlet');
const Spinner = require('cli-spinner').Spinner;
const fs = require('fs')

//clear the console
process.stdout.write('\033c');

// define variables
var api_key, players;
var interval_ms = 5000;// how long to wait before checking if a summoner is in game
var config = JSON.parse(fs.readFileSync('./config.json')); // read config
api_key = config.api_key;
players = config.players;

// print ascii art to console
figlet('LEAGUE OF LEGENDS', function(err, data) {
    if (err) {
        console.log('Something went wrong...');
        console.dir(err);
        return;
    }
    console.log(data)
});

function check_is_in_game(summoner, spinner) {
	//console.log("checking if [" +summoner.name+", "+summoner.region+ "] is in an active game...");
	// http request to see if there is an active game
	var options = {
	  host: "na.api.pvp.net",
	  path: "/observer-mode/rest/consumer/getSpectatorGameInfo/"+summoner.region+"/"+summoner.id+"?api_key="+api_key
	};
	https.get(options, function(response) {
		var str = "";
		var returnObj = '';
		//console.log("status code: ", response.statusCode);

		response.on('data', function(chunk) {
			str+=chunk;
		});

		response.on('end', function() {
			if (response.statusCode == 200) {
				responseObj = JSON.parse(str);
				console.log("game found!"); //" for [" +summoner.name+", "+summoner.region+ "]!");
				console.log("gameMode: " + responseObj.gameMode);
				console.log("gameType: " + responseObj.gameType);
				console.log("gameId: " + responseObj.gameId);
				console.log("observer encryptionKey: " + responseObj.observers.encryptionKey);

				spinner.clearLine(this.stream);
				spinner.stop();
				recordGame(responseObj);
			}
			else {
				// 404 error; check again in 5 seconds
				setTimeout(function() {
					check_is_in_game(summoner, spinner);
				}, 5000)
			}
		});

	}).on("error", function(e) {
		console.log("error: " + e.message);
	});
	return;
}

for (var i = 0; i < players.length; i++) {
	var obj = new Spinner({
	    text: "checking if [" +players[i].name+", "+players[i].region+ "] is in an active game.%s",
	    stream: process.stderr,
	    onTick: function(msg){
	        this.clearLine(this.stream);
	        this.stream.write(msg);
	    }
	})
	obj.setSpinnerDelay(100)
	obj.start();
	check_is_in_game(players[i], obj);
}

function recordGame(responseObj) {
	var spinner = new Spinner('Game in progress - Downloading replay.. %s');
	spinner.setSpinnerDelay(100);
	spinner.setSpinnerString('┤┘┴└├┌┬┐');
	spinner.start();
}
