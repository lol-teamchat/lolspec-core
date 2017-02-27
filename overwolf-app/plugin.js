console.log('plugin.js loaded...');

// create an instance of the plugin
var plugin = new OverwolfPlugin("InGameProbability", true);

console.log('plugin created');

// initialize it
plugin.initialize(function(status) {
    console.log('plugin initialize returned ' + status);
    if (status == false) {
        document.querySelector('#title').innerText = "Plugin couldn't be loaded??";
        return;
    }

    plugin.get().Log.addListener(function(message) {
        console.log(message);
    });
});
/*
console.log('plugin initialize called...');

function registerEvents()   {
    console.log('registering for events...');
    overwolf.games.onGameInfoUpdated.addListener(function(info) {
        console.log("got game info updated event! " + info);
        if(info && info.gameInfo)   {
            console.log('got gameinfo event!! ' + JSON.stringify(info.gameInfo));

            // check if game is closed...
            if(!info.gameInfo.isRunning)    {
                console.log("game is not running... shutting stuff down...");
                // called when game closes
                // close window
                overwolf.windows.getCurrentWindow(function(result)  {
                    overwolf.windows.close(result.window.id);
                });

                // reset plugin
                console.log('calling reset on plugin...');
                plugin.get().Reset(function(result) {
                    console.log("reset plugin... result " + result);
                });
            }

            // set window width to resolution
            if(info.gameInfo.width) {
                overwolf.windows.obtainDeclaredWindow("index", function(result) {
                    if(result && result.window && result.window.id) {
                        overwolf.windows.changePosition(result.window.id, 0, 16, function()   {
                            overwolf.windows.changeSize(result.window.id, info.gameInfo.width, 420, null);
                        });
                    }   else    {
                        console.log("could not get window with name index... " + JSON.stringify(result));
                    }
                })
            }
        }
    });

    overwolf.games.events.onError.addListener(function(info)    {
        console.log("Error: " + JSON.stringify(info));
    });

    overwolf.games.events.onInfoUpdates2.addListener(function(info)    {
        console.log("info: " + JSON.stringify(info));
        if(info && info.info.game_info && info.info.game_info.teams)  {
            var teams = JSON.parse(decodeURIComponent(info.info.game_info.teams));

            initializePredictions(teams);
        }   else    {
            console.log("something was missing, couldnt get teams");
        }
    });
}

function setFeatures(callback)  {
    overwolf.games.events.setRequiredFeatures(['teams'], function(info) {
        if(info.status == 'error')  {
            console.log('could not set required features: ' + info.reason);
            window.setTimeout(function() {setFeatures(callback)}, 1000);
        }   else    {
            console.log('set required features!');
            console.log(JSON.stringify(info));
            if(callback)    {
                callback(true);
            }
        }
    })
}

function initializePredictions(teams)  {
    var blue = teams.filter(function (player) {
        return player.team == 100;
    });

    var red = teams.filter(function (player) {
        return player.team == 200;
    });

    var extractSummoner = function (player) {
        return player.summoner;
    };

	var championNames = teams.map(function (player) {
		return player.champion;
	});

    var blueNames = blue.map(extractSummoner);
    var redNames = red.map(extractSummoner);

    console.log('blue players: ' + JSON.stringify(blueNames));
    console.log('red players: ' + JSON.stringify(redNames));

    plugin.get().InitializeState(championNames, blueNames, redNames, function (success) {
        if (success) {
            console.log('successfully initialized state');
            plugin.get().StartApp(function (success) {
                if (success === true) {
                    console.log('successfully started app');
                    plugin.get().WinChanceChanged.addListener(function(blueChance, redChange)   {
                        document.querySelector('#blue_chance').innerText = Math.round(blueChance * 1000) / 10 + '%';
                        document.querySelector('#red_chance').innerText = Math.round(redChange * 1000) / 10 + '%';
                        document.querySelector('#root').style.visibility = 'visible';
                    });
                } else {
                    console.log('failed to start app!');
                }
            });
        }
        else
        {
            console.log('failed to initialize state')
        }
    });
}

console.log("setting required features...");
setFeatures(function(success) {
    registerEvents();

    overwolf.games.events.getInfo(function(info)    {
        console.log('got game event info...');
        if(info && info.res && info.res.game_info && info.res.game_info.teams) {
            console.log('got teams... initializing predictions');

            var teams = JSON.parse(decodeURIComponent(info.res.game_info.teams));

            initializePredictions(teams);
        }
    });
});
*/
