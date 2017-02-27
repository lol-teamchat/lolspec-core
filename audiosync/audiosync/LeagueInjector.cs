using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    internal class LeagueInjector {

        //InjectionManager.Inject(int processId, string dllName);

        public bool Inject(string dllName) {
            var processes = Process.GetProcessesByName("League of Legends");                


            if (processes.Length != 1) {
                return false;
            }

            var processId = processes[0].Id;
            System.Console.WriteLine(processes[0].ToString() + ", id:" + processId);

            // cpp injector
            //return InjectionManager.Injector.Inject(processId, dllName);
            return ClrInjectionLib.Injector.Inject32(processId, dllName);


        }        
    }
}
