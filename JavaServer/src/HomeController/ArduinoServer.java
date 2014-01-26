package HomeController;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArduinoServer implements ArduinoDataEventListener
{

	private State state;
	private ArduinoController ac;

	protected Boolean action;

	public ArduinoServer(final ArduinoController ac, int serverPortNumber) throws IOException
	{
		this.ac = ac;

		InetSocketAddress addr = new InetSocketAddress(serverPortNumber);
		HttpServer server = HttpServer.create(addr, 0);

		server.createContext("/r/", new HttpHandler()
		{

			@Override
			public void handle(HttpExchange he)
			{
				try {
					URI uri = he.getRequestURI();

					action = "1".equals(uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1));
					ac.write("r:" + (action ? "1" : "0"));

					String response = String.format("{\"temperature\":%s, \"light\":%d, \"relayA\":%d, \"relayB\":%d}", new Float(state.getTemperature()).toString().replace(',', '.'), state.getLight(), action ? 1 : 0, state.isRelayB() ? 1 : 0);

					Headers responseHeaders = he.getResponseHeaders();
					responseHeaders.set("Content-Type", "application/json");
					responseHeaders.set("Access-Control-Allow-Origin", "*");
					he.sendResponseHeaders(200, response.length());
					OutputStream os = he.getResponseBody();
					os.write(response.getBytes());
					os.close();
				} catch (IOException ex) {
					Logger.getLogger(ArduinoServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		server.createContext("/s/", new HttpHandler()
		{

			@Override
			public void handle(HttpExchange he)
			{
				try {
					URI uri = he.getRequestURI();

					action = "1".equals(uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1));
					ac.write("s:" + (action ? "1" : "0"));

					String response = String.format("{\"temperature\":%s, \"light\":%d, \"relayA\":%d, \"relayB\":%d}", new Float(state.getTemperature()).toString().replace(',', '.'), state.getLight(), state.isRelayA() ? 1 : 0, action ? 1 : 0);

					Headers responseHeaders = he.getResponseHeaders();
					responseHeaders.set("Content-Type", "application/json");
					responseHeaders.set("Access-Control-Allow-Origin", "*");
					he.sendResponseHeaders(200, response.length());
					OutputStream os = he.getResponseBody();
					os.write(response.getBytes());
					os.close();
				} catch (IOException ex) {
					Logger.getLogger(ArduinoServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		server.createContext("/", new HttpHandler()
		{

			@Override
			public void handle(HttpExchange he)
			{
				try {
					String response = String.format("{\"temperature\":%s, \"light\":%d, \"relayA\":%d, \"relayB\":%d}", new Float(state.getTemperature()).toString().replace(',', '.'), state.getLight(), state.isRelayA() ? 1 : 0, state.isRelayB() ? 1 : 0);

					Headers responseHeaders = he.getResponseHeaders();
					responseHeaders.set("Content-Type", "application/json");
					responseHeaders.set("Access-Control-Allow-Origin", "*");
					he.sendResponseHeaders(200, response.length());

					OutputStream os = he.getResponseBody();
					os.write(response.getBytes());
					os.close();
				} catch (IOException ex) {
					Logger.getLogger(ArduinoServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		//server.setExecutor(Executors.newCachedThreadPool());
		server.setExecutor(null);
		server.start();
		System.out.println("Server is listening on port " + serverPortNumber);
	}

	@Override
	public void dataReady(State data)
	{
		this.state = data;
	}

}
