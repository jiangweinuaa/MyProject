package com.dsc.spos.restfulservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/testService")
public class TestRestfulService {
	
	@POST
	@Path("/testInvoke")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String invoke(@FormParam("xml") String xml) {
		return "XXXXX";
	}

}
