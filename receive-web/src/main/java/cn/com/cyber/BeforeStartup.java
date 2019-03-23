package cn.com.cyber;

import cn.com.cyber.util.CodeInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Date;

@Configuration
public class BeforeStartup implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeforeStartup.class);
	@Autowired
	private CodeInfoService codeInfoService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		LOGGER.info("BeforeStartup initCodeMap :{}", DateUtil.format(new Date(), "yyyy-MM-dd"));
		CodeInfoUtils.setCodeInfoMap(codeInfoService.getCodeInfoMap());
		CodeInfoUtils.setCodeListMap(codeInfoService.getCodeListMap());
	}

}