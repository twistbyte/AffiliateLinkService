package com.twistbyte.affiliate;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import java.util.HashMap;
import java.util.List;
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
 * The service object that will access Commission Junction webservice API to generate links for a sites url
 *
 */
public class CommissionJunctionService {


    private static final String prefix = "https://product-search.api.cj.com/v2/product-search";
    public static final String AUTHENTICATION_KEY = "authorization";
    public static final String BUY_URL_ELEMENT = "buy-url";
    private final HttpUtility httpUtility = new HttpUtility();

    private final String pid;
    private final String key;


    public CommissionJunctionService(String key, String pid) {
        this.key = key;
        this.pid = pid;
    }

    public String generateMobileLink(String itemId, String defaultUrl, String domain) {

        String url = generateLink(itemId, defaultUrl);

        // if returned url == default url than something failed, just return default
        if (defaultUrl.equals(url)){
            return defaultUrl;
        }

        // convert to mobile
        return convertToMobile(url, domain);


    }


    public String generateLink(String itemId, String defaultUrl) {


        String url = prefix + "?website-id=" + pid + "&advertiser-sku=" + itemId;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AUTHENTICATION_KEY, key);
        HttpResult r = httpUtility.doGet(url, headers);

        // if server error, return default
        if (!r.isSuccess()){
            return defaultUrl;
        }

        String value = getProperty(r.getBody(), BUY_URL_ELEMENT);

        // if parse error return default
        if (value == null) {
            return defaultUrl;
        }

        return value;


    }

    private String getProperty(String xml, String prop) {

        try {

            Document doc = DocumentHelper.parseText(xml);
            XPath xpath = new Dom4jXPath("//" + prop);
            List nodes = xpath.selectNodes(doc);
            String url = ((Element) nodes.get(0)).getText();

            return url;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String convertToMobile(String url, String domain) {
        return url.replace("www." + domain, "m." + domain);

    }


    public static void main(String[] args) {
        CommissionJunctionService cj = new CommissionJunctionService("dev key", "your pid");
        System.out.println(cj.generateMobileLink("N82E16882021123", "default/url", "newegg"));
        System.out.println(cj.generateLink("N82E16882021123", "default/url"));

    }
}
