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

function takeScreenshot(){
	overwolf.media.takeScreenshot(function(result){
		if (result.status == "success"){
			var img = document.getElementById("screenshot");
			img.src = result.url;
			img.onload = function() {
				overwolf.media.shareImage(img, "Screen Shot");
			};
		}
	});
}
