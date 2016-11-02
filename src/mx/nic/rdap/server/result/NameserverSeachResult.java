package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.renderer.json.NameserverParser;

/**
 * A result from a Nameserver search request
 * 
 * @author dalpuche
 *
 */
public class NameserverSeachResult implements RdapResult {

	private List<NameserverDAO> nameservers;

	public NameserverSeachResult(List<NameserverDAO> list) {
		nameservers = new ArrayList<NameserverDAO>();
		nameservers.addAll(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (NameserverDAO nameserver : nameservers) {
			NameserverParser parser=new NameserverParser(nameserver);
			arrayBuilder.add(parser.getJson());
		}
		builder.add("nameserverSearchResults", arrayBuilder.build());
		return builder.build();
	}

}
