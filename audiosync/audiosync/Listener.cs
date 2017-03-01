using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    using System.Net;
    using System.Net.Sockets;
    using System.Text;
    using System.Threading;

    internal class Listener {
        private readonly UdpClient _client;
        private IPEndPoint _ipep;
        private string lastMessage;

        public Listener(int port) {

            try {
                this._client = new UdpClient(port);
                this._ipep = new IPEndPoint(IPAddress.Any, port);
                lastMessage = "";
                startListening();

            }
            catch (Exception e) {
                Logger.setError(e.Message);
            }                  
        }

        private void startListening() {
            new Thread(() => {
                while (true) {

                    string msg = getMessage();
                    // a different message is received
                    if (msg != null && lastMessage != msg) {
                        lastMessage = msg;

                        // handle event updates and triggers TODO

                    }

                    // sleep for 1 ms
                    Thread.Sleep(1);
                }
            }).Start();            
        }

        private string getMessage() {
            try {
                return Encoding.UTF8.GetString(this._client.Receive(ref this._ipep));
            }
            catch (Exception e) {
                Logger.setError(e.Message);
                return null;
            }
        }
    }
}
