package be.mainsys.poc.ejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import be.mainsys.poc.inf.WSRemoteInterface;

@Stateless
@Remote(WSRemoteInterface.class)
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class WSTestEJB implements WSRemoteInterface {

	@WebMethod
	public String echo(String input) {
		return "EJB Wesbservice returning: " + input;
	}

}
