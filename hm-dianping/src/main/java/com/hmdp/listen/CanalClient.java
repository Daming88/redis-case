package com.hmdp.listen;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import java.net.InetSocketAddress;

public class CanalClient {

    public static void main(String[] args) throws InterruptedException {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.1.109", 11111), "example", "", "");

        connector.connect();
        while (true){
            connector.subscribe("hmdp.*");
            Message message = connector.get(100);
            if (message.getEntries().size() >0){
                System.out.println();
            }
        }
    }

}
