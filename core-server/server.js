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
var config = JSON.parse(fs.readFileSync('config/config.json')); // read config
const db = mysql.createConnection(config.mysql);
var api_key = config.api_key;
var teams = config.teams;
var app = express();

const SERVER_URL = "http://teamchat.lol";

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

function recordGame(data) {
	
	cmdHandler.signal("!joinid " + id);
	console.log("line 42");
	setTimeout(function() {
		cmdHandler.signal("!saveid " + id);
		cb(id + " done");
	}, 20000)
	// take responseObj and create a database entry for this


	console.log("recording audio for id: ", id);
}

// endpoints
app.get('/match/', function (req, res) {
	console.log(req.query.args);
	var commandLine = req.query.args;
	var key = req.query.key;
	var returnData = {success: false};
	const matchIdLength = 10;

	console.log(key);

	if (req.query.state == "terminate") {
		console.log("game ended early");
	}

	if (commandLine != null) {
		if (commandLine.includes(".rofl")) {
	        // watching a completed replay -- check to see if they "own" the replay
			// get the matchid from the arguments
			var extIndex = commandLine.indexOf(".rofl");
			var matchId = commandLine.substring(extIndex-matchIdLength, extIndex);

			db.query("select r.directory, r.id from replays r left outer join sessions s on s.team_id = r.team_id where s.session_hash = ? and r.match_id = ?", [key, matchId])
			.on('result', function(data) {
				returnData = data;
				returnData.playingReplay = true;
				console.log("valid session; data = ", data);
			})
			.on('end', function() {

				// calculate the offset based on start of match and mp3 timeline TODO
				returnData.offset = 2; // TODO
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

			var result = false;
			db.query("select p.discord_id from players p left outer join sessions s on s.team_id = p.team_id where s.session_hash = ? and p.summoner_id = ?", [key, summonerId])
			.on('result', function(data){
				result = data.discord_id;
			})
			.on('end', function() {
				if (result != false) {
					// tell discord bot to record
					recordGame(result);
				}
				else {
					// failed to validate
					returnData = {success: false};
					res.send(returnData);
				}
			})
	    }
	}
})

app.get('/audio/', function(req, res){
	console.log(req);
    res.set({'Content-Type': 'audio/mpeg'});
    var readStream = fs.createReadStream(filepath);
    readStream.pipe(res);
})

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

	// test game recording
	// var id = 204781126810599424;
	// recordGame(id.toString(), function(cb) {
	// 	console.log(cb);
	// });
	// id = 279394304894435329;
	// recordGame(id.toString(), function(cb) {
	// 	console.log(cb);
	// });

})
