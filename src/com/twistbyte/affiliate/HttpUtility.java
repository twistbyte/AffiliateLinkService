package com.twistbyte.affiliate;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Copyright (c) 2012 TwistByte LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Common HTTP utility
 * User: cbrandt
 */
public class HttpUtility {

    private static final Logger logger = Logger.getLogger(HttpUtility.class);
    private HttpClient httpclient;
    private int connectionTimeout = 8000;

    public HttpUtility() {

        MultiThreadedHttpConnectionManager connectionManager = new
                MultiThreadedHttpConnectionManager();

        connectionManager.getParams().setDefaultMaxConnectionsPerHost(50);
        connectionManager.getParams().setMaxTotalConnections(50);


        httpclient = new HttpClient(connectionManager);

        // connect timeout in 8 seconds
        httpclient.getParams().setParameter("http.connection.timeout", new Integer(8000));
        httpclient.getParams().setParameter("http.socket.timeout", new Integer(300000));


        Protocol protocol = new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", protocol);
    }

    /**
     * Do a get request to given url
     * @param url
     * @return
     */
    public HttpResult doGet(String url) {
        return doGet(url, null);
    }


    /**
     * Do a get request to the given url and set the header values passed
     *
     * @param url
     * @param headerValues
     * @return
     */
    public HttpResult doGet(String url, Map<String, String> headerValues) {

        String resp = "";

        logger.info("url : " + url);
        GetMethod method = new GetMethod(url);

        logger.info("Now use connection timeout: " + connectionTimeout);
        httpclient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connectionTimeout);


        if (headerValues != null) {
            for (Map.Entry<String, String> entry : headerValues.entrySet()) {

                method.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpResult serviceResult = new HttpResult();
        serviceResult.setSuccess(false);
        serviceResult.setHttpStatus(404);
        Reader reader = null;
        try {


            int result = httpclient.executeMethod(new HostConfiguration(), method, new HttpState());
            logger.info("http result : " + result);
            resp = method.getResponseBodyAsString();

            logger.info("response: " + resp);

            serviceResult.setHttpStatus(result);
            serviceResult.setBody(resp);
            serviceResult.setSuccess(200 == serviceResult.getHttpStatus());
        } catch (java.net.ConnectException ce) {
            logger.warn("ConnectException: " + ce.getMessage());
        } catch (IOException e) {
            logger.error("IOException sending request to " + url + " Reason:" + e.getMessage(), e);
        } finally {
            method.releaseConnection();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }

        return serviceResult;
    }

}
