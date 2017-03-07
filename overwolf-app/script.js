var audioplugin;
var auth;

$(document).ready(function() {
	// variables
	var features = [
		'summoner_info',
		'gameMode',
		'teams', // only works in spectator mode
		'matchState'
	];
	var playingMatch = false;
	var replayBegan = 1488489461479;
	var audioBegan = 12912983712;
	var offsetMillis = replayBegan-audioBegan;
	var offsetBeforeSeeking = 2; // league of legends spectator begin delay
	var offset = offsetMillis/1000 - offsetBeforeSeeking;
	var first_timeseek = true;
	var audiosync = new OverwolfPlugin("audiosync", true);

	function getAuth() {
		auth = localStorage.getItem("authtoken");
	}

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
			console.log("Set required features", features);
		});
	}

	function registerEvents() {
		// general events errors
		overwolf.games.events.onError.addListener(function(info) {
			console.log("Error: ", info);
		});

		overwolf.games.events.onInfoUpdates2.addListener(function(info) {
			console.log(info);
			if (info.feature == "matchState") { // a matchState event is triggered
				if (info.info.game_info.matchStarted == "True") {
					var currentTimestamp = new Date().getTime();

					// check if plugin is initialized
					if (audiosync.initialized() == true) {
						// initialize the beginning of the (watching) match with the plugin
						if (playingMatch == true) {
							console.log("playingMatch is true");
						}
						else { // watching a match?
							audiosync.get().setMatchStartTime(currentTimestamp, function(callback) {
								 // maybe calculate the new offset here?
								// tell window to play audio
								overwolf.windows.getOpenWindows( function(cb) {
									if (cb.audio != null) {
										overwolf.windows.sendMessage("audio", "play", offset, function(cb) {
											console.log("sent play message")
											//$('.audioDemo')[0].currentTime = offset + newtime;
										})
									}
								})
							})
						}
					}
				}
			}

			if (info.feature == "teams") {
				console.log("teams event time: ", new Date().getTime());

				if (auth == null) {
					getAuth();
				}

				// do some other stuff here TODO
				var info = info.info.game_info.teams;
				var decoded = JSON.parse(decodeURI(info));

				var red_team = decoded.splice(5)
				var blue_team = decoded;

				$.ajax({
					url: 'http://teamchat.lol:3500/teams/',
					dataType: 'json',
					type: 'get',
					cache: false,
					data: ({key: authtoken}),
					success: function(data) {

						$("#teamsTarget").removeClass('loading')
						if (handleAuth(data)){
							$("#teamsTarget").html(Mustache.render($("#teams-template").html(), data))
						}


					}
				});

				console.log("blue_team = ", blue_team);
				console.log("red_team = ", red_team);
			}
		});

		// an event is triggered
		overwolf.games.events.onNewEvents.addListener(function(info) {
			console.log("EVENT FIRED: ", info);
		});

		console.log("finished registering events");
	}

	audiosync.initialize(function(status) {
		if (status == false) {
			return;
		}

		// alow global access to the plugin for testing
		audioplugin = audiosync;

		console.log("audiosync plugin is initialized");

		var args;
		audiosync.get().getLeagueArgs(cb => {
			args = cb;
			audiosync.get().isPlayingReplay(playing => {
				if (playing == true) {
					playingMatch = true;
					// have the plugin determine the command line arguments of league of legends
					// then obtain the url of the corresponding game's mp3 file
					// audiosync.get().gameURL(function(cb) {
					// })
					var matchId = 2438328569;
					//var mp3_src1 = "http://xddddd.ddns.net/lolspec/uKTtTpLHnDv5O.mp3";
					//var mp3_src2 = "http://xddddd.ddns.net/lolspec/pb3Q8I7Zu3Laq.mp3";
					var mp3_src = "http://xddddd.ddns.net/lolspec/6DcbUxnNrGRdN.mp3"
					// Clear listener after first call.

					overwolf.windows.obtainDeclaredWindow("audio", function(cb) {

						// open audio window
						overwolf.windows.restore("audio", function(cb) {
							console.log("audio widow created", cb);
							// configure howler in audio window
							overwolf.windows.getOpenWindows(output => {

								overwolf.windows.sendMessage("audio", "file_info", {src: mp3_src, offset: offset}, function(cb) {
									console.log("howler initiated", cb);
									//$('.audioDemo')[0].currentTime = offset + newtime;

									// user inputs a timeseek
									audiosync.get().onTimeSeek.addListener(function(newtime) {
										console.log("timeseek", newtime);

										// only do this on the first timeseek
										if (first_timeseek == true) {
											first_timeseek = false;

											// change the offset now by 1
											offset -= 1;
										}
										overwolf.windows.sendMessage("audio", "timeseek", {newtime: newtime, offset: offset}, function() {
											console.log("timeseek sent", newtime);
											//$('.audioDemo')[0].currentTime = offset + newtime;
										})
									})
								})
							})
						});
					})
				}
				else { //not playing a replay and the plugin is reaady
					// TODO maybe some non-replay features or something?
				}
			})
		})

		// notofications when the game starts and finishes
		// overwolf.games.events.onInfoUpdates2.addListener(function(infoUpdateChange)

		// called whenever the game window is changed
		overwolf.games.onGameInfoUpdated.addListener(function(gameInfoChangeData) {
			console.log("GameInfoUpdated");

			// handle window size changes here -- tell overlay where to go
			// TODO

			// a game info was updated but the gameInfo is null
			if (gameInfoChangeData.gameInfo == null) {
				closeWindow();
				console.log("game closed");
			}
		})

		registerEvents();
		setFeatures(1);
	});
});
