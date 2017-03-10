// core-server

const https = require('https');
const figlet = require('figlet');
const Spinner = require('cli-spinner').Spinner;
const fs = require('fs');
const mysql = require('mysql');
const bodyParser = require('body-parser');
const cmdHandler = require('./cmdHandler.js');
const express = require('express');
const cors = require('cors');
const unirest = require('unirest');
var config = JSON.parse(fs.readFileSync('config/config.json')); // read config
const db = mysql.createConnection(config.mysql);

var api_key = config.api_key;
//var teams = config.teams;
var app = express();

var activeGames = [];

const SERVER_URL = "http://teamchat.lol";
const MAX_RETRIES = 10;

app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies
app.use(cors());
process.stdout.write('\033c'); //clear the console


//functions
function authenticate(key, callback){
	var user = {loggedIn: false, id: 0, email: ''}
	db.query('select u.* from sessions s left outer join users u on s.user_id = u.id where s.session_hash = ?', [key])
	.on('result', function(data){
		user.loggedIn = true
		user.id = data.id
		user.email = data.email
	})
	.on('end', function(){
		console.log(user)
		callback(user)
	})
}

function getSpectatorGameInfo(summonerId, cb) {
	unirest.get("https://na.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/NA1/"+summonerId+"?api_key="+api_key)
	.headers({'Accept': 'application/json', 'Content-Type': 'application/json'})
	.end(function (response) {
		cb(response.body);
	});
}

// function recordGame(data) {
//
// 	// discord_id and summonerId
// 	cmdHandler.signal("!joinid " + data.summonerId)
//
// 	setTimeout(function() {
// 		cmdHandler.signal("!saveid " + id);
// 		cb(id + " done");
// 	}, 20000)
// 	// take responseObj and create a database entry for this
//
//
// 	console.log("recording audio for id: ", id);
// }

function checkGame(summonerId, team_id, success) {
	var gameInfo;

	var doFind = function(retries) {
		if (retries > MAX_RETRIES) {
			console.log("exceeded max retries, not recording ", summonerId);
			return;
		}
		getSpectatorGameInfo(summonerId, function(data) {
			console.log(data);
			if (data.status != null && data.status.status_code == 403) {
				console.log("api key bad");
				return;
				// send ixenbay an email TODO
			}
			else if (data.status != null && data.status.status_code == 404) {
				setTimeout(function() {
					doFind(retries+1);
				}, 1500)
			}
			else { // obtained match spectate data -> check all teammates
				var players = [];
				gameInfo = data;
				console.log(gameInfo);
				var teamNumber = 0;
				var participants = data.participants;

				// get the team number
				for (var i = 0; i < participants.length; i++) {
					if (participants[i].summonerId == summonerId) {
						teamNumber = participants[i].teamId;
						break;
					}
				}

				db.query("select summoner_id from players where team_id = ?", [team_id])
				.on('result', function(data) {
					players.push(data);
				})
				.on('end', function() {

					for (var i = 0; i < players.length; i++) { // each player on a Teamchat team
						var hasTeammate = false;
						for (var j = 0; j < participants.length; j++) { // each participant in a game
							if (participants[j].summonerId == players[i].summoner_id) {
								hasTeammate = true;
								if (participants[j].teamId != teamNumber) {
									success(false);
									return;
								}
								else { // teamchat teammate is on the same team
									break;
								}
							}
						}
						if (hasTeammate == false) {
							success(false);
							return;
						}
					}
					success(true, gameInfo.gameId); // callback true with gameId
				})
			}
		})
	}

	doFind(1);

}

// adds the game to an array and gets the start time later;
function addToActiveGames(matchId, summonerId, team_id) {

	for (var i = 0; i < activeGames.length; i++) {
		if (activeGames[i].matchId == matchId) {
			return; // already has this match
		}
	}

	makeUniqueId(function(dirname) {
		console.log("directory name: " + dirname);
		db.query("insert into replays (match_id, team_id, start_time, directory) values (?,?,?,?)", [matchId, team_id, 0, dirname])
		console.log("updated matchId, team_id, directory ", matchId, team_id, dirname);
		// tell discord bot to join the server
		cmdHandler.signal("!joinid "+summonerId+" "+dirname);


		// the matchid, the summoner who began the recording, 0 start time, folder where its being saved
	 	activeGames.push({matchId: matchId, summonerId: summonerId, startTime: 0, directory: dirname}); // push to array

		setTimeout(function() {
			getSpectatorGameInfo(summonerId, function(data) {
				var startTime = data.gameStartTime;
				for (var i = 0; i < activeGames.length; i++) {
					if (activeGames[i].matchId == matchId) {
						db.query("UPDATE replays SET start_time = ? WHERE match_id = ?", [startTime, matchId])
						console.log("updated match start time for match: ", matchId);
						return;
					}
				}
			})
		}, 70000); // wait 70 seconds before obtaining the gameStartTime to allow it to appear
	});
}

// random string
function makeUniqueId(returnValue) {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < 5; i++ ) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
	}

	var count = 0;
	db.query("select count(*) as cnt from replays where directory = ?", [text])
	.on('result', function(data) {
		count = data.cnt;
	})
	.on('end', function() {
		if (count > 0) {
			makeUniqueId(returnValue);
		}
		else {
			returnValue(text);
		}
	})
}

// endpoints
app.get('/match/', function (req, res) {
	console.log(req.query.args);
	var commandLine = req.query.args;
	var key = req.query.key;
	var returnData = {success: false};
	const matchIdLength = 10;

	console.log(key);

	if (commandLine != null) {
		if (commandLine.includes(".rofl")) {
	        // watching a completed replay -- check to see if they "own" the replay
			// get the matchid from the arguments
			var extIndex = commandLine.indexOf(".rofl");
			var matchId = commandLine.substring(extIndex-matchIdLength, extIndex);

			db.query("select r.directory, r.id, r.start_time from replays r left outer join sessions s on s.team_id = r.team_id where s.session_hash = ? and r.match_id = ?", [key, matchId])
			.on('result', function(data) {
				returnData = data;
				returnData.playingReplay = true;
				console.log("valid session; data = ", data);
			})
			.on('end', function() {

				// calculate the offset based on start of match and mp3 timeline TODO

				var replayBegan = returnData.start_time;
				var audioBegan = 1489155868949;
				var offset = (replayBegan-audioBegan)/1000;
				returnData.offset = offset;
				// form a .php link and send that TODO
				returnData.src = SERVER_URL+"/core-server/recording/"+returnData.directory+"/audio.mp3";
				returnData.timeline = SERVER_URL+"/core-server/recording/"+returnData.directory+"/timestamp.txt";
				res.send(returnData);
				if (returnData.success != false) { // valid mp3 request
					db.query("insert into views (replay_id) values (?)", [returnData.id]);
				}
			});
	    }
	    else if (commandLine.includes("spectator")) {
	        // spectator
	        if (commandLine.includes("lol.riotgames.com")) {
	            // spectating on the live server grid -> return false
	            res.send({message: "spectating live games not supported"});
	        }
			else {
				// watching a server hosted game
				res.send({message: "watching hosted replays is not yet supported"});
			}
	    }
	    else {
	        // no .rofl or spectator arguments
	        // playing a live game
			console.log("playing a real game");
			var index = commandLine.indexOf("=="); // find the is-equals in the args
			const charsBetweenEquals = 3; // 3 characters between the == and first digit
			var summonerIdToEnd = commandLine.substring(index+3);
			var summonerId = summonerIdToEnd.substring(0, summonerIdToEnd.indexOf("\""));
			console.log(summonerId);
			var result = false;
			db.query("select p.discord_id, s.team_id from players p left outer join sessions s on s.team_id = p.team_id where s.session_hash = ? and p.summoner_id = ?", [key, summonerId])
			.on('result', function(data) {
				result = {};
				result.discord_id = data.discord_id;
				result.team_id = data.team_id;
			})
			.on('end', function() {
				if (result != false) {
					// tell discord bot to record
					result.summonerId = summonerId;
					console.log("line 205 ", result);
					checkGame(summonerId, result.team_id, function(success, matchId = null) {
						if (success == true) { // all teammates on same team
							res.send({playingMatch: true});
							console.log("all players on team and we good to go");

							// this function will start the discord bot as well
							addToActiveGames(matchId, summonerId, result.team_id);
						}
						else { // not all teammates in same game
							returnData = {success: false, message: "not all teammates on your team"};
							res.send(returnData)
							console.log("not all playing are in the match or on the same team");
						}
					});
				}
				else { // invalid hash
					console.log("invalid hash");
					returnData = {success: false};
					res.send(returnData);
				}
			})
	    }
	}
})

app.get('/endgame/', function (req, res) {
	console.log("endGame called");
	var key = req.query.key;
	var returnData = {success: false};

	console.log(req.query);

	if (req.query.state == "terminate") { // whole team left the game early
		// TODO check if other teammates are still in game before deleting the record

		console.log("game ended early");
		// authenticate
		var matchId = null;
		var teamId = null;
		db.query("select r.match_id, s.team_id from replays r left outer join sessions s on s.team_id = r.team_id where s.session_hash = ?", [key])
		.on('result', function(data) {
			matchId = data.match_id;
			teamId = data.team_id;
		})
		.on('end', function() {

			if (matchId != null) {
				// end the game and STOP the recording
				for (var i = 0; i < activeGames.length; i++) {
					if (activeGames[i].matchId == matchId) {
						db.query("DELETE FROM replays WHERE replays.match_id = ? AND replays.team_id = ?", [matchId, team_id])
						console.log("deleted match: ", matchId, team_id);

						// change to leaveid
						cmdHandler.signal("!saveid "+activeGames[i].summonerId+" "+activeGames[i].directory);
						res.send({success: true});
						activeGames.splice(i, 1); // remove from the active games
						return;
					}
				}
			}
		})

	}

	if (req.query.state == "end") { // game finished with a w/l
		var matchId = null;
		var teamId = null;
		db.query("select r.match_id, s.team_id from replays r left outer join sessions s on s.team_id = r.team_id where s.session_hash = ?", [key])
		.on('result', function(data) {
			matchId = data.match_id;
			teamId = data.team_id;
		})
		.on('end', function() {
			if (matchId != null) {
				// end the game and SAVE the recording
				for (var i = 0; i < activeGames.length; i++) {
					if (activeGames[i].matchId == matchId) {
						// stop the discord bot
						cmdHandler.signal("!saveid "+activeGames[i].summonerId);

						res.send({success: true});

						activeGames.splice(i, 1); // remove from the active games
						return;
					}
				}
			}
		})
	}
})

// app.get('/audio/', function(req, res){
// 	console.log(req);
//     res.set({'Content-Type': 'audio/mpeg'});
//     var readStream = fs.createReadStream(filepath);
//     readStream.pipe(res);
// })

// listen on port 3501
app.listen(3501, function () {
	console.log('listening on port 3501')
})

// start the bot
console.log("starting discord bot...");
cmdHandler.startDiscordBot(function() {
	//var interval_ms = 5000;// how long to wait before checking if a summoner is in game
	// print ascii art to console
	figlet('LEAGUE OF LEGENDS', function(err, data) {
	    if (err) {
	        console.log('Something went wrong...');
	        console.dir(err);
	        return;
	    }
	    console.log(data)
		console.log("waiting for users to start a game..");
	});

})
