using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
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
                //return;
            }

            // listens for udp data
            listener = new Listener(listen_port);
            // triggers events when listened data becomes out of sync
            sync = new Synchronizer(listener);
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
            sync.setStartTime(currentTimeStamp);
            callback("set start time to: " + currentTimeStamp);

            //MessageBox.Show("currentTimeStamp= " + currentTimeStamp, null, MessageBoxButtons.OK);
        }


        // updates callback whenever a timeSeek action occurs 
        public void timeSeek(Action<object> callback) { // TODO         
            if (callback == null) {
                return;
            }
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