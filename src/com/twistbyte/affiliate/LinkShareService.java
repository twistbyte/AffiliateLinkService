package com.twistbyte.affiliate;

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
 *The service object that will access LinkShare webservice API to generate links for a sites url
 *
 * User: cbrandt
 */
public class LinkShareService {

    private static final String prefix = "http://getdeeplink.linksynergy.com/createcustomlink.shtml?";

    private String key;
    private String mid;
    private final HttpUtility httpUtility = new HttpUtility();

    public LinkShareService(String key, String mid){
        this.key = key;
        this.mid = mid;

    }


    public String generateLink(String url){

        String callUrl = prefix + "token=" + key + "&mid=" + mid + "&murl=" + url;

        HttpResult r  = httpUtility.doGet(callUrl);

        // if server error return back url passed
        if (!r.isSuccess()){
            return url;
        }

        // if no response pass back url
        if (r.getBody() == null){
            return url;
        }

        return r.getBody();

    }

    public static void main(String[] args){
        LinkShareService ls = new LinkShareService("your dev key","your mid");
        System.out.println(ls.generateLink("http://mobile.buy.com/ibuy/Product.aspx?sku=221894403"));

    }




}
