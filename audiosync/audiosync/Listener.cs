using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace audiosync {
    using System.Net;
    using System.Net.Sockets;
    using System.Text;

    public class Listener {
        private readonly UdpClient _client;
        private IPEndPoint _ipep;

        public Listener(int port) {
            this._client = new UdpClient(port);
            this._ipep = new IPEndPoint(IPAddress.Any, port);
        }

        /**
         * Returns a single UDP message from the socket listener
         */
        public string GetMessage() {
            return Encoding.UTF8.GetString(this._client.Receive(ref this._ipep));
        }
    }
}
