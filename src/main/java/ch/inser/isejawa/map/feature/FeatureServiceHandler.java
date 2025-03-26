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

package ch.inser.isejawa.map.feature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.rest.util.ISClientBuilder;
import ch.inser.rest.util.JsonUtil;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

/**
 * Handler to work with feature services.
 *
 * @author INSER SA
 */
public class FeatureServiceHandler {

    /**
     * The logger.
     */
    private static final Log logger = LogFactory.getLog(FeatureServiceHandler.class);

    /**
     * Private constructor to hide the public one, since all methods are static
     */
    private FeatureServiceHandler() {
    }

    /**
     * Parameter to use when there is no value to provide
     */
    private static final String EMTPY_PARAMETER = "";

    /**
     * Get a feature.
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aObjectId
     *            the object id
     * @param aToken
     *            the token
     * @return the feature
     */
    public static JsonObject getFeature(String aFeatureUrl, int aObjectId, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(String.format("%s/%s", aFeatureUrl, aObjectId)).queryParam("f", "json").queryParam("token",
                aToken);
        logger.debug(String.format("getFeature: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getFeature: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * Get features
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aWhere
     *            the where clause
     * @param aToken
     *            the token
     * @return the features
     */
    public static JsonObject getFeatures(String aFeatureUrl, String aWhere, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/query").queryParam("where", aWhere).queryParam("outFields", "*")
                .queryParam("f", "json").queryParam("token", aToken);
        logger.debug(String.format("getFeatures: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getFeatures: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * Get features
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aWhere
     *            the where clause
     * @param aToken
     *            the token
     * @param outFields
     *            the outField
     * @param geometry
     *            the extent
     * @param orderByFields
     *            sorting by fields
     * @param returnCountOnly
     *            if true return only count, false get the features
     * @param returnGeometry
     *            if true return the geometry of objects, false no geometry
     * @param resultOffset
     *            if set, return only a portion of the result (pagination)
     * @param resultRecordCount
     *            if set, return only a portion of the result (pagination)
     * @return the features
     */
    public static JsonObject getFeatures(String aFeatureUrl, String aToken, String aWhere, String outFields, String geometry,
            String orderByFields, Boolean returnCountOnly, Boolean returnGeometry, String resultOffset, String resultRecordCount) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/query").queryParam("f", "json").queryParam("token", aToken)
                .queryParam("where", aWhere).queryParam("returnCountOnly", returnCountOnly).queryParam("outFields", outFields)
                .queryParam("geometry", geometry).queryParam("orderByFields", orderByFields).queryParam("returnGeometry", returnGeometry)
                .queryParam("resultOffset", resultOffset).queryParam("resultRecordCount", resultRecordCount);

        logger.debug(String.format("getFeatures: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getFeatures: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aToken
     *            the token
     * @param aWhere
     *            aWhere the where clause
     * @param outFields
     *            the outField
     * @param geometry
     *            the extent
     * @param orderByFields
     *            sorting by fields
     * @param aGeometryType
     *            the geometryType (e.g: esriGeometryPoint)
     * @param aInSR
     *            the Input Spatial Reference
     * @param resultOffset
     *            if set, return only a portion of the result (pagination)
     * @param resultRecordCount
     *            if set, return only a portion of the result (pagination)
     * @param returnCountOnly
     *            if true return only count, false get the features
     * @param returnGeometry
     *            if true return the geometry of objects, false no geometry
     * @return a JsonObject that is the arcGis answer
     */
    public static JsonObject getFeatures(String aFeatureUrl, String aToken, String aWhere, String outFields, String geometry,
            String orderByFields, String aGeometryType, String aInSR, String resultOffset, String resultRecordCount,
            Boolean returnCountOnly, Boolean returnGeometry) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/query").queryParam("f", "json").queryParam("token", aToken)
                .queryParam("where", aWhere).queryParam("returnCountOnly", returnCountOnly).queryParam("outFields", outFields)
                .queryParam("geometry", geometry).queryParam("orderByFields", orderByFields).queryParam("returnGeometry", returnGeometry)
                .queryParam("resultOffset", resultOffset).queryParam("resultRecordCount", resultRecordCount).queryParam("inSR", aInSR)
                .queryParam("geometryType", aGeometryType);

        logger.debug(String.format("getFeatures: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getFeatures: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aWhere
     *            aWhere the where clause
     * @param outFields
     *            the outField
     * @param geometry
     *            the extent, must be encoded for URL
     * @param aGeometryType
     *            the geometryType (e.g: esriGeometryPoint)
     * @param returnGeometry
     *            if true return the geometry of objects, false no geometry
     * @return a JsonObject that is the arcGis answer
     */
    public static JsonObject getFeatures(String aFeatureUrl, String aWhere, String outFields, String geometry, String aGeometryType,
            Boolean returnGeometry) {
        return getFeatures(aFeatureUrl, EMTPY_PARAMETER, aWhere, outFields, geometry, EMTPY_PARAMETER, aGeometryType, EMTPY_PARAMETER,
                EMTPY_PARAMETER, EMTPY_PARAMETER, false, false);
    }

    /**
     * Get features from webTarget
     *
     * @param target
     *            the web target
     * @return the features
     */
    public static JsonObject getFeaturesFromTarget(WebTarget target) {
        logger.debug(String.format("getFeatures: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getFeatures: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * Get extent
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aWhere
     *            the where clause
     * @param aToken
     *            the token
     * @return the features
     */
    public static JsonObject getExtent(String aFeatureUrl, String aWhere, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/query").queryParam("where", aWhere).queryParam("returnExtentOnly", true)
                .queryParam("f", "json").queryParam("token", aToken);
        logger.debug(String.format("getFeatures: request to='%s'", target.getUri()));
        Response response = target.request().get();
        logger.debug(String.format("getFeatures: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class)).getJsonObject("extent");
    }

    /**
     * Get a feature attribute value.
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aObjectId
     *            the object id
     * @param aAttributeName
     *            the attribute name
     * @param aToken
     *            the token
     * @return the attribute value
     */
    public static JsonValue getFeatureAttribute(String aFeatureUrl, int aObjectId, String aAttributeName, String aToken) {
        return getFeatureAttributes(aFeatureUrl, aObjectId, aToken).get(aAttributeName);
    }

    /**
     * Get all feature attributes.
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aObjectId
     *            the object id
     * @param aToken
     *            the token
     * @return the attribute value
     */
    public static JsonObject getFeatureAttributes(String aFeatureUrl, int aObjectId, String aToken) {
        JsonObject feature = getFeature(aFeatureUrl, aObjectId, aToken);
        return feature.getJsonObject("feature").getJsonObject("attributes");
    }

    /**
     * @param features
     *            to extract data from
     * @param aAttributeName
     *            the arcgis parameter name
     * @return attribute value or null
     *
     */
    public static JsonValue getAttribute(JsonObject features, String aAttributeName) {
        JsonArray featuresArray = features.getJsonArray("features");
        if (featuresArray == null || featuresArray.isEmpty()) {
            logger.debug("No feature were found or it is empty");
            return null;
        }

        JsonObject attributesObj = featuresArray.getJsonObject(0).getJsonObject("attributes");
        if (attributesObj == null) {
            logger.debug("No attributes were found in features");
            return null;
        }
        return attributesObj.get(aAttributeName);
    }

    /**
     * Adds a feature
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aFeature
     *            the feature to be added in the layer: {attributes: {..}, geometry: {..}, etc.}
     * @param aRollbackOnFailure
     *            do rollback if the operation fails
     * @param aToken
     *            the arcgis authentication token
     * @return the result of the operation: {addResults: [ {objectId: ..., success: true/false}]}
     */
    public static JsonObject addFeature(String aFeatureUrl, JsonObject aFeature, boolean aRollbackOnFailure, String aToken) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        jab.add(aFeature);
        return addFeatures(aFeatureUrl, jab.build(), aRollbackOnFailure, aToken);
    }

    /**
     * Add an array of features
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aFeatures
     *            the features in a json array to be added in the layer: [{attributes: {..}, geometry: {..}, etc.}, ...]
     * @param aRollbackOnFailure
     *            do rollback if the operation fails
     * @param aToken
     *            the arcgis authentication token
     * @return the result of the operation: {addResults: [ {objectId: ..., success: true/false}, ...]}
     */
    public static JsonObject addFeatures(String aFeatureUrl, JsonArray aFeatures, boolean aRollbackOnFailure, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/addFeatures").queryParam("token", aToken);
        logger.debug(String.format("addFeatures: request to='%s'", target.getUri()));
        logger.debug("addFeatures: features " + aFeatures.toString());
        Form form = new Form().param("f", "json").param("rollbackOnFailure", Boolean.toString(aRollbackOnFailure)).param("token", aToken)
                .param("features", aFeatures.toString());
        Response response = target.request().post(Entity.form(form));
        logger.debug(String.format("addFeatures: response HTTP code='%s'", response.getStatus()));

        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * Update a feature
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aFeatures
     *            the feature in a json array to be added in the layer: [{attributes: {..}, geometry: {..}, etc.}, ...]
     * @param aToken
     *            the token
     * @return the result of the operation: {updateResults: [ {objectId: ..., success: true/false}]}
     */
    public static JsonObject updateFeatures(String aFeatureUrl, JsonArray aFeatures, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/updateFeatures");
        logger.debug(String.format("updateFeatures: request to='%s'", target.getUri()));
        logger.debug("updateFeatures: features " + aFeatures.toString());
        Form form = new Form().param("f", "json").param("token", aToken).param("features", aFeatures.toString());
        Response response = target.request().post(Entity.form(form));
        logger.debug(String.format("updateFeatures: response HTTP code='%s'", response.getStatus()));
        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }

    /**
     * Delete features
     *
     * @param aFeatureUrl
     *            the full feature URL including the feature service URL and the layer id (example:
     *            https://arcgis.msfuat.de/server/rest/services/DEV-PRESENCE/Presence/FeatureServer/1)
     * @param aClauseWhere
     *            A where clause for the query filter. Any legal SQL where clause operating on the fields in the layer is allowed. Features
     *            conforming to the specified where clause will be deleted
     * @param aRollbackOnFailure
     *            do rollback if the operation fails
     * @param aToken
     *            the token
     * @return the result of the operation: {deleteResults: [ {objectId: ..., success: true/false},...]}
     */
    public static JsonObject deleteFeatures(String aFeatureUrl, String aClauseWhere, boolean aRollbackOnFailure, String aToken) {
        Client client = ISClientBuilder.build();
        WebTarget target = client.target(aFeatureUrl + "/deleteFeatures");
        logger.debug(String.format("deleteFeatures: request to='%s'", target.getUri()));
        Form form = new Form().param("f", "json").param("token", aToken).param("rollbackOnFailure", Boolean.toString(aRollbackOnFailure))
                .param("where", aClauseWhere);
        Response response = target.request().post(Entity.form(form));
        logger.debug(String.format("deleteFeatures: response HTTP code='%s'", response.getStatus()));
        return JsonUtil.stringToJsonObject(response.readEntity(String.class));
    }
}
