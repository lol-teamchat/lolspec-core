using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Management;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    static class LeagueInjector {
        //InjectionManager.Inject(int processId, string dllName);

        public static bool Inject(string dllName) {
            var processes = Process.GetProcessesByName("League of Legends");                

            // check to make sure there is only one instance of League of Legends.exe
            if (1 != processes.Length) {
                return false;
            }

            Process leagueProcess = processes.First();
            
            // do more checking to make sure League of Legends is running as spectate mode TODO
            try {
                string commandLine = leagueProcess.GetCommandLine();
                // only run if command line has .rofl or spectate
                if (commandLine.Contains(".rofl")) {
                    Console.WriteLine("has .rofl in command line");
                    // watching a completed replay
                }
                else if (commandLine.Contains("spectator")) {
                    // spectator 
                    if (commandLine.Contains("lol.riotgames.com")) {
                        // spectating on the live server grid -> return false
                        return false;
                    }
                }
                else {
                    // no .rofl or spectator arguments
                    return false;
                }
            }
            catch (Win32Exception ex) when ((uint)ex.ErrorCode == 0x80004005)  {
                return false;
            }

            var processId = processes[0].Id;
            System.Console.WriteLine(processes[0].ToString() + ", id:" + processId);

            // Do actual injection TODO
            


            //return InjectionManager.Injector.Inject(processId, dllName);        
            return true;
        }    
        private static string GetCommandLine(this Process process) {
            var commandLine = new StringBuilder(process.MainModule.FileName);

            commandLine.Append(" ");
            using (var searcher = new ManagementObjectSearcher("SELECT CommandLine FROM Win32_Process WHERE ProcessId = " + process.Id)) {
                foreach (var @object in searcher.Get()) {
                    commandLine.Append(@object["CommandLine"]);
                    commandLine.Append(" ");
                }
            }

            return commandLine.ToString();
        }
    }
}
