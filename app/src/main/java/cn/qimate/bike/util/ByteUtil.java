package cn.qimate.bike.util;

import android.util.Log;

import java.util.zip.CRC32;

public class ByteUtil {

	/** 
	 * Byte转Bit 
	 */  
	public static String byteToBit(byte b) {  
	    return "" +(byte)((b >> 7) & 0x1) +   
	    (byte)((b >> 6) & 0x1) +   
	    (byte)((b >> 5) & 0x1) +   
	    (byte)((b >> 4) & 0x1) +   
	    (byte)((b >> 3) & 0x1) +   
	    (byte)((b >> 2) & 0x1) +   
	    (byte)((b >> 1) & 0x1) +   
	    (byte)((b >> 0) & 0x1);  
	}  
	  
	/** 
	 * Bit转Byte 
	 */  
	public static byte BitToByte(String byteStr) {  
	    int re, len;  
	    if (null == byteStr) {  
	        return 0;  
	    }  
	    len = byteStr.length();  
	    if (len != 4 && len != 8) {  
	        return 0;  
	    }  
	    if (len == 8) {// 8 bit处理  
	        if (byteStr.charAt(0) == '0') {// 正数  
	            re = Integer.parseInt(byteStr, 2);  
	        } else {// 负数  
	            re = Integer.parseInt(byteStr, 2) - 256;  
	        }  
	    } else {//4 bit处理  
	        re = Integer.parseInt(byteStr, 2);  
	    }  
	    return (byte) re;  
	}  
	
	
	public static int byteArrayToInt(byte[] b) {  
	    return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;  
	}  
	  
	public static byte[] intToByteArray(int a) {  
	    return new byte[] {  
	        (byte) ((a >> 24) & 0xFF),  
	        (byte) ((a >> 16) & 0xFF),     
	        (byte) ((a >> 8) & 0xFF),     
	        (byte) (a & 0xFF)  
	    };  
	}  
	
	/** 
	 * 校验和 
	 *  
	 * @param msg 需要计算校验和的byte数组 
	 * @param length 校验和位数 
	 * @return 计算出的校验和数组 
	*/  
	public static byte[] SumCheck(byte[] msg, int length) {  
	    long mSum = 0;  
	    byte[] mByte = new byte[length];  
	          
	    /** 逐Byte添加位数和 */  
	    for (byte byteMsg : msg) {  
	        long mNum = ((long)byteMsg >= 0) ? (long)byteMsg : ((long)byteMsg + 256);  
	        mSum += mNum;  
	    } /** end of for (byte byteMsg : msg) */  
	          
	    /** 位数和转化为Byte数组 */  
	    for (int liv_Count = 0; liv_Count < length; liv_Count++) {  
	        mByte[length - liv_Count - 1] = (byte)(mSum >> (liv_Count * 8) & 0xff);  
	    } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */  
	          
	    return mByte;  
	} 
	
	
	public static long SumCheck(byte[] msg) {  
	    long mSum = 0;  
	    /** 逐Byte添加位数和 */  
	    for (byte byteMsg : msg) {  
	        long mNum = ((long)byteMsg >= 0) ? (long)byteMsg : ((long)byteMsg + 256);  
	        mSum += mNum;  
	    } /** end of for (byte byteMsg : msg) */  
	          
	    return mSum;  
	} 
	
	/**
	 * 异或校验
	 * @param bb
	 * @return
	 */
	public static byte checkyh(byte[] bb){
		byte check = (byte)0;
		for (int i = 0; i < bb.length; i++) {
			check  = (byte) (check ^ bb[i]);
		}
		return (byte) (check & 0xFF);
	}
	
	/**
	 * crc32 
	 * @param bb
	 * @return
	 */
	public static long crc32(byte[] bb){
		CRC32 crc32 = new CRC32();
		crc32.update(bb);
		return crc32.getValue();
	}
	
	public static String toByteArray(byte[] bb){
		StringBuilder builder = new StringBuilder(bb.length);
		for (byte byteChar : bb){
			builder.append(String.format("%02X ", byteChar));
		}
		return builder.toString();
	}

	public static void log(String msg){
		Log.d("xytlog-->>>", msg);
	}


	public static void main(String[] args) {
		
		//0E FF    --02(设备类型) 91(ID之和除2) D7 EC F0 70 (ID4byte)  0B (ID之和除3)
		//20 91 48 4E EE B6 
		System.out.println(byteToBit((byte)'A'));
		System.out.println((int)'A');
		System.out.println(BitToByte("01000001"));
		long a = SumCheck(new byte[]{(byte) 0xD7, (byte) 0xEC, (byte) 0xF0, (byte) 0x70});
		
		 // D7 EC F0 70 和 
		System.out.println("=="+Integer.toHexString((int)a));
		
		System.out.println(Integer.toHexString((int)a/3) );
		System.out.println(Integer.toHexString((int)a/2) );
		byte b  = (byte)(a/2);
		System.out.println(b&0xff);
		System.out.println(0x91&0xff);
		
		byte bb[] = new byte[3];
		System.out.println(bb.length);


	}
}
