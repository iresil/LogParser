package com.example.LogParser;

public class RequestModel {
    /**
     * The host that performed the request
     */
    String host;

    /**
     * The http verb that was used for the request
     */
    String httpVerb;

    /**
     * The resource that was requested
     */
    String resource;

    /**
     * The response code returned
     */
    String responseCode;

    /**
     * Checks whether the request was successful or not
     * @return True if successful
     */
    public Boolean isSuccessful() {
        if (responseCode == null)
            return false;

        return responseCode.startsWith("2") || responseCode.startsWith("3");
    }

    /**
     * Checks if the given field has been parsed correctly
     * @param field A string containing a field value
     * @return True if the field was parsed correctly
     */
    private Boolean isFieldValid(String field) {
        return field != null;
    }

    /**
     * Checks whether any valid fields exist in this RequestModel
     * @return True if any of the fields are valid
     */
    public Boolean validFieldsExist() {
        return isFieldValid(host) || isFieldValid(httpVerb) || isFieldValid(resource) || isFieldValid(responseCode);
    }

    /**
     * Checks if the host text was correctly formatted, and it has been parse correctly
     * @return True if host was parsed correctly
     */
    public Boolean isHostValid() {
        return isFieldValid(host) && host.contains(".");
    }

    /**
     * Checks if the httpVerb text was correctly formatted, and it has been parse correctly
     * @return True if httpVerb was parsed correctly
     */
    public Boolean isHttpVerbValid() {
        return isFieldValid(httpVerb);
    }

    /**
     * Checks if the resource text was correctly formatted, and it has been parse correctly
     * @return True if resource was parsed correctly
     */
    public Boolean isResourceValid() {
        return isFieldValid(resource);
    }

    /**
     * Checks if the responseCode text was correctly formatted, and it has been parse correctly
     * @return True if responseCode was parsed correctly
     */
    public Boolean isResponseCodeValid() {
        return isFieldValid(responseCode);
    }
}
