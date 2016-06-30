package com.plu.bulletscreen.utils;

import com.plu.bulletscreen.client.BulletScreenClient;

/**
 * @summary: 弹幕心跳
 * @author: liutaihua(defage@gmail.com)
 * @date:   2016-6-23
 * @version V1.0
 */
public class KeepAlive extends Thread {

    @Override
    public void run()
    {
    	//获取弹幕客户端
    	BulletScreenClient danmuClient = BulletScreenClient.getInstance();
    	
    	//判断客户端就绪状态
        while(danmuClient.getReadyFlag())
        {
        	//发送心跳保持协议给服务器端
        	danmuClient.keepAlive();
            try
            {
            	//设置间隔45秒再发送心跳协议
                Thread.sleep(3000);        //keep live at least 3 seconds
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
