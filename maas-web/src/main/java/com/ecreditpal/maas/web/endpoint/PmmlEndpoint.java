package com.ecreditpal.maas.web.endpoint;

import com.ecreditpal.maas.common.IPBasedRateLimiter;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.common.utils.json.JsonUtil;
import com.ecreditpal.maas.model.bean.Data;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.processor.ExportModelProcessor;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.dmg.pmml.PMML;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @ApiOperation(value = "pmml generator")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "XYB Model Response", response = PMML.class)})
    @Produces(MediaType.APPLICATION_XML)
    public PMML generatePmml(@ApiParam(name = "column_config", value = "column_config", required = true)
                             @FormParam("column_config") String config,
                             @ApiParam(name = "params", value = "model params", required = true)
                             @FormParam("params") String params) {
        ExportModelProcessor p = new ExportModelProcessor(null, true, config,
                ConfigurationManager.getConfiguration().getString("ModelConfig.json"), params);
        try {
            p.run();
            return p.getPmml();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
