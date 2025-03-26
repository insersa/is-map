/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package ch.inser.isejawa.map.services;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.isejawa.map.map.MapServiceHandler;
import ch.inser.isejawa.map.util.ServiceUtil;
import ch.inser.rest.auth.ISSecurityException;
import ch.inser.rest.util.RestUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Resource for collecting configurations for an esri map
 *
 * @author INSER SA
 *
 */
@Path("/map")
@Api(value = "map")
public class MapResource {

    /**
     * The logger.
     */
    private static final Log logger = LogFactory.getLog(MapResource.class);

    /**
     * The context.
     */
    @Context
    private ServletContext iContext;

    /**
     * Get the map configuration.
     *
     * @param aToken
     *            the security token
     *
     * @return the map configuration
     */
    @ApiOperation(value = "Get configurationthe")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Error input parameters"),
            @ApiResponse(code = 401, message = "User not authorized"), @ApiResponse(code = 500, message = "Unexpected error") })
    @GET()
    @Path("configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response configuration(@HeaderParam("token")
    @ApiParam(value = "The security token", required = true)
    String aToken) {
        try {
            IContextManager contextManager = RestUtil.getContextManager();

            // Check the input values
            if (aToken == null) {
                logger.warn("Error input parameters");
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Check the security
            RestUtil.getClaims(aToken);

            JsonObject configuration;
            try (JsonReader jsonReader = Json.createReader(
                    new InputStreamReader(new FileInputStream(contextManager.getProperty("map.config.file")), StandardCharsets.UTF_8))) {
                configuration = jsonReader.readObject();
            }

            // Check if the configuration contain a not client login
            Response error = checkConfiguration(configuration);
            if (error != null) {
                RestUtil.cleanNdc();
                return error;
            }

            RestUtil.cleanNdc();
            return Response.ok(configuration.toString()).build();
        } catch (ISSecurityException e) {
            logger.warn("User not authorized", e);
            RestUtil.cleanNdc();
            return Response.status(Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            RestUtil.cleanNdc();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check that the configuration is correct before returning it, for example that it contains a client login
     *
     * To be implemented in the project
     *
     * @param aConfiguration
     *            the map configuration
     *
     * @return error response or null
     */
    protected Response checkConfiguration(JsonObject aConfiguration) {
        return null;
    }

    /**
     * Get the map token.
     *
     * @param aToken
     *            the security token
     * @param aReferer
     *            the referer URL
     * @param aService
     *            Name of service if not the default one
     * @param aRequestIP
     *            <code>true</code> to use the <code>requestip</code> client identification instead of <code>referer</code>, more
     *            information on https://developers.arcgis.com/rest/services-reference/generate-token.htm
     *
     * @return the map token
     */
    @ApiOperation(value = "Get the map token")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Error input parameters"),
            @ApiResponse(code = 401, message = "User not authorized"), @ApiResponse(code = 500, message = "Unexpected error") })
    @GET()
    @Path("token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response token(@HeaderParam("token")
    @ApiParam(value = "The security token", required = true)
    String aToken, @HeaderParam("Referer")
    @ApiParam(value = "The referer URL", required = true)
    String aReferer, @QueryParam("service")
    @ApiParam(value = "Name of service if not the default one", required = false)
    String aService, @QueryParam("requestip")
    @ApiParam(value = "true to use the requestip client identification instead of referer", required = false)
    boolean aRequestIP) {
        try {
            IContextManager contextManager = RestUtil.getContextManager();

            // Check the input values
            if (aToken == null) {
                logger.warn("Error input parameters");
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Check the security
            RestUtil.getClaims(aToken);

            // Get the token
            JsonObject token = null;
            if (aService == null) {
                token = ServiceUtil.getToken(contextManager, aReferer, aRequestIP);
            } else {
                token = ServiceUtil.getToken(contextManager, aReferer, aService, aRequestIP);
            }

            // Return the token
            RestUtil.cleanNdc();
            return Response.ok(token.toString()).build();
        } catch (ISSecurityException e) {
            logger.warn("User not authorized", e);
            return Response.status(Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get the map domains
     *
     * @param aToken
     *            the security token
     * @param aReferer
     *            the referer URL
     * @param aRequestIP
     *            <code>true</code> to use the <code>requestip</code> client identification instead of <code>referer</code>, more
     *            information on https://developers.arcgis.com/rest/services-reference/generate-token.htm
     * @param aMapUrl
     *            The url of the map service
     * @param aLayers
     *            The layer in an array or * char for all layers
     *
     * @return the map configuration
     */
    @ApiOperation(value = "Get the domains")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Error input parameters"),
            @ApiResponse(code = 401, message = "User not authorized"), @ApiResponse(code = 500, message = "Unexpected error") })
    @GET()
    @Path("domains")
    @Produces(MediaType.APPLICATION_JSON)
    public Response domains(@HeaderParam("token")
    @ApiParam(value = "The security token", required = true)
    String aToken, @HeaderParam("Referer")
    @ApiParam(value = "The referer URL", required = true)
    String aReferer, @QueryParam("requestip")
    @ApiParam(value = "true to use the requestip client identification instead of referer", required = false)
    boolean aRequestIP, @QueryParam("url")
    @ApiParam(value = "The referer URL", required = true)
    String aMapUrl, @QueryParam("layers")
    @ApiParam(value = "The referer URL", required = true)
    String aLayers) {
        try {
            logger.debug(String.format("MapRessource.domains: url='%s' layers='%s'", aMapUrl, aLayers));

            IContextManager contextManager = RestUtil.getContextManager();

            // Check the input values
            if (aToken == null) {
                logger.warn("Error input parameters");
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Check the security
            RestUtil.getClaims(aToken);

            String token = ServiceUtil.getToken(contextManager, aReferer, aRequestIP).getString("token");

            JsonArray domains = MapServiceHandler.getMapDomains(aMapUrl, aLayers, token).getJsonArray("domains");

            RestUtil.cleanNdc();
            return Response.ok(domains.toString()).build();
        } catch (ISSecurityException e) {
            logger.warn("User not authorized", e);
            RestUtil.cleanNdc();
            return Response.status(Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            RestUtil.cleanNdc();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
