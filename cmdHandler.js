var spawn = require('child_process').spawn;
const child = spawn('java', ['-jar', 'LeagueReplayComplete.jar']);
var proctab = ["start"];
//console.log('stdout: ' + stdout);
//console.log('stderr: ' + stderr);
child.stdout.on('data', (data) => {
  console.log(`stdout: ${data}`);

  //when stdout gets an "ended current process" message, execute first in queue
  if(`${data}`.match("Ended current process")){
    if (!proctab[0]){
      return;
    }
    //only happens when the bot boots up
    if(proctab[0] == "start"){
      proctab.shift();
	  //return; only if we dont want to continue
    }
    child.stdin.write(proctab[0],"utf-8",proctab.shift());
  }
});

child.stderr.on('data', (data) => {
  console.log(`stderr: ${data}`);
});

child.on('close', (code) => {
  console.log(`child process exited with code ${code}`);
});

//pushes the command to the queue
function signal(str){
  if(!proctab[0]){
    proctab.push(str);
    child.stdin.write(proctab[0],"utf-8",proctab.shift());
  }
  else{
    proctab.push(str);
  }
}

//Note: will not work if joining and saving the same guild less than 3 secs apart
  signal("!joinid 279394304894435329\n");
  setTimeout(function(){
  signal("!saveid 279394304894435329\n");
},7000);

  signal("!joinid 196297375046696960\n");
//child.stdin.end();
