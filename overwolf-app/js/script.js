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
	var audioReady = false;
	// var replayBegan = 1488489461479;
	// var audioBegan = 12912983712;
	// var offsetMillis = replayBegan-audioBegan;
	var deltaBeforeSeeking = 2; // league of legends spectator begin delay
	// var offset = offsetMillis/1000 - offsetBeforeSeeking;
	var first_timeseek = true;
	var audiosync = new OverwolfPlugin("audiosync", true);
	var playingAudio = false; // playing audio

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
			console.log("INFO FIRED", info);
			if (info.feature == "summoner_info") {
				console.log(info);
			}

			if (info.feature == "matchState") { // a matchState event is triggered
				if (info.info.game_info.matchStarted == "True") {
					var currentTimestamp = new Date().getTime();
				}
				else if (info.info.game_info.matchOutcome != null) { // match over
					// check is playing a match and match had outcome
					if (playingMatch) {
						$.ajax({
							url: 'http://teamchat.lol:3501/endgame/',
							dataType: 'json',
							type: 'get',
							cache: false,
							data: ({key: key, state: "end"}),
							success: function(data) {
								window.close(); // wait until server response before exiting app
							}
						});
					}
					else {
						// finished a game, can stop app now
						window.close();
					}
				}
			}
		});
		// an event is triggered
		overwolf.games.events.onNewEvents.addListener(function(info) {
			console.log("EVENT FIRED", info);
			if (info.events["0"].name == "matchStart") {
				// match is started
				console.log("MATCH STARTED");
				if (audioReady) {
					playingAudio = true;
					overwolf.windows.sendMessage("audio", "play", {}, function() {}); // play sent
				}
				else {
					console.log("audio isn't ready yet");
					// DO SOMETHING IF AUDIO CANT PLAY -- play when ready?? TODO
				}
			}
		});

		console.log("finished registering events");

		// set features after registering events
		setFeatures(1);
	}

	function startMatchUI() {
		// idk do some stuff here TODO
		// maybe show that youre being recorded or show tools for talking to discord bot
		// buttons that play forecast janna quotes or whatever else in the discord !!
	}

	function startReplayUI(src, offset, timeline) {
		overwolf.windows.obtainDeclaredWindow("audio", function() {
			overwolf.windows.restore("audio", function() {
				overwolf.windows.getOpenWindows(function() {
					overwolf.windows.onMessageReceived.addListener(function(cb) {
						if (cb.id == "ready") {
							audioReady = true;
							setAudioFile(src, offset, timeline);
						}
					})
				})
			});
		})
	}

	function setAudioFile(src, offset, timeline) {
		overwolf.windows.sendMessage("audio", "info", {src: src, offset: offset, timeline: timeline}, function(cb) {
			console.log("howler initiated", cb);
			audiosync.get().onTimeSeek.addListener(function(newtime) {
				console.log("timeseek", newtime);
				// only do this on the first timeseek
				if (first_timeseek == true) {
					first_timeseek = false;
					// change the offset now by 1
					//offset -= 1;
				}
				// change the time in the audio accordingly
				overwolf.windows.sendMessage("audio", "timeseek", {newtime: newtime, offset: offset}, function() {
					console.log("timeseek sent", newtime);
				})
			})
		})
	}

	function getServer() {
		audiosync.get().getLeagueArgs(args => {
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
							startReplayUI(data.src, data.offset-deltaBeforeSeeking, data.timeline);
						}
						else {
							// indicate that the hash failed for the current match TODO
							console.log("failed to authenticate on server");
						}
					},
					error: function() {
						// do something if error
					}
				});
			}
		});
	}

	audiosync.initialize(function(status) {
		if (status == false) {
			console.log("plugin failed to load");
			return;
		}
		// alow global access to the plugin for testing TODO
		audioplugin = audiosync;
		console.log("audiosync plugin is initialized");

		// run game start logic;
    audiosync.get().onTimeSeek.addListener(function(newtime) {
      console.log("timeseek: ", newtime);
    });
    //getServer();
	});

	// called whenever the game window is changed
	overwolf.games.onGameInfoUpdated.addListener(function(gameInfoChangeData) {
		console.log("GameInfoUpdated", gameInfoChangeData);

		// handle window size changes here -- tell overlay where to go
		// TODO

		if (gameInfoChangeData.gameChanged == true) {
			getServer();
		}

		// a game info was updated but the gameInfo is null
		if (gameInfoChangeData.gameInfo == null) { // game is closed but not match outcome
			console.log("game closed");
			if (playingMatch) {
				console.log("playing a match -> game ended");
				$.ajax({
					url: 'http://teamchat.lol:3501/endgame/',
					dataType: 'json',
					type: 'get',
					cache: false,
					data: ({key: key, state: "terminate"}),
					success: function(data) {
						// check if the game still exists in the server before purging TODO
						window.close(); // wait until server response before exiting app
					}
				});
			}
			else {
				window.close();
			}
		}
	});
});
