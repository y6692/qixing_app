package cn.qimate.bike.util;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;

import java.io.File;

/**
 * 初始化七牛文件上传管理器
 */
public class QiNiuInitialize {

    private static UploadManager instance;

    //私有构造
    private QiNiuInitialize() {
    }

    //获取UploadManager
    public static UploadManager getSingleton() {
        if (instance == null) {
            synchronized (QiNiuInitialize.class) {
                if (instance == null) {

                    Configuration config = new Configuration.Builder()
                            .connectTimeout(10)           // 链接超时。默认10秒
                            .useHttps(true)               // 是否使用https上传域名
                            .responseTimeout(60)          // 服务器响应超时。默认60秒
//                            .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
//                            .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
//                            .dns(null)
//                            .zone(FixedZone.zone0)        // 设置区域，不指定会自动选择。指定不同区域的上传域名、备用域名、备用IP。
                            .build();

//                    instance = new UploadManager(config);
                    instance = new UploadManager(config, 3);

//                    instance = new UploadManager(new Configuration.Builder().zone()
//                            .zone(Zone.zone0) // 设置区域为华北
//                            .build());
                }
            }
        }
        return instance;
    }


//    String dirPath = <断点记录文件保存的文件夹位置>
//            Recorder recorder = null;
//try {
//        recorder = new FileRecorder(dirPath);
//    }catch (Exception e){
//    }
    //默认使用key的url_safe_base64编码字符串作为断点记录文件的文件名
//避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
    KeyGenerator keyGen = new KeyGenerator(){
        public String gen(String key, File file){
            // 不必使用url_safe_base64转换，uploadManager内部会处理
            // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
            return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
        }
    };
}
