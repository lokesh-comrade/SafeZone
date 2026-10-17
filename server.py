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
        # Friendly route redirection
        if self.path in ('/app', '/mobile'):
            self.send_response(302)
            self.send_header('Location', '/app/app.html')
            self.end_headers()
            return
        elif self.path in ('/monitor', '/console', '/police'):
            self.send_response(302)
            self.send_header('Location', '/monitor/monitor.html')
            self.end_headers()
            return
        elif self.path in ('/analytics', '/data', '/sdg'):
            self.send_response(302)
            self.send_header('Location', '/analytics/analytics.html')
            self.end_headers()
            return
        return super().do_GET()

def run_server():
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), SafeZoneHandler) as httpd:
        print(f"==========================================================================")
        print(f"  PRJ_650: Real-Time Women Safety Monitoring & Response Platform")
        print(f"  Mapped to UN SDG 5 (Gender Equality - Target 5.2)")
        print(f"==========================================================================")
        print(f"  [1] Project Portal (Main Hub):     http://localhost:{PORT}/index.html")
        print(f"  [2] Victim Mobile Web Client:      http://localhost:{PORT}/app/app.html")
        print(f"  [3] Police Tactical Command Center: http://localhost:{PORT}/monitor/monitor.html")
        print(f"  [4] Big Data & SDG 5 Analytics:    http://localhost:{PORT}/analytics/analytics.html")
        print(f"  [5] Native Android APK:            {os.path.join(BASE_DIR, 'app-debug.apk')}")
        print(f"==========================================================================")
        print(f"  Server is ACTIVE. Press Ctrl+C to terminate.")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down server cleanly.")

if __name__ == '__main__':
    run_server()
