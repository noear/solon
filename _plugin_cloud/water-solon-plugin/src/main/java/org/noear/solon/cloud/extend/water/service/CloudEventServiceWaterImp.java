package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.water.WW;
import org.noear.water.WaterClient;
import org.noear.water.utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceWaterImp implements CloudEventService {
    static Logger log = LoggerFactory.getLogger(CloudEventServiceWaterImp.class);

    private final String DEFAULT_DEAL = "Pckb6BpGzDE6RUIy";
    private String seal;
    private Map<String, CloudEventObserverEntity> instanceObserverMap = new HashMap<>();
    private Map<String, CloudEventObserverEntity> clusterObserverMap = new HashMap<>();
    private boolean unstable;
    private String eventChannelName;

    public CloudEventServiceWaterImp() {
        this.unstable = WaterProps.instance.getDiscoveryUnstable()
                || Solon.cfg().isFilesMode()
                || Solon.cfg().isDriftMode();

        this.eventChannelName = WaterProps.instance.getEventChannel();

        this.seal = WaterProps.instance.getEventSeal();

        if (Utils.isEmpty(seal)) {
            seal = DEFAULT_DEAL;
        }
    }

    public String getSeal() {
        return seal;
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        if (Utils.isEmpty(event.topic())) {
            throw new IllegalArgumentException("Event missing topic");
        }

        if (Utils.isEmpty(event.content())) {
            throw new IllegalArgumentException("Event missing content");
        }

        try {
            return WaterClient.Message.
                    sendMessageAndTags(event.key(), event.topic(), event.content(), event.scheduled(), event.tags());
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
    }

    /**
     * 登记关注
     */
    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        if (level == EventLevel.instance) {
            instanceObserverMap.putIfAbsent(topic, new CloudEventObserverEntity(level, group, topic, observer));
        } else {
            clusterObserverMap.putIfAbsent(topic, new CloudEventObserverEntity(level, group, topic, observer));
        }
    }

    /**
     * 执行订阅
     */
    public void subscribe() {
        try {
            subscribe0();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void subscribe0() throws Exception {
        Instance instance = Instance.local();

        //
        //subscribeTopic(String subscriber_key, String subscriber_note, String receive_url, String access_key, String alarm_mobile, int receive_way, boolean is_unstable, String... topics)
        //
        if (instanceObserverMap.size() > 0) {
            String instance_receiver_url = "http://" + instance.address() + WW.path_msg_receiver;
            String instance_subscriber_Key = EncryptUtils.md5(instance.service() + "_instance_" + instance_receiver_url);

            WaterClient.Message.subscribeTopic(instance_subscriber_Key,
                    instance.service(),
                    instance_receiver_url,
                    seal,
                    "",
                    1,
                    unstable,
                    String.join(",", instanceObserverMap.keySet()));
        }

        if (clusterObserverMap.size() > 0) {
            String cluster_hostname = WaterProps.instance.getEventReceive();
            if (Utils.isEmpty(cluster_hostname)) {
                cluster_hostname = instance.address();
            }

            String cluster_receiver_url = "http://" + cluster_hostname + WW.path_msg_receiver;
            String cluster_subscriber_Key = EncryptUtils.md5(instance.service() + "_cluster_" + cluster_receiver_url);

            WaterClient.Message.subscribeTopic(cluster_subscriber_Key,
                    instance.service(),
                    cluster_receiver_url,
                    seal,
                    "",
                    1,
                    false,
                    String.join(",", clusterObserverMap.keySet()));
        }
    }


    /**
     * 处理接收事件
     */
    public boolean onReceive(Event event) throws Throwable {
        boolean isOk = true;
        boolean isHandled = false;
        CloudEventObserverEntity entity = null;

        event.channel(eventChannelName);

        entity = instanceObserverMap.get(event.topic());
        if (entity != null) {
            isHandled = true;
            isOk = entity.handler(event);
        }

        entity = clusterObserverMap.get(event.topic());
        if (entity != null) {
            isHandled = true;
            isOk = entity.handler(event) && isOk; //两个都成功，才是成功
        }

        if(isHandled == false){
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]",event.topic());
        }

        return isOk;
    }
}
