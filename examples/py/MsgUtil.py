from struct import *

class MsgReader:
    def __init__(self, msg):
        self._msg = msg

    def readStr(self):
        len = self.readShort()
        s = self._msg[:len]
        self._msg = self._msg[len:]
        return s

    def readByte(self):
        b = self._msg[0]
        self._msg = self._msg[1:]
        return ord(b)

    def readShort(self):
        s, = unpack('!h', self._msg[:2])
        self._msg = self._msg[2:]
        return s

    def readInt(self):
        i, = unpack('!i', self._msg[:4])
        self._msg = self._msg[4:]
        return i

    def readUInt(self):
        i, = unpack('!I', self._msg[:4])
        self._msg = self._msg[4:]
        return i

    def readSingle(self):
        f, = unpack('!f', self._msg[:4])
        self._msg = self._msg[4:]
        return f

class MsgWriter:
    def __init__(self, msgId):
        self._msg = ''
        self.msgId = msgId
        self.bodyLen = 0
        self.body = ''

    def writeStr(self, s):
        self.writeShort(len(s))
        self._msg += s

    def writeBody(self, s):
        self.body = s
        self.bodyLen = len(s)
        self._msg += s

    def writeByte(self, b):
        self._msg += chr(b)

    def writeShort(self, s):
        self._msg += pack('!h', s)

    def writeInt(self, i):
        self._msg += pack('!i', i)

    def writeUInt(self, i):
        self._msg += pack('!I', i)

    def writeSingle(self, f):
        self._msg += pack('!f', f)

    @property
    def msg(self):
        msgLen = 16 + len(self.body)
        head = pack('!ih', msgLen, 16)
        return head + self._msg
