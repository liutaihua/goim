/*
 *  @version : 1.0
 *  @author : liutaihua
 *  @E-mail：defage@gmail.com
 *  @date : 2016/06/20
*/

#ifndef __barrage_CLIENT_H__
#define __barrage_CLIENT_H__

#include <stdint.h>
#include <string>
#include "data_def.h"

using namespace std;

class barrage_client
{
public:
    int sock_fd;        //socket file descriptor

public:
    barrage_client(int uid, int rid);
    ~barrage_client();

    //connect to douyu barrage server
    int connect_server(const char *host, int port);
    int login_room();

    void get_server_msg();
    void keep_alive();

protected:
    int user_id;
    int room_id;
    //do something when receive a barrage
    void on_barrage(string data);
    //do something when receive a login response
    void on_login_response(string data);
};

#endif      //__barrage_CLIENT_H__
