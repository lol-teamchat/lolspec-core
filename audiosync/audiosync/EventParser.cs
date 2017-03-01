using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    internal class EventParser {

        public static double extractSetCurrentReplayTime(string message) {

            string[] funcs = message.Split(',');  

            for (int i = 0; i < funcs.Length-1; i++) {
                if (funcs[i].Equals("SetCurrentReplayTime")) {
                    // get the next element
                    return double.Parse(funcs[i + 1]);
                }
            }

            // no time found in this message (-1)
            return -1.0;                      
        } 

        public static bool extractReplayFastForwardCaughtUp(string message) {

            string[] funcs = message.Split(',');
            for (int i = 1; i < funcs.Length; i++) {
                if (funcs[i].Equals("ReplayFastForwardCaughtUp")) {
                    // get the previous element
                    return true;
                }
            }

            return false;
        }
    }
}
