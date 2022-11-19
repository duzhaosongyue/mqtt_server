package com.example.mqtt_server.listener;

import com.example.mqtt_server.mqtt.MqttConfig;
import com.example.mqtt_server.mqtt.MqttServer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/****
 * mqtt 队列监听程序
 * @author fuping
 */
@Component
@Slf4j
@AllArgsConstructor
public class Listener implements ApplicationListener<ContextRefreshedEvent>, Runnable {

    private MqttConfig mqttConfig;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(Listener.class);
        log.info("启动MQTT监听服务");
        new Thread(this).start();
    }

    @Override
    public void run() {
        final CallbackConnection connection = MqttServer.getInstance().getConnection();
        //添加监听时间
        connection.listener(new org.fusesource.mqtt.client.Listener() {
            @Override
            public void onConnected() {
                log.info("{}","连接MQTT服务器成功！");
            }

            @Override
            public void onDisconnected() {
                log.info("{}","断开MQTT服务器连接！");
            }

            @SneakyThrows
            @Override
            public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ask) {
                ask.run();
                String msg = new String(payload.toByteArray(), StandardCharsets.UTF_8);
                log.info("监听到的消息:{}", msg);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        connection.connect(new Callback<>() {
            @Override
            public void onSuccess(Void aVoid) {
                connection.subscribe(new Topic[]{
                        //可以同时订阅多个话题
                        new Topic(mqttConfig.getRecTopic(), QoS.AT_MOST_ONCE),
                }, new Callback<>() {
                    @Override
                    public void onSuccess(byte[] qoses) {
                        log.info("订阅主题成功!");
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        log.info("订阅主题失败！{}", value.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Throwable value) {
                log.info("连接MQTT服务失败:{}", value.getMessage());
            }
        });

    }

}