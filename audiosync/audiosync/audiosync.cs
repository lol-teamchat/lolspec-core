using System;
using System.Collections.Generic;
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

        // constructor
        public Audiosync() {
            //MessageBox.Show("this is called", null, MessageBoxButtons.OK);
            dllPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}\\{dll}";
            if (LeagueInjector.Inject(dllPath) == false) {
                Logger.setError("failed to inject " + dll);
            }
            // listens for udp data
            listener = new Listener(listen_port);
            // triggers events when listened data becomes out of sync
            sync = new Synchronizer(listener, triggerTimeSeek);
        }

        // getUIMessage(callback(success, message));
        //public void getUIMessage(Action<object, object> callback) {     
        //    // check for invalid callback      
        //    if (callback == null) {
        //        return;
        //    }
        //    if (listener == null) {
        //        callback("error", Logger.getError());
        //    }
        //    else {
        //        string msg = listener.GetMessage();
        //        if (msg == "error") {
        //            callback("error", Logger.getError());
        //            return;
        //        }
        //        callback("success", msg);
        //    }                   
        //}

        // getGameTime(callback(success, message));
        //public void getGameTime(Action<object, object> callback) {
        //    // check for invalid callback      
        //    if (callback == null) {
        //        return;
        //    }
        //    if (listener == null) {
        //        callback("error", Logger.getError());
        //    }
        //    else {
        //        string msg = listener.GetMessage();
        //        if (msg == "error") {
        //            callback("error", Logger.getError());
        //            return;
        //        }
        //        callback("success", msg);
        //    }
        //    // get the current time of the game
        //    // TODO 
        //    // ----------------------->                      
        //}

        public void setMatchStartTime(double currentTimeStamp, Action<object> callback) {
            // tell the synchronizer what time should be synced
            //triggerTimeSeek("1");
            //triggerTimeSeek("2");
            //triggerTimeSeek("3");

            //System.Media.SoundPlayer player = new System.Media.SoundPlayer(@"c:\users\Adam\Desktop\11_-_Bird_of_Paradise.wav");
            //player.Play();

            sync.setStartTime(currentTimeStamp);
            callback("game start time at " + currentTimeStamp);

            //MessageBox.Show("currentTimeStamp= " + currentTimeStamp, null, MessageBoxButtons.OK);
        }

        // updates callback whenever a timeSeek action occurs
        // audiosync.get().onTimeSeek.addListener(function(time) { 
        public event Action<object> onTimeSeek;

        private void triggerTimeSeek(object o) {
            onTimeSeek(o);
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