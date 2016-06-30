package com.plu.bulletscreen.utils;

import com.plu.bulletscreen.client.BulletScreenClient;

/**
 * @summary: 用来接收弹幕的线程
 * @author: liutaihua(defage@gmail.com)
 * @date:   2016-6-23
 * @version V1.0
 */
public class KeepGetMsg extends Thread {

	@Override
    public void run()
    {
		////获取弹幕客户端
    	BulletScreenClient danmuClient = BulletScreenClient.getInstance();
    	
    	//判断客户端就绪状态
        while(danmuClient.getReadyFlag())
        {
        	//获取服务器发送的弹幕信息
        	danmuClient.getServerMsg();
        }
    }
}
