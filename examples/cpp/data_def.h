/*
 *  @version : 1.0
 *  @author : liutaihua
 *  @E-mail：defage@gmail.com
 *  @date : 2016/06/20
*/

#ifndef __DATA_DEF_H__
#define __DATA_DEF_H__

#include <string>

using namespace std;


typedef unsigned char		byte;
typedef unsigned char		byte;
typedef unsigned char		uint8;
typedef signed short			int16;
typedef unsigned short		uint16;
typedef signed int			int32;
typedef unsigned int			uint32;
typedef signed long long		int64;
typedef unsigned long long	uint64;

enum MESSAGE_TYPE
{
    MSG_TYPE_HEARTBEAT = 2,
    MSG_TYPE_HEARTBEAT_REPLY = 3,
    MSG_TYPE_BARRAGE = 5,       //barrage
    MSG_TYPE_LOGIN_RESPONSE = 8         //login response
};

struct login_req
{
    std::string transform_to_string(int user_id, int room_id);
};

typedef struct gPacket
{
    uint32 nt_packLen;
    uint16 nt_headLen;
    uint16 nt_ver;
    uint32 nt_op;
    uint32 nt_seq;

}NetHeadPacket;

struct loginPacket
{
    uint32 packLen;
    uint16 headLen;
    uint16 ver;
    uint32 op;
    uint32 seq;
};

typedef struct gheartbeat
{
    uint32 packLen;
    uint16 headLen;
    uint16 ver;
    uint32 op;
    uint32 seq;
} NetHBPacket;

#endif  //__DATA_DEF_H__
