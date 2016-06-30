package com.plu.bulletscreen.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

/**
 * @summary: 弹幕客户端类
 * @author: liutaihua(defage@gmail.com)
 * @date:   2016-6-23
 * @version V1.0
 */
public class BulletScreenClient
{
	Logger logger = Logger.getLogger(BulletScreenClient.class);
	private static BulletScreenClient instance;

	//弹幕协议服务器地址
	private  String hostName = "115.231.96.138";
	//弹幕协议服务器端口
	private int port = 6666;

	private int roomId = 14168;
	private int userId = 0;

	//设置字节获取buffer的最大值
    private static final int MAX_BUFFER_LENGTH = 4096;

    //socket相关配置
    private Socket sock;
    private BufferedOutputStream bos;
    private BufferedInputStream bis;

    //获取弹幕线程及心跳线程运行和停止标记
    private boolean readyFlag = false;

    private BulletScreenClient(){}

	public enum MSGTYPE {
		MSG_TYPE_HEARTBEAT_REPLY(3),
		MSG_TYPE_BARRAGE(5),
		MSG_TYPE_LOGIN_RESPONSE(8);
		private final int value;

		// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
		MSGTYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public void setConnectionArgs(String host, int port, int roomId, int userId)
	{
		this.hostName = host;
		this.port = port;
		this.roomId = roomId;
		this.userId = userId;
	}

	/**
     * 单例
     */
    public static BulletScreenClient getInstance(){
    	if(null == instance){
    		instance = new BulletScreenClient();
    	}
    	return instance;
    }

    /**
     * 客户端初始化，连接弹幕服务器并登陆
     */
    public void init(){
    	//连接弹幕服务器
    	this.connectServer();
    	//登陆指定房间
    	this.loginRoom();
    }

    /**
     * 获取弹幕客户端就绪标记
     * @return
     */
    public boolean getReadyFlag(){
    	return readyFlag;
    }

    /**
     * 连接弹幕服务器
     */
    private void connectServer()
    {
        try
        {
        	//获取弹幕服务器访问host
        	String host = InetAddress.getByName(hostName).getHostAddress();
            //建立socke连接
			System.out.println(host);
			System.out.println(port);

			sock = new Socket(host, port);
            //设置socket输入及输出
            bos = new BufferedOutputStream(sock.getOutputStream());
            bis= new BufferedInputStream(sock.getInputStream());
        }
        catch(Exception e)
        {
			e.printStackTrace();
			return;
		}
		logger.debug("Server Connect Successfully!");
    }


	public static int htonl(int value) {
		if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) )
		{
			return value;
		}
		return Integer.reverseBytes(value);
	}

	public static byte[] getLoginRequestData(int roomId, int userId){
        String s = "{\"rid\":"+ roomId+",\"uid\":" + userId + ",\"token\":\"\"}";
		return s.getBytes();
	}


	/**
     * 登录
     */
    private void loginRoom()
    {
    	//获取弹幕服务器登陆请求数据包
    	byte[] loginRequestData = getLoginRequestData(roomId, userId);

		int packLen = 16 + loginRequestData.length;
		short headLen = 16;
		short ver = 1;
		int op = 7;
		int seq = 1;

		ByteBuffer packByte = ByteBuffer.wrap(new byte[4]);
		packByte.asIntBuffer().put(packLen);
		packByte.order(ByteOrder.BIG_ENDIAN);

		ByteBuffer headByte = ByteBuffer.wrap(new byte[2]);
		headByte.asShortBuffer().put(headLen);
		headByte.order(ByteOrder.BIG_ENDIAN);


		ByteBuffer verByte = ByteBuffer.wrap(new byte[2]);
		verByte.asShortBuffer().put(ver);
		verByte.order(ByteOrder.BIG_ENDIAN);

		ByteBuffer opByte = ByteBuffer.wrap(new byte[4]);
		opByte.asIntBuffer().put(op);
		opByte.order(ByteOrder.BIG_ENDIAN);

		ByteBuffer seqByte = ByteBuffer.wrap(new byte[4]);
		seqByte.asIntBuffer().put(seq);
		seqByte.order(ByteOrder.BIG_ENDIAN);



		try {
			bos.write(packByte.array(), 0, 4);
			bos.write(headByte.array(), 0, 2);

			bos.write(verByte.array(), 0, 2);
			bos.write(opByte.array(), 0, 4);
			bos.write(seqByte.array(), 0, 4);
			bos.write(loginRequestData, 0, loginRequestData.length);
			bos.flush();

		} catch(Exception e) {
			e.printStackTrace();
		}
		this.readyFlag = true;
	}


    /**
     * 服务器心跳连接
     */
    public void keepAlive()
    {
		int packLen = 16;
		short headLen = 16;
		short ver = 1;
		int op = 2;
		int seq = 1;

		ByteBuffer packByte = ByteBuffer.wrap(new byte[4]);
		packByte.asIntBuffer().put(packLen);
		packByte.order(ByteOrder.BIG_ENDIAN);

		ByteBuffer headByte = ByteBuffer.wrap(new byte[2]);
		headByte.asShortBuffer().put(headLen);
		headByte.order(ByteOrder.BIG_ENDIAN);


		ByteBuffer verByte = ByteBuffer.wrap(new byte[2]);
		verByte.asShortBuffer().put(ver);
		verByte.order(ByteOrder.BIG_ENDIAN);

		ByteBuffer opByte = ByteBuffer.wrap(new byte[4]);
		opByte.asIntBuffer().put(op);
		opByte.order(ByteOrder.BIG_ENDIAN);

		ByteBuffer seqByte = ByteBuffer.wrap(new byte[4]);
		seqByte.asIntBuffer().put(seq);
		seqByte.order(ByteOrder.BIG_ENDIAN);



		try {
			bos.write(packByte.array(), 0, 4);
			bos.write(headByte.array(), 0, 2);

			bos.write(verByte.array(), 0, 2);
			bos.write(opByte.array(), 0, 4);
			bos.write(seqByte.array(), 0, 4);
			bos.flush();

		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("send heartbeat success");
	}

	public static int toInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;

		for (int i = 0; i < bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}
	public static short toShort(byte[] bRefArr) {
		short iOutcome = 0;
		byte bLoop;

		for (int i = 0; i < bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	public static int ntohs(short value){
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putShort(value);
		buffer.flip();
		return toInt(buffer.array());
	}
	public static int ntohl(int value){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(value);
		buffer.flip();
		return toInt(buffer.array());
	}

    /**
     * 获取服务器返回信息
     */
    public void getServerMsg(){
    	//初始化获取弹幕服务器返回信息包大小
    	byte[] recvByte = new byte[MAX_BUFFER_LENGTH];
    	//定义服务器返回信息的字符串
    	String dataStr;
		try {
			ByteBuffer packByte = ByteBuffer.wrap(new byte[4]);

			byte[] packLen = new byte[4];
			byte[] headLen = new byte[2];
			byte[] ver = new byte[2];
			byte[] op = new byte[4];
			byte[] seq = new byte[4];

			int ret = bis.read(packLen, 0, 4);
			if (ret == -1) {
				logger.error("recv error");
				return;
			}

			ret = bis.read(headLen, 0, 2);
			if (ret == -1) {
				logger.error("recv error");
				return;
			}

			ret = bis.read(ver, 0, 2);
			if (ret == -1) {
				logger.error("recv error");
				return;
			}

			ret = bis.read(op, 0, 4);
			if (ret == -1) {
				logger.error("recv error");
				return;
			}

			ret = bis.read(seq, 0, 4);
			if (ret == -1) {
				logger.error("recv error");
				return;
			}

//			logger.debug(String.format("\npackSize: %d,\n" +
//					"head_len:%d,\n" +
//					"ver: %d,\n" +
//					"op: %d,\n" +
//					"seqId:%d", ntohl(toInt(packLen)), ntohs(toShort(headLen)), ntohs(toShort(ver)), ntohl(toInt(op)), ntohl(toInt(seq))));

			// 需要做一次网络字节流到本地字节流的转换
			int bodyLen = ntohl(toInt(packLen)) - ntohs(toShort(headLen));

			int op_int = ntohl(toInt(op));
			MSGTYPE t = MSGTYPE.valueOf("MSG_TYPE_HEARTBEAT_REPLY");
			switch (t) {
				case MSG_TYPE_HEARTBEAT_REPLY:
					System.out.println("recv heartbeat");
					break;
				case MSG_TYPE_LOGIN_RESPONSE:
					System.out.println("auth success");
					break;
			}


			if (bodyLen == 0) {
				return;
			}

			byte[] body = new byte[bodyLen];

			ret = bis.read(body, 0, bodyLen);
			if (ret == -1) {
				logger.error("recv error");
				return;
			}

			String msg = new String(body);
			System.out.println("弹幕消息:" + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
