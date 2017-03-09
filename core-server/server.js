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

function recordGame(id, cb) {
	//console.log(responseObj); 204781126810599424 279394304894435329
	cmdHandler.signal("!joinid " + id);
	console.log("line 42");
	setTimeout(function() {
		cmdHandler.signal("!saveid " + id);
		cb(id + " done");
	}, 20000)
	// take responseObj and create a database entry for this

	// var spinner = new Spinner('Game in progress - Downloading replay.. %s');
	// spinner.setSpinnerDelay(100);
	// spinner.setSpinnerString('┤┘┴└├┌┬┐');
	// spinner.start();
	console.log("recording audio for id: ", id);
}

// endpoints
app.get('/match/', function (req, res) {
	console.log(req.query.args);
	var commandLine = req.query.args;
	var key = req.query.key;

	if (commandLine.includes(".rofl")) {
        // watching a completed replay -- check to see if they "own" the replay
		
    }
    else if (commandLine.includes("spectator")) {
        // spectator
        if (commandLine.includes("lol.riotgames.com")) {
            // spectating on the live server grid -> return false
            return -1;
        }
    }
    else {
        // no .rofl or spectator arguments
        return -1;
    }


	// db.query("select ")
	// res.send()
	//
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
