package be.mainsys.poc.inf;

import javax.jws.WebService;

@WebService
public interface WSRemoteInterface {
	String echo(String input);
}
