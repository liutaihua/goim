/*
 *  @version : 1.0
 *  @author : liutaihua
 *  @E-mail：defage@gmail.com
 *  @date : 2016/06/20
*/

#include "data_def.h"
#include <time.h>
#include <string>
#include <iostream>
#include <sstream>

using namespace std;

static string pack_header(string data_str)
{
    string pack_str;

    int data_len = data_str.length() + 8;
    short msg_type = 689;     //client message type is 689
    char encrypt = 0;
    char reserve =0;

    pack_str.append((const char *)&data_len, sizeof(data_len));      // 4 bytes is len
    pack_str.append((const char *)&data_len, sizeof(data_len));      // 4 bytes is len
    pack_str.append((const char *)&msg_type, sizeof(msg_type));      // 2 bytes is message type
    pack_str.append((const char *)&encrypt, sizeof(encrypt));      // 1 bytes is encrypt
    pack_str.append((const char *)&reserve, sizeof(reserve));      // 1 bytes is reserve
    pack_str.append(data_str.data(), data_str.size());        //data

    return pack_str;
}


string login_req::transform_to_string(int user_id, int room_id)
{
    std::ostringstream ss;
    ss << "{\"rid\":" << std::to_string(room_id) << ",\"uid\":" << std::to_string(user_id) << ",\"token\":\"\"}";
    return ss.str();
}
