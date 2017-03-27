package com.ecreditpal.maas.web.endpoint;

import com.ecreditpal.maas.model.bean.Data;
import com.ecreditpal.maas.model.bean.Result;
import com.ecreditpal.maas.web.bean.User;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/10.
 */
@Slf4j
@Api(value = "d3", description = "Endpoint for rest test")
@Path("/d3")
public class D3TestEndpoint {
    @GET
    @Path("/mock")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns user details", notes = "Return a mock data by json", response = Data.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of mock data", response = Data.class)}
    )
    public Result<List<Data>> getData() {
        List<Data> list = Lists.newArrayList();
        Data data = new Data();
        data.setNumber(22);
        list.add(data);

        data = new Data();
        data.setNumber(23);
        list.add(data);

        data = new Data();
        data.setNumber(24);
        list.add(data);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>test");

        return Result.wrapSuccessfulResult(list);
    }
}
