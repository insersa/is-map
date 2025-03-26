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

package ch.inser.isejawa.map.util;

import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.jsl.exceptions.ISException;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Utilities to work on ArcGIS servers (ArcGIS Online, ArcGIS Portal, ArcGIS Server, ...).
 *
 * @author INSER SA
 */
public class ServiceUtil {

    /**
     * The logger.
     */
    private static final Log logger = LogFactory.getLog(ServiceUtil.class);

    /**
     * Private constructor to hide the public one. All methods are static.
     */
    private ServiceUtil() {
    }

    /**
     * Get a token for an ArcGIS server.
     *
     * @param aTokenService
     *            the token service full URL (example: https://arcgis.msfuat.de/portal/sharing/rest/generateToken)
     * @param aUsername
     *            the username who wants to get a token
     * @param aPassword
     *            the password of user who wants to get a token
     * @param aReferer
     *            the referer the base URL of the client application that will use the token to access the ArcGIS server
     * @param aRequestIP
     *            <code>true</code> to use the <code>requestip</code> client identification instead of <code>referer</code>, more
     *            information on https://developers.arcgis.com/rest/services-reference/generate-token.htm
     * @param aExpiration
     *            token expiration time in minutes, if <0 the request is done without the expiration parameter (default is 60 minutes)
     * @return the token token generated in exchange for user credentials
     * @throws ISException
     *             if the token can't be generated from the token service
     */
    public static JsonObject getToken(String aTokenService, String aUsername, String aPassword, String aReferer, boolean aRequestIP,
            int aExpiration) throws ISException {
        Client client = ch.inser.rest.util.ISClientBuilder.build();

        WebTarget target = client.target(aTokenService).queryParam("f", "json");

        Form form = new Form().param("username", aUsername).param("password", aPassword);
        form = aRequestIP ? form.param("client", "requestip") : form.param("referer", aReferer);
        form = aExpiration >= 0 ? form.param("expiration", Integer.toString(aExpiration)) : form;

        logger.debug("getToken: request to: " + target.getUri());
        Response response = target.request().post(Entity.form(form));
        logger.debug("getToken: response HTTP code: " + response.getStatus());

        // Check the result
        if (response.getStatus() != Status.OK.getStatusCode()) {
            logger.error("getToken: HTTP error code: " + response.getStatus());
            throw new ISException("Error getting a new token: " + response.getStatus());
        }

        try (JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)))) {
            return reader.readObject();
        }
    }

    /**
     * Get a token for an ArcGIS server.
     *
     * @param aTokenService
     *            the token service full URL (example: https://arcgis.msfuat.de/portal/sharing/rest/generateToken)
     * @param aUsername
     *            the username who wants to get a token
     * @param aPassword
     *            the password of user who wants to get a token
     * @param aReferer
     *            the referer the base URL of the client application that will use the token to access the ArcGIS server
     * @param aRequestIP
     *            <code>true</code> to use the <code>requestip</code> client identification instead of <code>referer</code>, more
     *            information on https://developers.arcgis.com/rest/services-reference/generate-token.htm
     * @return the token token generated in exchange for user credentials
     * @throws ISException
     *             if the token can't be generated from the token service
     */
    public static JsonObject getToken(String aTokenService, String aUsername, String aPassword, String aReferer, boolean aRequestIP)
            throws ISException {
        return getToken(aTokenService, aUsername, aPassword, aReferer, aRequestIP, -1);
    }

    /**
     * Get a token for the default ArcGIS server.
     *
     * @param aContextManager
     *            the context manager to retrieve "map.service.token.service", "map.service.token.username" and "map.service.token.password"
     *            properties
     * @param aReferer
     *            the referer the base URL of the client application that will use the token to access the ArcGIS server
     * @param aRequestIP
     *            <code>true</code> to use the <code>requestip</code> client identification instead of <code>referer</code>, more
     *            information on https://developers.arcgis.com/rest/services-reference/generate-token.htm
     * @return the token token generated in exchange for user credentials
     * @throws ISException
     *             if the token can't be generated from the token service
     */
    public static JsonObject getToken(IContextManager aContextManager, String aReferer, boolean aRequestIP) throws ISException {
        return getToken(aContextManager.getProperty("map.service.token.service"), aContextManager.getProperty("map.service.token.username"),
                aContextManager.getProperty("map.service.token.password"), aReferer, aRequestIP,
                getTimeout(aContextManager, "map.service"));
    }

    /**
     * Get a token for a given ArcGIS server.
     *
     * @param aContextManager
     *            the context manager to retrieve "servicename.token.service", "servicename.token.username" and "servicename.token.password"
     *            properties
     * @param aReferer
     *            the referer the base URL of the client application that will use the token to access the ArcGIS server
     * @param aServiceName
     *            Name of service, used as prefix of the user/pwd properties
     * @param aRequestIP
     *            <code>true</code> to use the <code>requestip</code> client identification instead of <code>referer</code>, more
     *            information on https://developers.arcgis.com/rest/services-reference/generate-token.htm
     * @return the token token generated in exchange for user credentials
     * @throws ISException
     *             if the token can't be generated from the token service
     */
    public static JsonObject getToken(IContextManager aContextManager, String aReferer, String aServiceName, boolean aRequestIP)
            throws ISException {
        return getToken(aContextManager.getProperty(aServiceName + ".token.service"),
                aContextManager.getProperty(aServiceName + ".token.username"),
                aContextManager.getProperty(aServiceName + ".token.password"), aReferer, aRequestIP,
                getTimeout(aContextManager, aServiceName));
    }

    /**
     * Get a token for a given ArcGIS server.
     *
     * @param aProperties
     *            the properties file to retrieve "servicename.token.service", "servicename.token.username" and "servicename.token.password"
     *            properties
     * @param aServiceName
     *            Name of service, used as prefix of the user/pwd properties
     * @return the token token generated in exchange for user credentials
     * @throws ISException
     *             if the token can't be generated from the token service
     */
    public static JsonObject getToken(Properties aProperties, String aServiceName) throws ISException {
        return getToken(aProperties.getProperty(aServiceName + ".token.service"), aProperties.getProperty(aServiceName + ".username"),
                aProperties.getProperty(aServiceName + ".password"), aProperties.getProperty(aServiceName + ".referer"),
                "true".equals(aProperties.getProperty(aServiceName + ".requestip")));
    }

    /**
     * Get the timeout from the context manage for a given service.
     *
     * @param aContextManager
     *            the context manager
     * @param aServiceName
     *            the service name
     * @return the timeout in minutes or <code>-1</code> if not found
     */
    private static int getTimeout(IContextManager aContextManager, String aServiceName) {
        String timeout = aContextManager.getProperty(aServiceName + ".token.timeout");
        if (timeout != null && timeout.length() > 0) {
            return Integer.parseInt(timeout);
        }
        return -1;
    }
}
