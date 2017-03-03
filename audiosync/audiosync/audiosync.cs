using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Media;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace audiosync
{
    // plugin entrypoint
    public class Audiosync {
        const string dll = "LeagueReplayHook.dll";
        const int listen_port = 7000;
        string dllPath;
        Listener listener;
        Synchronizer sync;
        bool watchingReplay;

        // constructor
        public Audiosync() {
            //MessageBox.Show("this is called", null, MessageBoxButtons.OK);
            dllPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}\\{dll}";
            if (LeagueInjector.Inject(dllPath) == false) {
                Logger.setError("failed to inject " + dll);
                watchingReplay = false;
                return;
            }
            watchingReplay = true;       
            // listens for udp data
            listener = new Listener(listen_port);
            // triggers events when listened data becomes out of sync
            sync = new Synchronizer(listener, triggerTimeSeek);
        }

        // used to tell if the audiosync plugin injected properly
        // and the league of legends mode is a valid replay
        public void isPlayingReplay(Action<object> callback) {
            if (watchingReplay) {
                callback(true);
            }
            else {
                callback(false);
            }
        }

        // tells the plugin the current time in the game and resynchronizes
        public void setMatchStartTime(double currentTimeStamp, Action<object> callback) {
            if (sync == null) {
                callback("not watching a replay");
                return;
            }
            sync.setStartTime(currentTimeStamp);
            callback("setStartTime -- game start time at " + currentTimeStamp);
        }

        public void getLeagueArgs(Action<object> callback) {            
            callback(LeagueInjector.getLeagueArgs());
        }

        // updates callback whenever a timeSeek action occurs
        // audiosync.get().onTimeSeek.addListener(function(time) { 
        public event Action<object> onTimeSeek;

        private void triggerTimeSeek(object o) {
            onTimeSeek(o);
        }

        public void getLastTime(Action<object> callback) {
            if (sync == null) {
                callback("getLastTime -- not watching a replay");
                return;
            }
            callback(sync.getLastTime());
        }

        // whenever a non-timeseek time-related button is pressed on the client such as
        // p (pause), bkspc (rewind 15 seconds), kp_plus (increase playback), kp_minus (decrease playback), etc..
        public void keyPress(string key, Action<object> callback) { // TODO
            if (callback == null) {
                return;
            }

            // do something with the keypress TODO

            callback("done");
        }

    }
}