// load basic window functions
function dragResize(edge){
	overwolf.windows.getCurrentWindow(function(result){
		if (result.status=="success"){
			overwolf.windows.dragResize(result.window.id, edge);
		}
	});
};

function dragMove(){
	overwolf.windows.getCurrentWindow(function(result){
		if (result.status=="success"){
			overwolf.windows.dragMove(result.window.id);
		}
	});
};

function closeWindow(){
	overwolf.windows.getCurrentWindow(function(result){
		if (result.status=="success"){
			overwolf.windows.close(result.window.id);
		}
	});
};


var audiosync = new OverwolfPlugin("audiosync", true);
audiosync.initialize(function(status) {
	if (status == false) {
		return;
	}

	console.log("audiosync plugin is initialized");

	// have the plugin determine the command line arguments of league of legends
	// then obtain the url of the corresponding game's mp3 file
	// audiosync.get().gameURL(function(cb) {
	//
	// })
	$('body').append('<audio class="audioDemo" controls oncanplay="console.log(\'ready to play\')" src="http://xddddd.ddns.net/lolspec/2uKTtTpLHnDv5O.mp3"></audio>');
	// audiosync.get().timeSeek.addListener(function(newtime) {
	// 	// seeked to a new time, readjust audio accordingly
	//
	// })
});

/*
var plugin = new OverwolfPlugin("testplugin", true);
plugin.initialize(function(status) {
	if (status == false) {
		document.querySelector('#title').innerText = "Plugin couldn't be loaded??";
		return;
	}

	plugin.get().add(5, 4, function(result) {
    	console.log("5 + 4 = " + result);
  	});

	plugin.get().add(1, 2, function(result) {
    	console.log("1 + 2 = " + result);
  	});

	plugin.get().getShpit(function(result) {
		console.log(result);
	});

});
*/

// onNewEvents dont appear in league of legends replays for some reason
// overwolf.games.events.onNewEvents.addListener(function(newEvents) {
// 	console.log("new events");
// 	console.log(newEvents);
// })

// which features we are interested in receiving from the provider
var features = [
	'matchState',
	'teams'
	// 'summoner_info',
	// 'gameMode',
	// 'spellsAndAbilities',
	// 'deathAndRespawn',
	// 'kill',
	// 'assist',
	// 'minions'
];

function setFeatures(numRetries) {
	if (numRetries > 5) {
		console.log("failed to get features from provider -- exceeded 5 retries");
		return;
	}
	overwolf.games.events.setRequiredFeatures(features, (info) => {
		if (info.status == "error")	{
			console.log("Could not set required features: " + info.reason);
			console.log("Trying again in 2 seconds");
			window.setTimeout(function() {setFeatures(numRetries + 1)}, 2000);
			return;
		}
		console.log("Set required features:");
		console.log(JSON.stringify(info));
	});
}
setFeatures(1);

var replayBegan = 1488340510000;
var audioBegan = Date.parse("2017-02-28T19:53:38.306-08:00");
var offsetMillis = replayBegan-audioBegan;
var offset = offsetMillis/1000 + 60;

// notofications when the game starts and finishes
overwolf.games.events.onInfoUpdates2.addListener(function(infoUpdateChange) {
	console.log("info update:", infoUpdateChange);
	if (infoUpdateChange.feature == "matchState") {

		if (infoUpdateChange.info.game_info.matchStarted == "True") {
			console.log("help me terry");
			var currentTimestamp = new Date().getTime();
			if (audiosync.initialized() == true) {
				// initialize the beginning of the (watching) match with the plugin
				audiosync.get().setMatchStartTime(currentTimestamp, function(callback) {
					$('.audioDemo')[0].currentTime = offset;
					$('.audioDemo')[0].play();
					console.log(callback);
				});
			}
		}

		// if (infoUpdateChange.info.game_info.matchOutcome == "win" ||
		// 			infoUpdateChange.info.game_info.matchOutcome == "lose") {
		// 	// game is closed
		//
		// 	window.close();
		// }
	}

	if (infoUpdateChange.feature == "teams") {
		// do some other stuff here TODO
		var info = infoUpdateChange.info.game_info.teams;
		var decoded = JSON.parse(decodeURI(info));

		var red_team = decoded.splice(5)
		var blue_team = decoded;

		console.log("blue_team = ", blue_team);
		console.log("red_team = ", red_team);
	}
})

// if the game is terminated
overwolf.games.onGameInfoUpdated.addListener(function(gameInfoChangeData) {
	if (gameInfoChangeData.gameInfo == null) {
		window.close();
	}
})

// overwolf.games.events.getInfo(cb =>
// 	{
// 		var info = cb.res.game_info.teams;
// 		var decoded = JSON.parse(decodeURI(info));
//
// 		var red_team = decoded.splice(5)
// 		var blue_team = decoded;
//
// 		console.log("blue_team = ", blue_team);
// 		console.log("red_team = ", red_team);
// 	}
// )
