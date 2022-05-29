package io.github.ipagentpool;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import io.github.ipagentpool.config.AgentSiteProperties;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 19:55
 * @Version 1.0.0
 */
public class IpAgentPageProcessor implements PageProcessor {

    private static final Integer SLEEP_TIME  = 500;

    private static final Integer DEFAULT_MAX_SIZE = 100;


    private static final String  USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36";


    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private static final Site site = Site.me()
            .setRetryTimes(3)
            .setUserAgent(USER_AGENT)
            .setSleepTime(SLEEP_TIME);


    private Set<String> addresses;

    private Map<String, AgentSiteProperties.IpAgent> ipAgents=new ConcurrentHashMap<>();

    public IpAgentPageProcessor(){

        AgentSiteProperties.IpAgent ipAgent = new AgentSiteProperties.IpAgent();
        ipAgent.setAnalyzeTr("//div[@class='table-responsive']/table/tbody/tr");
        ipAgent.setPosition(new ConcurrentHashMap<>(){{
            put("ip",1);
            put("port",2);
            put("anonymity",7);
            put("address",3);
        }});
        ipAgent.setNextLinkRegex("https://ip.ihuan.me/?page=\\\\w+");
        Map<String, AgentSiteProperties.IpAgent.Rule> rules = ipAgent.getRules();
        rules.put("ip",new AgentSiteProperties.IpAgent.Rule<String>("/a/text()",false,null,null,String.class));
        rules.put("address",new AgentSiteProperties.IpAgent.Rule<String>("/a/text()",true,"-",null,String.class));
        rules.put("anonymity",new AgentSiteProperties.IpAgent.Rule<String>("/a/text()",false,null,null,String.class));
        ipAgents.put("https://ip.ihuan.me/",ipAgent);
    }

    @Override
    public void process(Page page) {
        List<IpAgentModel> res = new ArrayList<>();
            String url = page.getRequest().getUrl();
                AgentSiteProperties.IpAgent ipAgent = ipAgents.get("https://ip.ihuan.me/");

        Map<String, Integer> position = ipAgent.getPosition();

                String nextLinkRegex = ipAgent.getNextLinkRegex();

        Map<String, AgentSiteProperties.IpAgent.Rule> rules = ipAgent.getRules();

        List<Selectable> nodes = page.getHtml().xpath(ipAgent.getAnalyzeTr()).nodes();
                for(int i =1;i<nodes.size();i++){
                    
                    Selectable node = nodes.get(i);
                    
                    IpAgentModel ipAgentModel = new IpAgentModel();
                    for (String key : position.keySet()) {
                        
                        Integer posi = position.get(key);

                        AgentSiteProperties.IpAgent.Rule rule = rules.getOrDefault(key, AgentSiteProperties.IpAgent.Rule.DEFAULT_STRING_RULE);

                        // 从html中拿到的字符串
                        String value = null;

                        // 最终的值
                        Object obj = null;

                        if(posi!=-1){
                            Selectable xpath = node.xpath(String.format("//td[%d]%s", posi, rule.getRule()));
                            if(rule.isMulti()){
                                value = String.join(rule.getSeparator(), xpath.all());
                            }else{
                                value = xpath.get();
                            }

                            Class type = rule.getClazz();

                            obj = TypeUtils.cast(value,type,new ParserConfig());
                        }else{
                            obj = rule.getDefaultValue();
                        }

                        ReflectUtil.invoke(ipAgentModel, StringUtils.getSetter(key),obj);

                    }
                    res.add(ipAgentModel);
                }
                page.putField("models",res);
                page.addTargetRequests(page.getHtml().links().regex(String.format("(%s)",nextLinkRegex)).all());
    }

    @Override
    public Site getSite() {
        return site;
    }
}
