package com.ecreditpal.maas.web.endpoint;

import com.ecreditpal.maas.common.IPBasedRateLimiter;
import com.ecreditpal.maas.model.bean.Data;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author lifeng
 * @CreateTime 2017/5/7.
 */
@Slf4j
@Api(value = "pmml", description = "Endpoint for pmml generate service")
@Path("/pmml")
public class PmmlEndpoint {

    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public Data getUserText() {
        Data data = new Data();
        data.setNumber(22);

//        data = new Data();
//        data.setNumber(23);
//        list.add(data);
//
//        data = new Data();
//        data.setNumber(24);
//        list.add(data);
        return data;
    }
}
