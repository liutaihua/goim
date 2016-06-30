#!/usr/bin/env python
# encoding: utf-8

import json
from MsgUtil import MsgReader, MsgWriter

MSG_Heartbeat = 2
MSG_HeartbeatReply = 3

MSG_OP_SEND_SMS_REPLY= 5

MSG_LoginReq = 7
MSG_LoginReqReply = 8

class MsgLoginReq:
    def __init__(self):
        self.ver = 1
        self.op = MSG_LoginReq
        self.seq = 1
        self.body = ''


    def pack(self):
        w = MsgWriter(MSG_LoginReq)
        w.writeShort(self.ver)
        w.writeInt(self.op)
        w.writeInt(self.seq)
        w.writeBody(self.body)
        return w.msg

class MsgHeartbeat:
    def __init__(self):
        self.ver = 1
        self.op = MSG_Heartbeat
        self.seq = 1
        #self.body = json.dumps({"uid": self.userId, "rid": self.roomId, "token": ""})
        self.body = ''


    def pack(self):
        w = MsgWriter(MSG_LoginReq)
        w.writeShort(self.ver)
        w.writeInt(self.op)
        w.writeInt(self.seq)
        w.writeBody(self.body)
        return w.msg
