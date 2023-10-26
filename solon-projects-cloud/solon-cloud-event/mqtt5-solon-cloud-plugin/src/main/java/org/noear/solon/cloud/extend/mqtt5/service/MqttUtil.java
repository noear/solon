package org.noear.solon.cloud.extend.mqtt5.service;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventObserver;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;

/**
 * @author noear
 * @since 2.4
 */
public class MqttUtil {
    /**
     * 接收
     */
    public static void receive(Logger log, String eventChannelName, CloudEventHandler eventHandler, String topic, MqttMessage message) throws Exception {
        try {
            Event event = new Event(topic, new String(message.getPayload()))
                    .qos(message.getQos())
                    .retained(message.isRetained())
                    .channel(eventChannelName);

            if (eventHandler != null) {
                if (eventHandler.handle(event) == false) {
                    //未成功，则异常（模拟 ack）
                    throw new CloudEventException("This event handling returns false: " + event.topic());
                }
            } else {
                //只需要记录一下
                log.warn("There is no observer for this event topic[{}]", event.topic());
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);

            log.warn(e.getMessage(), e); //todo: ?

            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new CloudEventException(e);
            }
        }
    }

    /**
     * 订阅
     */
    public static void subscribe(IMqttAsyncClient client, MqttConnectionOptions options, String eventChannelName, CloudEventObserverManger observerManger) throws MqttException {
        String[] topicAry = observerManger.topicAll().toArray(new String[0]);
        //int[] topicQos = new int[topicAry.length];

        MqttSubscription[] subscriptions = new MqttSubscription[topicAry.length];
        IMqttMessageListener[] topicListener = new IMqttMessageListener[topicAry.length];
        for (int i = 0, len = topicAry.length; i < len; i++) {
            EventObserver eventObserver = observerManger.getByTopic(topicAry[i]);
            subscriptions[i] = new MqttSubscription(topicAry[i], eventObserver.getQos());
            topicListener[i] = new MqttMessageListenerImpl(eventChannelName, eventObserver);
        }

        IMqttToken token = client.subscribe(subscriptions, null, null, topicListener, new MqttProperties());
        //转为毫秒
        long waitConnectionTimeout = options.getConnectionTimeout() * 1000;
        token.waitForCompletion(waitConnectionTimeout);
    }
}
