var audioplugin;
var features = [
	'summoner_info',
	'gameMode',
	'teams', // only works in spectator mode
	'matchState'
];

$(document).ready(function() {
	// variables
	key = getAuth();
	var playingMatch = false,
		playingReplay = false;
	// var replayBegan = 1488489461479;
	// var audioBegan = 12912983712;
	// var offsetMillis = replayBegan-audioBegan;
	var deltaBeforeSeeking = 2; // league of legends spectator begin delay
	// var offset = offsetMillis/1000 - offsetBeforeSeeking;
	var first_timeseek = true;
	var audiosync = new OverwolfPlugin("audiosync", true);


	function setFeatures(numRetries) {
		if (numRetries > 500) {
			console.log("failed to get features from provider -- exceeded 500 retries");
			return;
		}
		overwolf.games.events.setRequiredFeatures(features, (info) => {
			if (info.status == "error")	{
				//console.log("Could not set required features: " + info.reason);
				console.log("setting features..");
				window.setTimeout(function() {setFeatures(numRetries + 1)}, 50);
				return;
			}
			console.log("set required features: ", features);
		});
	}

	function registerEvents() {
		// general events errors
		overwolf.games.events.onError.addListener(function(info) {
			console.log("Error: ", info);
		});

		overwolf.games.events.onInfoUpdates2.addListener(function(info) {
			if (info.feature == "summoner_info") {
				console.log(info);
			}

			if (info.feature == "matchState") { // a matchState event is triggered
				if (info.info.game_info.matchStarted == "True") {
					var currentTimestamp = new Date().getTime();
				}
				else if (info.info.game_info.matchOutcome != null) {
					$.ajax({
						url: 'http://teamchat.lol:3501/match/',
						dataType: 'json',
						type: 'post',
						cache: false,
						data: ({key: key, summonerId: summonerId, state: "end"}),
						success: function(data) {
							// can now safely close the overwolf app
							if (data.success == true) {
								window.close();
							}
							else {
								// show something on the front end -- the post didn't works
								window.close();
							}
						},
					});
				}
			}
		});
		// an event is triggered
		overwolf.games.events.onNewEvents.addListener(function(info) {
			console.log("EVENT FIRED: ", info);
		});

		console.log("finished registering events");

		// set features after registering events
		setFeatures(1);
	}

	function startMatchUI() {
		// idk do some stuff here TODO
	}

	function startReplayUI() {
		overwolf.windows.obtainDeclaredWindow("audio", function() {
			overwolf.windows.restore("audio", function() {
				overwolf.windows.getOpenWindows(function() {
					overwolf.windows.sendMessage("audio", "file_info", {src: mp3_src, offset: offset}, function(cb) {
						console.log("howler initiated", cb);
						audiosync.get().onTimeSeek.addListener(function(newtime) {
							console.log("timeseek", newtime);
							// only do this on the first timeseek
							if (first_timeseek == true) {
								first_timeseek = false;
								// change the offset now by 1
								offset -= 1;
							}
							// change the time in the audio accordingly
							overwolf.windows.sendMessage("audio", "timeseek", {newtime: newtime, offset: offset}, function() {
								console.log("timeseek sent", newtime);
							})
						})
					})
				})
			});
		})
	}

	audiosync.initialize(function(status) {
		if (status == false) {
			console.log("plugin failed to load");
			return;
		}
		// alow global access to the plugin for testing TODO
		audioplugin = audiosync;
		console.log("audiosync plugin is initialized");

		audiosync.get().getLeagueArgs(args => {
			console.log(args);
			if (args == null) {
				console.log("not in game");
			}
			else {
				registerEvents();
				$.ajax({
					url: 'http://teamchat.lol:3501/match/',
					dataType: 'json',
					type: 'get',
					cache: false,
					data: ({key: key, args: args}),
					success: function(data) {
						// register listeners and events
						if (data.playingMatch == true) {
							playingReplay = false;
							playingMatch = true;
							// start visuals/connection with bot
							startMatchUI();
						}
						else if (data.playingReplay == true) {
							playingReplay = true;
							playingMatch = false;
							// start
							startReplayUI(data.mp3, data.offset);
						}
					},
					error: function() {
						// do something if error
					}
				});
			}
		});
	});

	// called whenever the game window is changed
	overwolf.games.onGameInfoUpdated.addListener(function(gameInfoChangeData) {
		console.log("GameInfoUpdated", gameInfoChangeData);

		// handle window size changes here -- tell overlay where to go
		// TODO

		// a game info was updated but the gameInfo is null
		if (gameInfoChangeData.gameInfo == null) { // game is closed but not match outcome
			if (playingMatch) {
				$.ajax({
					url: 'http://teamchat.lol:3501/match/',
					dataType: 'json',
					type: 'post',
					cache: false,
					data: ({key: key, summonerId: summonerId, state: "close"}),
					success: function(data) {
						window.close();
					}
				});
			}
			else {
				window.close();
			}
			console.log("game closed");
		}
	});
});
