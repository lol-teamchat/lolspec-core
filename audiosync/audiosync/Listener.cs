using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    using System.Net;
    using System.Net.Sockets;
    using System.Text;

    internal class Listener {
        private readonly UdpClient _client;
        private IPEndPoint _ipep;
        private string lastMessage;
        private bool stop;
        private List<Action<object>> callbacks;

        public Listener(int port) {

            try {
                this._client = new UdpClient(port);
                this._ipep = new IPEndPoint(IPAddress.Any, port);
                lastMessage = "";
                stop = false;
                callbacks = new List<Action<object>>();
                startListening();

            }
            catch (Exception e) {
                Logger.setError(e.Message);
            }                  
        }

        private void startListening() {
            new Task(() => {
                while (!stop) {                    
                    string msg = getMessage();
                    //Console.Error.WriteLine(msg);
                    // a different message is received
                    if (msg != null && lastMessage != msg) {
                        lastMessage = msg;

                        // handle event updates and triggers TODO
                        foreach (Action<object> cb in callbacks) {
                            cb(lastMessage);
                        }
                    }                  
                }
            }).Start();            
        }

        private void stopListening() {
            stop = true;
        }

        public void add(Action<object> cb) {
            callbacks.Add(cb);            
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
