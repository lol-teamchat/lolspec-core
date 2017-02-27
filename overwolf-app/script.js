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
