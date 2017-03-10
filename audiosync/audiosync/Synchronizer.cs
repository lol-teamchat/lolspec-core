using System;

namespace audiosync {

    using System.Diagnostics;
    using System.Threading.Tasks;

    internal class Synchronizer {

        private double gameStartTime;
        private Listener listener;
        private Action<object> timeSeek;
        //private readonly Stopwatch watch;
        private double lastTimeInGame;

        public Synchronizer(Listener listener, Action<object> triggerTimeSeek) {
            timeSeek = triggerTimeSeek;
            this.listener = listener;
            gameStartTime = -1.0;
            //watch = new Stopwatch();

            // call triggerTimeSeek event when timeseeks occur
            listenToTimeseeks();
        }

        // starts a stopwatch for this time and compares it with the game time to keep it in sync 
        // TODO
        internal void setStartTime(double time) {
            gameStartTime = time;

            // run stopwatch
            //watch.Start();

            // use listener and EventParser to get the in-game time of the UX and compare 
            // it with the stopwatch to see if there are any time seeks
            // TODO
            listener.add(message => {

                double receivedTime = EventParser.extractSetCurrentReplayTime((string)message);
                lastTimeInGame = receivedTime;

                if (EventParser.extractReplayFastForwardCaughtUp((string)message) == false) {
                    return;
                }
                //double receivedTime = EventParser.extractSetCurrentReplayTime((string)message);
                
                if (receivedTime != -1.0) {
                    Task.Run(() => {
                        timeSeek(receivedTime);
                    });                    
                }            
            });
        } 

        // report the time whenever a timeseek happens
        internal void listenToTimeseeks() {
            listener.add(message => {

                double receivedTime = EventParser.extractSetCurrentReplayTime((string)message);
                lastTimeInGame = receivedTime;

                if (EventParser.extractReplayFastForwardCaughtUp((string)message) == false) {
                    return;
                }
                //double receivedTime = EventParser.extractSetCurrentReplayTime((string)message);

                if (receivedTime != -1.0) {
                    Task.Run(() => {
                        timeSeek(receivedTime);
                    });
                }
            });
        }
        
        internal double getLastTime() {
            return lastTimeInGame;
        }  
    }
}