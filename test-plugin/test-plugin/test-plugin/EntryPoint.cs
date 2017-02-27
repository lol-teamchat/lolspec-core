using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace test_plugin
{
    public class EntryPoint
    {
        const string str = "uh.. uh.. Shpit!";
        public EntryPoint() {

        }

        // this is an add function which adds two integers together
        public void add(int a, int b, Action<object> callback) {
            if (callback == null) {
                return;
            }

            int c = a + b;
            Task.Run(() => {
                try {
                    callback(c);
                }
                catch (Exception) {
                    callback("error");
                }
            });

            return;
        }

        public void getShpit(Action<object> callback) {
            if (callback == null) {
                return;
            }

            Task.Run(() => {
                try {
                    callback(str);
                }
                catch (Exception) {
                    callback("error");
                }
            });

            return;
        }
    }
}
