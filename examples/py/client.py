#!/usr/bin/env python
# encoding: utf-8
# author liutaihua(defage@gmail.com)
# a example python impl for im proto


import json
import gevent
from gevent import monkey
monkey.patch_all

import socket
from struct import *

from PyMsg import MsgLoginReq, MsgHeartbeat
from PyMsg import MSG_Heartbeat, MSG_LoginReqReply, MSG_HeartbeatReply, MSG_OP_SEND_SMS_REPLY

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setblocking(1)
sock.settimeout(1)
sock.connect(('127.0.0.1', 8888))


hb = MsgHeartbeat()
hb_message = hb.pack()

userId = 323232
roomId = 14168

def send_heartbeat():
    ############# send heartbeat pack #################
    global sock
    print 'start hb'
    login = MsgLoginReq()
    login.body = json.dumps({"uid": userId, "rid": roomId, "token": ""})
    login_message = login.pack()
    sock.sendall(login_message)
    print 'send login req done'
    gevent.sleep(1)
    while True:
        try:
            sock.sendall(hb_message)
        except:
            print 'send heartbeat error'
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(('127.0.0.1', 8888))
        print 'send heartbeat'
        gevent.sleep(8)


def recv_message():
    while True:
        # first: recv head and pack lean
        try:
            head = sock.recv(4)
        except Exception, e:
            # just continue to next read
            gevent.sleep(1)
            continue
        if len(head) != 4:
            print 'err head len'
            continue

        # second: recv proto field
        try:
            remain = sock.recv(12)
        except:
            print 'socket close2'
            break
        if len(remain) != 12:
            print 'err remain len'
            continue

        headLen, ver, op, seq = unpack('!hhii', remain)
        packLen, = unpack('!i', head)
        bodyLen = packLen - headLen
        # print 'headLen, ver, op, seq', headLen, ver, op, seq
        # if bodyLen eq 0, is a heartbeat reply pack
        if op == MSG_HeartbeatReply:
            print 'got heartbeat reply'
        elif op == MSG_LoginReqReply:
            print 'got login reply'
        elif op == MSG_OP_SEND_SMS_REPLY:
            print 'got msg'
        else:
            print 'unknown op', op
        if bodyLen == 0:
            gevent.sleep(1)
            continue
        try:
            msg = sock.recv(bodyLen)
        except:
            print 'socket close3'
            continue
        print 'msg:', msg

if __name__ == "__main__":
    l = []
    l.append(gevent.spawn(send_heartbeat))
    l.append(gevent.spawn(recv_message))
    gevent.joinall(l)
