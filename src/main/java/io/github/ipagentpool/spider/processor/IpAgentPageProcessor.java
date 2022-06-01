package io.github.ipagentpool.spider.processor;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import io.github.ipagentpool.config.AgentSiteProperties;
import io.github.ipagentpool.model.IpAgentModel;
import io.github.ipagentpool.spider.fomatter.Fomatter;
import io.github.ipagentpool.spider.fomatter.StringToStringFomatter;
import io.github.ipagentpool.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 翁丞健
 * @Date 2022/5/28 19:55
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class IpAgentPageProcessor implements PageProcessor {

    private static final Integer MAX_CRAWL_NUM = 10;

    private final AgentSiteProperties properties;

    private static final Integer SLEEP_TIME  = 1000;

    private static final Integer DEFAULT_MAX_SIZE = 100;

    private static final String  USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36";


    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private static final Site site = Site.me()
            .setRetryTimes(3)
            .setUserAgent(USER_AGENT)
            .setSleepTime(SLEEP_TIME);


    private Set<String> addresses;

    private Map<String, AgentSiteProperties.IpAgent> ipAgents;

    @PostConstruct
    public void init(){

        ipAgents = properties.getIpAgents().stream().collect(Collectors.toMap(AgentSiteProperties.IpAgent::getSiteName,(item)->item));

        addresses = ipAgents.keySet();
    }

    /**
     * 因为代理ip时效性比较强，所以默认只爬一天内的数据
     * @param date
     * @return
     */
    public boolean checkRecentTime(Date date){
        final LocalDateTime aTime = LocalDateTime.now().minus(1,DateUnit.DAY.toChronoUnit());
        LocalDateTime bTime = DateUtil.toLocalDateTime(date);
        return bTime.isAfter(aTime);

    }

    @SneakyThrows
    @Override
    public void process(Page page) {
        List<IpAgentModel> res = new ArrayList<>();
        for (String address : addresses) {
            String url = page.getRequest().getUrl();
            if (url.startsWith(address)) {
                AgentSiteProperties.IpAgent ipAgent = ipAgents.get(address);
                Map<String, Integer> position = ipAgent.getPosition();
                String nextLinkRegex = ipAgent.getNextLinkRegex();
                Map<String, AgentSiteProperties.IpAgent.Rule> rules = ipAgent.getRules();
                List<Selectable> nodes = page.getHtml().xpath(ipAgent.getAnalyzeTr()).nodes();
                /**
                 *  没有期望中的节点直接返回
                 */
                if(nodes==null || nodes.size()==0){
                    return;
                }

                for(int i =1;i<nodes.size();i++){

                    Selectable node = nodes.get(i);

                    IpAgentModel ipAgentModel = convert(position,node,rules);

                    if(ipAgentModel!=null){
                        res.add(ipAgentModel);
                    }
                }
                page.putField("models", res);
                page.addTargetRequests(page.getHtml().links().regex(String.format("(%s)", nextLinkRegex)).all());
            }
        }

    }

    public IpAgentModel convert(Map<String, Integer> position,Selectable node,Map<String, AgentSiteProperties.IpAgent.Rule> rules) throws Exception {
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
                if(value!=null && value.length()!=0){
                    value = value.strip();
                }
                // 校验ip和port是否为空
                if(("ip".equals(key) || "port".equals(key)) && (value==null || value.length()==0)){
                    return null;
                }

                Class<? extends Fomatter<String,?>> type = rule.getFomatter();

                type = type!=null ? type : StringToStringFomatter.class;

                Fomatter<String, ?> fomatter = type.newInstance();
                obj = fomatter.doFormat(value);

                if("updateTime".equals(key)){
                    if(!checkRecentTime((Date) obj)){
                        return null;
                    }
                }
            }else{
                obj = rule.getDefaultValue();
            }
            if(obj!=null && !"updateTime".equals(key)){
                ReflectUtil.invoke(ipAgentModel, StringUtils.getSetter(key),obj);
            }
        }

        return ipAgentModel;
    }

    @Override
    public Site getSite() {
        return site;
    }
}
