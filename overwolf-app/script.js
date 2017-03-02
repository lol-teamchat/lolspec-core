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


$(document).ready(function() {
  // Handler for .ready() called.
	// which features we are interested in receiving from the provider
	var features = [
		'matchState',
		'teams'
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
		});
	}

	//var replayBegan1 = 1488340509000;
	//var audioBegan1 = Date.parse("2017-02-28T19:53:38.306-08:00");
	//var replayBegan2 = 1488402675490;
	//var audioBegan2 = Date.parse("2017-03-01T13:09:29.948-08:00");
	var replayBegan = 1488489461479;
	var audioBegan = Date.parse("2017-03-02T13:17:02.591-08:00");
	var offsetMillis = replayBegan-audioBegan;
	//var mp3RecordingNullTime = 60;
	var mp3RecordingNullTime = 6;
	var offset = offsetMillis/1000 + mp3RecordingNullTime;

	var audiosync = new OverwolfPlugin("audiosync", true);
	audiosync.initialize(function(status) {
		if (status == false) {
			return;
		}
		console.log("audiosync plugin is initialized");

		// have the plugin determine the command line arguments of league of legends
		// then obtain the url of the corresponding game's mp3 file
		// audiosync.get().gameURL(function(cb) {
		// })
		var matchId = 2438328569;
		//var mp3_src1 = "http://xddddd.ddns.net/lolspec/uKTtTpLHnDv5O.mp3";
		//var mp3_src2 = "http://xddddd.ddns.net/lolspec/pb3Q8I7Zu3Laq.mp3";
		var mp3_src = "http://xddddd.ddns.net/lolspec/6DcbUxnNrGRdN.mp3"
		// Clear listener after first call.
		//sound.once('load', function(){
		//var id = sound.play();
			//console.log("loaded");
			//$('body').append('<audio class="audioDemo" controls oncanplay="console.log(\'ready to play\')" src="http://xddddd.ddns.net/lolspec/2uKTtTpLHnDv5O.mp3"></audio>');
			//$('.audioDemo')[0].load();
		overwolf.windows.obtainDeclaredWindow("audio", function(cb) {

			// open audio window
			overwolf.windows.restore("audio", function(cb) {
				console.log("audio widow created", cb);
				// configure howler in audio window
				overwolf.windows.getOpenWindows(output => {

					overwolf.windows.sendMessage("audio", "file_info", {src: mp3_src, offset: offset}, function(cb) {
						console.log("howler initiated", cb);
						//$('.audioDemo')[0].currentTime = offset + newtime;

						audiosync.get().onTimeSeek.addListener(function(newtime) {
							console.log("timeseek", newtime);

							overwolf.windows.sendMessage("audio", "timeseek", newtime, function() {
								console.log("timeseek sent", newtime);
								//$('.audioDemo')[0].currentTime = offset + newtime;
							})
						})

						// notofications when the game starts and finishes
						overwolf.games.events.onInfoUpdates2.addListener(function(infoUpdateChange) {
							//console.log("info update:", infoUpdateChange);
							if (infoUpdateChange.feature == "matchState") {

								if (infoUpdateChange.info.game_info.matchStarted == "True") {
									var currentTimestamp = new Date().getTime();
									if (audiosync.initialized() == true) {
										// initialize the beginning of the (watching) match with the plugin
										audiosync.get().setMatchStartTime(currentTimestamp, function(callback) {
											console.log(callback);

											// maybe calculate the new offset here?

											// tell window to play audio
											overwolf.windows.sendMessage("audio", "play", offset, function(cb) {
												console.log("sent play message")
												//$('.audioDemo')[0].currentTime = offset + newtime;
											})
										});
									}
								}
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

						setFeatures(1);

					})
				})
			});
	//});
		})

		// if the game is terminated
		overwolf.games.onGameInfoUpdated.addListener(function(gameInfoChangeData) {
			if (gameInfoChangeData.gameInfo == null) {
				closeWindow();
			}
		})
	});
});
