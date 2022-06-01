package io.github.ipagentpool.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @Author 翁丞健
 * @Date 2022/5/29 18:58
 * @Version 1.0.0
 */
@Slf4j
public class IpUtil {

    private static final String  USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36";

    private static final String DEFAULT_REQUEST_URL = "http://www.baidu.com";

    private static final String SEARCH_CONTENT = "百度一下，你就知道";
    /**
     * 代理IP有效检测
     *
     * @param proxyIp
     * @param proxyPort
     */
    public static boolean checkProxyIp(String proxyIp, int proxyPort) {

        try(CloseableHttpClient client = HttpClientBuilder.create().setProxy(new HttpHost(proxyIp,proxyPort)).build();){
            HttpGet httpGet = new HttpGet(DEFAULT_REQUEST_URL);
            httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
            httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
            httpGet.setHeader("User-Agent", USER_AGENT);
            RequestConfig config = RequestConfig.custom().setConnectTimeout(2000).setCookieSpec(CookieSpecs.STANDARD).setSocketTimeout(2000).build();
            httpGet.setConfig(config);

            HttpResponse response = client.execute(httpGet);
            int statuCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

            if(statuCode == 200){
                if(EntityUtils.toString(entity,"utf-8").contains(SEARCH_CONTENT)){
                    log.info("{}:{} is valid",proxyIp,proxyPort);
                    return true;
                }
            }
            log.info("{}:{} is invalid",proxyIp,proxyPort);
            return false;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.info("{}:{} is invalid",proxyIp,proxyPort);
            return false;
        }
    }
}
