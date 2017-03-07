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

function recordGame(responseObj) {
	//console.log(responseObj);
	cmdHandler.signal("!joinid 279394304894435329");
	setTimeout(function() {
		cmdHandler.signal("!saveid 279394304894435329");
	}, 20000)
	// take responseObj and create a database entry for this
	var spinner = new Spinner('Game in progress - Downloading replay.. %s');
	spinner.setSpinnerDelay(100);
	spinner.setSpinnerString('┤┘┴└├┌┬┐');
	spinner.start();
}

// endpoints
app.get('/match/', function (req, res) {
	console.log(req.query)
	authenticate(req.query.key, function(user){
		var returnData = {authenticated: false, teams: []}
		if (user.loggedIn){
			returnData.authenticated = true
			var summonerids = ''
			var currTeam = 0
			var push = false

			var team = {}
			team.players = []
			db.query('select p.*, t.team_name from teams t left outer join players p on t.id = p.team_id where t.owner = ? order by t.id', [user.id])
			.on('result', function(data){

				if (data.team_id != currTeam){

					if (currTeam > 0){
						summonerids = summonerids.substring(0, summonerids.length-1)
						console.log(summonerids)
						unirest.get('https://na.api.pvp.net/api/lol/na/v1.4/summoner/'+summonerids+'?api_key='+config.riot_api)
						.headers({'Accept': 'application/json', 'Content-Type': 'application/json'})
						.end(function (response) {
							var summonersData = response.body
							for (var i = 0; i < summonersData.length; i++){
								team.players[i].summoner_data = summonersData[i]
							}
							returnData.teams.push(team)
							currTeam = data.team_id
							team = {}
							team.players = []
						});
					}
				}
				team.id = data.team_id
				team.name = data.team_name
				team.players.push({dbname: data.name, sumid: data.summoner_id})
				summonerids += data.summoner_id+','
			})
			.on('end', function(){

				summonerids = summonerids.substring(0, summonerids.length-1)
				console.log(summonerids)
				unirest.get('https://na.api.pvp.net/api/lol/na/v1.4/summoner/'+summonerids+'?api_key='+config.riot_api)
				.headers({'Accept': 'application/json', 'Content-Type': 'application/json'})
				.end(function (response) {
					//console.log(response.body)
					var summonersData = response.body;
					for (sum in summonersData){
						for (var i = 0; i < team.players.length; i++){
							if (team.players[i].sumid == summonersData[sum].id){
								team.players[i].summoner_data = summonersData[sum]
							}
						}

					}
					returnData.teams.push(team)
					res.send(returnData)

				});
			})
		}
		else{
			res.send(returnData)
		}

	})
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
	});
})

// listen on port 3501
app.listen(3501, function () {
	console.log('istening on port 3501')
})
