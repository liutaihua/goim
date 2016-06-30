/*
 *  @version : 1.0
 *  @author : liutaihua
 *  @E-mail：defage@gmail.com
 *  @date : 2016/06/20
*/

#include "data_def.h"
#include "barrage_client.h"
#include <string.h>
#include <iostream>
#include <stdlib.h>
#include <sys/time.h>
#include <sys/times.h>
#include <unistd.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>

using namespace std;

#define MAX_DATA_SIZE 40960   //maximum length(bytes) of each reception

barrage_client::~barrage_client()
{
}

barrage_client::barrage_client(int uid, int rid):
    user_id(uid),
    room_id(rid)
{
}

int barrage_client::connect_server(const char *host_p, int port)
{
	struct hostent *host;
	host = gethostbyname(host_p);
	if(host == NULL)
	{
		cout << "gethostbyname err, host is" << host_p << endl;
		return -1;
	}

	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
    if(sock_fd == -1)
    {
        cout << "create socket failed!" << endl;
        return -1;
    }

	struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
    //bzero(&(serv_addr.sin_zero), 8);
	memset(serv_addr.sin_zero, 0, 8);

    int con_ret = connect(sock_fd, (struct sockaddr *)&serv_addr, sizeof(struct sockaddr));
    if(con_ret == -1)
    {
        cout << "connect server " << host_p << ":" << port << " err!" << endl;
        return -1;
    }
    else
    {
        cout << "connect server " << host_p << ":" << port << " ok" << endl;
    }
    return 0;
}

int barrage_client::login_room()
{
    int ret = 0;
    login_req req;
    string data = req.transform_to_string(user_id, room_id);

    int bodyLen = data.size();

    loginPacket login_packet;
    login_packet.packLen = htonl(16+bodyLen);
    login_packet.headLen = htons(16);
    login_packet.ver = htons(1);
    login_packet.op = htonl(7);
    login_packet.seq = htonl(1);

    ret = send(sock_fd, &login_packet, sizeof(loginPacket), 0);
    if(ret == -1)
    {
        cout << "send login head: " << ret << endl;
    }

    cout << data << endl;
    ret = send(sock_fd, data.data(), data.size(), 0);
    if(ret == -1)
    {
        cout << "send login request err: " << ret << endl;
    }
    else
    {
        cout << "send login request ok" << endl;
    }

    return 0;
}

void barrage_client::get_server_msg()
{

    // get head packet, total size: 4+2+2+4+4 = 16 byte
    NetHeadPacket  head_packet;
    char buff[sizeof(NetHeadPacket)];
    int recv_bytes = recv(sock_fd, &buff, sizeof(NetHeadPacket), 0);
    if (recv_bytes == -1)
    {
        cout << "receive server message err!" << endl;
        return;
    }
    memcpy(&head_packet, buff, sizeof(NetHeadPacket));
    int32 packet_length = ntohl(head_packet.nt_packLen);
    int16 head_length = ntohs(head_packet.nt_headLen);
    int16 ver = ntohs(head_packet.nt_ver);
    int32 op = ntohl(head_packet.nt_op);
    int32 seq_id = ntohl(head_packet.nt_seq);

    int32 body_length = packet_length - head_length;

//    cout << "packet_length: " << packet_length << endl;
//    cout << "head_length: " << head_length << endl;
//    cout << "ver: " << ver << endl;
//    cout << "op: " << op << endl;
//    cout << "seq_id: " << seq_id << endl;

    if (body_length == 0)
    {
        if (op == MSG_TYPE_LOGIN_RESPONSE)
        {
            cout << "auth success" << endl;
        }
        if (op == MSG_TYPE_HEARTBEAT_REPLY)
        {
            cout << "recv heartbeat" << endl;
        }
        return;
    }
//    cout << "body_length:" << body_length << endl;
    char body[body_length];
    recv_bytes = recv(sock_fd, reinterpret_cast<char *>(&body), body_length, 0);
    if (recv_bytes == -1)
    {
        cout << "receive server message err!" << endl;
        return;
    }
    string msg(body);
    cout << "弹幕消息:" << msg << endl;
}

void barrage_client::keep_alive()
{

    NetHBPacket hb_packet;

    hb_packet.packLen = htonl(16+0);
    hb_packet.headLen = htons(16);
    hb_packet.ver = htons(1);
    hb_packet.op = htonl(2);
    hb_packet.seq = htonl(1);

    int con_ret = send(sock_fd, &hb_packet, sizeof(NetHBPacket), 0);
    if(con_ret == -1)
    {
        cout << "send heartbeat failed "<< endl;
    }
    else
    {
        cout << "send heartbeat success "<< endl;
    }
}

void barrage_client::on_barrage(string data)
{
}
