package com.example.mqtt_server.mqtt;

import com.example.mqtt_server.util.SpringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

/**
 * mqtt服务
 *
 * @author fuping
 */
@Slf4j
public class MqttServer {

    /****
     * 初始化连接选项
     * @return
     */
    @SneakyThrows
    private static MQTT initMQTT() {
        MqttConfig mqttConfig = SpringUtil.getBean(MqttConfig.class);
        MQTT mqtt = new MQTT();
        mqtt.setHost(mqttConfig.getBroker());
        mqtt.setUserName(mqttConfig.getUsername());
        mqtt.setPassword(mqttConfig.getPassword());
        mqtt.setClientId(mqttConfig.getClientId());
        //连接前清空会话信息
        mqtt.setCleanSession(Boolean.TRUE);
        //设置心跳时间
        mqtt.setKeepAlive(mqttConfig.getKeepAlive());
        return mqtt;
    }


    private static MqttServer mqttServer;

    private CallbackConnection connection;

    private MqttServer(CallbackConnection connection) {
        this.connection = connection;
    }

    public static MqttServer getInstance() {
        if (mqttServer == null) {
            synchronized (MqttServer.class) {
                if (mqttServer == null) {
                    mqttServer = new MqttServer(initMQTT().callbackConnection());
                }
            }
        }
        return mqttServer;
    }


    public CallbackConnection getConnection() {
        return connection;
    }


    /****
     * 消息推送
     * @param connection  连接对象
     * @param topic       MQTT topic
     * @param msg         Message
     * @param qos         消息级别
     */
    private static void publish(final CallbackConnection connection, String topic, String msg, QoS qos) {
        connection.publish(topic, msg.getBytes(), qos, false, new Callback<Void>() {
            @Override
            public void onSuccess(Void v) {
                log.info("推送数据:{}, 话题：{}", msg, topic);
            }

            @Override
            public void onFailure(Throwable value) {
                value.printStackTrace();
            }
        });
    }

    /****
     * 发送数据到mqtt消息队列
     * @param   topic
     * @param    msg
     */
    public static void sendCommand(String topic, String msg) {
        final CallbackConnection connection = MqttServer.getInstance().getConnection();
        connection.getDispatchQueue().execute(() -> publish(connection, topic, msg, QoS.AT_LEAST_ONCE));
    }
}
