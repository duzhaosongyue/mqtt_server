package com.example.mqtt_server.mqtt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * mqtt配置
 * @author fuping
 */
@Component
@ConfigurationProperties(prefix = "mqtt")
@Getter
@Setter
@ToString
public class MqttConfig {

    /****
     * 订阅的主题
     */
    private String recTopic;

    /****
     * 心跳间隔
     */
    private short keepAlive;

    /****
     * 消息队列服务器地址和端口
     */
    private String broker;

    /****
     * 客户端编号
     */
    private String clientId;

    /****
     * 账号
     */
    private String username;

    /****
     * 密码
     */
    private String password;

}
