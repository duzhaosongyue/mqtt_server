package com.example.mqtt_server;

import com.example.mqtt_server.mqtt.MqttServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MqttServerApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {
        var data = """
                {"projectCode":"402881e78432bf2401843768906f6445","corpType":"009","corpName":"安徽中厦建筑安装有限公司","corpCode":"913412007389129030","p_id":309}
                """;

        for (int i = 0; i < 800; i++) {
            MqttServer.sendCommand("mqtt/hq/123141/Rec",i+"_"+data);
        }
        System.out.println("发送完成");
        Thread.sleep(10000000L);
    }

}
