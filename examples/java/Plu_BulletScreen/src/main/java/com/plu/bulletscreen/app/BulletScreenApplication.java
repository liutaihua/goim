package com.plu.bulletscreen.app;

import com.plu.bulletscreen.client.BulletScreenClient;
import com.plu.bulletscreen.utils.KeepAlive;
import com.plu.bulletscreen.utils.KeepGetMsg;

/**
 * @summary: 弹幕程序入口main
 * @author: liutaihua(defage@gmail.com)
 * @date:   2016-6-23
 * @version V1.0
 */
public class BulletScreenApplication
{
	public static void main(String[] args)
	{
        int roomId, userId, port;
        String host;
        try {
            roomId = Integer.parseInt(args[0]);
            userId = Integer.parseInt(args[1]);
            host = args[2];
            port = Integer.parseInt(args[3]);
        }
        catch(java.lang.Exception e)
        {
            System.out.println("Arguments: roomId userId host port");
            return;
        }
		//初始化弹幕Client
        BulletScreenClient danmuClient = BulletScreenClient.getInstance();
        //设置需要连接和访问的房间ID，以及弹幕池分组号
        danmuClient.setConnectionArgs(host, port, roomId, userId);
        danmuClient.init();
        
        //保持弹幕服务器心跳
        KeepAlive keepAlive = new KeepAlive();
        keepAlive.start();
        
        //获取弹幕服务器发送的所有信息
        KeepGetMsg keepGetMsg = new KeepGetMsg();
        keepGetMsg.start();
	}
}