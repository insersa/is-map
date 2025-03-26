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

package ch.inser.isejawa.map.map;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.rest.util.ISClientBuilder;
import ch.inser.rest.util.JsonUtil;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

/**
 * Handler to work with map services.
 *
 * @author INSER SA
 *
 */
public class MapServiceHandler {

    /**
     * The logger.
     */
    private static final Log logger = LogFactory.getLog(MapServiceHandler.class);

    /**
     * Constructor
     */
    private MapServiceHandler() {
    }

    /**
     * Export as an image a portion of the map
     *
     * @param aMapUrl
     *            the map server url
     * @param aBbox
     *            The extent (bounding box) of the exported image. Syntax: xmin, ymin, xmax, ymax.
     * @param aSize
     *            size of the image (example 800,800)
     * @param aTransparent
     *            If true, the image will be exported with the background color of the map set as its transparent color.
     * @param aLayerDefs
     *            Filter the features of individual layers in the exported map by specifying definition expressions for those layers.
     * @param aLayers
     *            Determines which layers appear on the exported map.
     * @param aToken
     *            the token
     * @return the image as a bufferedImage
     * @throws IOException
     *             errors
     */
    public static BufferedImage getMapExport(String aMapUrl, String aBbox, String aSize, boolean aTransparent, String aLayerDefs,
            String aLayers, String aToken) throws IOException {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = null;

        if ("".equals(aToken)) {
            target = client.target(aMapUrl + "/export");
        } else {
            target = client.target(aMapUrl + "/export").queryParam("token", aToken);
        }

        logger.debug(String.format("export: request to='%s'", target.getUri()));
        logger.debug(String.format("export: bbox='%s' size ='%s' transparent='%s' layerDefs='%s' layers='%s'", aBbox, aSize, aTransparent,
                aLayerDefs, aLayers));

        Form form = new Form().param("f", "image").param("bbox", aBbox).param("transparent", String.valueOf(aTransparent));
        if (!"".equals(aSize)) {
            form.param("size", aSize);
        }
        if (!"".equals(aLayerDefs)) {
            form.param("layerDefs", aLayerDefs);
        }
        if (!"".equals(aLayers)) {
            form.param("layers", aLayers);
        }

        Response response = target.request().post(Entity.form(form));
        logger.debug(String.format("export: response HTTP code='%s'", response.getStatus()));

        return ImageIO.read(new ByteArrayInputStream(response.readEntity(byte[].class)));
    }

    /**
     * Get the map domains
     *
     * @param aMapUrl
     *            the map server url
     * @param aLayers
     *            Select the layer to get the domain.
     * @param aToken
     *            the token
     * @return the domains
     */
    public static JsonObject getMapDomains(String aMapUrl, String aLayers, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(String.format("%s/queryDomains", aMapUrl)).queryParam("f", "json").queryParam("token", aToken)
                .queryParam("layers", aLayers);
        logger.debug(String.format("getMapDomains: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getMapDomains: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }
}
