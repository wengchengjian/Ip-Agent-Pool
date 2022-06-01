package io.github.ipagentpool;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import io.github.ipagentpool.spider.processor.IpAgentPageProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class IpAgentPoolApplicationTests {

    @Test
    public void test3(){
        final LocalDateTime aTime = LocalDateTime.now().minus(1, DateUnit.DAY.toChronoUnit());
        final LocalDateTime bTime = LocalDateTime.now().minus(2, DateUnit.DAY.toChronoUnit());
        System.out.println(bTime.isAfter(aTime));
    }

    @Test
    void contextLoads() {
//        Spider.create(new IpAgentPageProcessor()).addPipeline(new ConsolePipeline()).addUrl("https://ip.ihuan.me/").thread(1).run();
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger a  = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(100);
        for(int i =0;i<100;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    a.getAndIncrement();
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        System.out.println(a.intValue());
    }

}
