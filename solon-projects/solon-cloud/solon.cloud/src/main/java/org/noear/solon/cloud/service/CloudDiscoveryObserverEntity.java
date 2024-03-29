package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.utils.DiscoveryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云端发现观察者实体
 *
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryObserverEntity implements CloudDiscoveryHandler {
    static final Logger log = LoggerFactory.getLogger(CloudDiscoveryObserverEntity.class);

    public String group;
    public String service;
    public CloudDiscoveryHandler handler;

    public CloudDiscoveryObserverEntity(String group, String service, CloudDiscoveryHandler handler) {
        this.group = group;
        this.service = service;
        this.handler = handler;
    }

    @Override
    public void handle(Discovery discovery) {
        try {
            //尝试增加发现代理
            DiscoveryUtils.tryLoadAgent(discovery, group, service);

            handler.handle(discovery);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}