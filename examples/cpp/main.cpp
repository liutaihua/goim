/*
 *  @version : 1.0
 *  @author : liutaihua
 *  @E-mail：defage@gmail.com
 *  @date : 2016/06/20
*/

/*
协议包发送开始
协议传输拆成包头和数据部分， 其中包头包含包大小，以及包头的几个字段
写协议的流程：
1, 写包首部中包头的大小，这部分包含包头的16字节长度 + 数据体大小
2, 写包头中各字段的大小， headLen int16, ver int16, op int32, seq int32, 注意需要做htonl(本机字节顺序转化为网络字节顺序)
3, 写数据体
完成单个协议包的发送

收协议包的流程:(基本和发协议包顺序是一致的)
1, 获取4个字节的长度， ntohl(网络字节顺序转本地字节顺序), 得到总包头大小: packSize
2, 获取包头中各字段大小，依次获取 headLen(2字节), ver(2字节), op(4字节), seq(4字节), 分别对他们做ntohl才能得到正确的数值
3, 计算数据体长度 bodyLen = packSize - headLen
4, 根据bodyLen，接收bodyLen大小个字节流， 即为数据体
接收协议包完成

以上方式适用任意编程语言，按协议顺序和大小即可

优点：
流式的数据传输，非常有利于服务端做buffer，大大减少系统掉用开销
具有一定加密性， 可以阻挡部分用普通工具拉弹幕或发起连接的小白
非readline式的socket读，防止因弹幕中存在约定的行分隔符，造成readline失效
相对ws协议更轻，且不能在浏览器中直接通过F12查看
*/


#include "barrage_client.h"
#include <iostream>
#include <string>
#include <stdint.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>



using namespace std;

void usage()
{
	std::cout << "usage:" << std::endl;
    std::cout << "\t./get_barrage room_id user_id host port" << std::endl;
}

void* thr_keep_live(void *args)
{
    barrage_client *bclient = (barrage_client *)args;
	while(1)
	{
//        cout << "begin to keep live" << endl;
        bclient->keep_alive();
        usleep(1000000 * 3);   // sleep 3 seconds
    }
}

int main(int argc, char **args)
{
	int ret = 0;
	int room_id = 0;
        int user_id = 0;
//    std::string host_name = "172.16.9.4";
    string host_name;
    int32_t port;
    std::string host;

    if(argc < 5)
	{
		usage();
		return 0;
	}

	// parse parameters
    room_id = atoi(args[1]);
    user_id = atoi(args[2]);
    host_name = args[3];
    port = atoi(args[4]);

    cout<<"roomid:"<<std::to_string(room_id) << "  userid:"<< std::to_string(user_id) << endl;

    barrage_client bclient(user_id, room_id);

    ret = bclient.connect_server(host_name.c_str(), port);
	if(0 != ret)
	{
		std::cout << "please check the address and restart the program" << std::endl;
		return 0;
	}

    ret = bclient.login_room();
	if(0 != ret)
	{
		return 0;
	}

	// create a thread to keep alive
    pthread_t tid;
    ret = pthread_create(&tid, NULL, &thr_keep_live, &bclient);
    if(ret != 0)
    {
        cout << "Create pthread error!" << endl;
        return 0;
    }


	while(1)
	{
        bclient.get_server_msg();
	}

#ifdef _MSC_VER
#else
    pthread_join(tid, NULL);
#endif

	return 0;
}
