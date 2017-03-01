using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace audiosync
{
    public class audiosync
    {
        // constructor
        public audiosync() {
            
            
        }

        public void getTime(Action<object> callback) {
            if (callback == null) {
                return;
            }

            // get the current time of the game

            return;
        }

        static int Main(string[] args) {

            var dll = "LeagueReplayHook.dll";
            //Console.WriteLine(dll);
            var dllPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}\\{dll}";
            if (LeagueInjector.Inject(dll) == true) {
                Console.WriteLine(dll + " injected successfully");
            }
            else {
                Console.WriteLine(dll + " failed to inject.");
                return 0;
            }

            int udp_port = 7000;
            var listener = new Listener(udp_port);

            while (true) {
                Console.WriteLine(listener.GetMessage());
            }
            //Console.ReadKey();
            //return 0;
        }
    }    
}