using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    static class Logger {
        private static string last_error_message = "";

        public static void setError(string msg) {
            last_error_message = msg;
            // TODO log msg to a file or send it somewhere idk
            // --->
        }

        public static string getError() {
            return last_error_message;
        }
    }
}
