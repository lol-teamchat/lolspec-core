function openSettings() {
	overwolf.windows.obtainDeclaredWindow("settings", function(cb) {

		overwolf.windows.restore("settings", function(cb) {
			console.log("settings opened", cb);

		});
	})
}
