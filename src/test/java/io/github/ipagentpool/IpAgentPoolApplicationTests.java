package io.github.ipagentpool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class IpAgentPoolApplicationTests {

    @Test
    void contextLoads() {
        Spider.create(new IpAgentPageProcessor()).addPipeline(new ConsolePipeline()).addUrl("https://ip.ihuan.me/").thread(1).run();
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
