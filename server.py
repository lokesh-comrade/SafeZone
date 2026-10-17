import http.server
import socketserver
import os
import sys

PORT = 8080
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

class SafeZoneHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=BASE_DIR, **kwargs)

    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        super().end_headers()

    def do_GET(self):
        # Convenient routing redirects
        if self.path in ('/', '/app', '/mobile'):
            self.send_response(302)
            self.send_header('Location', '/app/app.html')
            self.end_headers()
            return
        elif self.path in ('/monitor', '/console', '/police'):
            self.send_response(302)
            self.send_header('Location', '/monitor/monitor.html')
            self.end_headers()
            return
        return super().do_GET()

def run_server():
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), SafeZoneHandler) as httpd:
        print(f"===============================================================")
        print(f" SafeZone: Women Safety Monitoring & Response System Server")
        print(f"===============================================================")
        print(f" Mobile App (Jagruthi Paradigm): http://localhost:{PORT}/app/app.html")
        print(f" Desktop Tactical Console:       http://localhost:{PORT}/monitor/monitor.html")
        print(f" Native Android Project:         {os.path.join(BASE_DIR, 'android')}")
        print(f"===============================================================")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down server.")

if __name__ == '__main__':
    run_server()
