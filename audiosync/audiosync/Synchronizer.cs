using System;

namespace audiosync {
    internal class Synchronizer {

        private double gameStartTime;
        private Listener listener;

        public Synchronizer(Listener listener) {
            this.listener = listener;
            gameStartTime = -1.0;
        }

        internal void setStartTime(double time) {
            gameStartTime = time;
            // run a stopwatch thing,


            // use listener and EventParser to get the in-game time of the UX and compare 
            // it with the stopwatch to see if there are any time seeks

        }
    }
}