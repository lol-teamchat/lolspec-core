using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    internal class EventParser {

        public static float extractReplayTime(string message) {

            string[] funcs = message.Split(',');  

            for (int i = 0; i < funcs.Length; i++) {
                if (funcs[i].Equals("SetCurrentReplayTime")) {
                    // get the next element
                    return float.Parse(funcs[i + 1]);                    
                }
            }

            // no time found in this message (-1F)
            return -1F;                      
        } 
    }
}
