package io.github.ipagentpool.spider.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wengchengjian
 * @date 2022/6/1-9:49
 */
public class IpAgentPoolScheduler extends BalanceMultiQueueScheduler{

    /**
     * 只爬单个代理ip网站100页
     */
    private final static Integer SINGEL_SITE_MAX_CRAWL_NUM = 30;

    @Override
    protected boolean allowAddRequest(Request request) {
        String domain = UrlUtils.getDomain(request.getUrl());
        for (SiteCall site : sites) {
            // 判断当前url的所属网站是否超过调用次数
            if(site.getSite().equals(domain)){
                if(site.getCallNum().intValue() > SINGEL_SITE_MAX_CRAWL_NUM){
                    return false;
                }
            }
        }
        return true;
    }
}
