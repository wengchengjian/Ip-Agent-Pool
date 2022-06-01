package io.github.ipagentpool.spider.scheduler;

import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author wengchengjian
 * @date 2022/6/1-9:26
 */
public class BalanceMultiQueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    protected Map<String, BlockingQueue<Request>> queueMap = new ConcurrentHashMap<>();

    protected List<SiteCall> sites = new CopyOnWriteArrayList<>();

    protected Set<String> uniqueSite = new ConcurrentHashSet<>();

    private final AtomicInteger pointer = new AtomicInteger(0);

    @Data
    @EqualsAndHashCode(exclude = "callNum")
    public static class SiteCall{
        private String site;

        private AtomicInteger callNum = new AtomicInteger(0);

        public void increment(){
            this.callNum.incrementAndGet();
        }
    }
    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        String domain = UrlUtils.getDomain(request.getUrl());
        // 如果需要初始化的话
        queueMap.computeIfAbsent(domain,(d)-> new LinkedBlockingQueue<>());

        BlockingQueue<Request> queue = queueMap.get(domain);
        if(allowAddRequest(request)){
            if(!uniqueSite.contains(domain)){
                SiteCall siteCall = new SiteCall();
                siteCall.setSite(domain);
                sites.add(siteCall);
                uniqueSite.add(domain);
            }
            queue.offer(request);
        }
    }

    protected boolean allowAddRequest(Request request) {
        return true;
    }

    @Override
    public int getLeftRequestsCount(Task task) {
    return queueMap.values().stream().map(Collection::size).reduce(0,(a,b)->a+b);
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }

    @Override
    public Request poll(Task task) {
        SiteCall siteCall = sites.get(incrForLoop());
        String site = siteCall.getSite();

        BlockingQueue<Request> queue = queueMap.get(site);
        if(queue!=null){
            siteCall.increment();
            return queue.poll();
        }else{
            throw new IllegalArgumentException("not crawl this site");
        }
    }

    private int incrForLoop() {
        int p = pointer.incrementAndGet();
        int size = sites.size();
        if(size==0){
            throw new IndexOutOfBoundsException();
        }
        if (p < size) {
            return p;
        }
        while (!pointer.compareAndSet(p, p % size)) {
            p = pointer.get();
        }
        return p % size;
    }
}
