import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FastFdsTest {


    @Test
    public void testConfig() throws IOException, MyException {
//        ClientGlobal.init("fdfs_client.conf");
        ClientGlobal.initByProperties("fastdfs-client.properties");
        System.out.println(ClientGlobal.configInfo());
    }

    public static void main(String[] args) throws Exception {
        //???????????????
        String configFileName = "fdfs_client.conf";
        try {
            ClientGlobal.init(configFileName);
            System.out.println(ClientGlobal.configInfo());
        }catch(Exception e){
            e.printStackTrace();
        }
        File file = new File("/Users/songyangyang/Desktop/111.png");
        //???????·??:group1 M00/00/00/wKhuW1Vmj6KAZ09pAAC9przUxEk788.jpg
        String[] files =  uploadFile(file, "111.png", file.length());
        System.out.println(Arrays.asList(files));
    }
    /**
     * ??????
     */
    public static String[] uploadFile(File file, String uploadFileName, long fileLength) throws IOException {
        byte[] fileBuff = getFileBuffer(new FileInputStream(file), fileLength);
        String[] files = null;
        String fileExtName = "";
        if (uploadFileName.contains(".")) {
            fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
        } else {
            System.out.println("Fail to upload file, because the format of filename is illegal.");
            return null;
        }

        // ????????
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient client = new StorageClient(trackerServer, storageServer);

        // ????????
        NameValuePair[] metaList = new NameValuePair[3];
        metaList[0] = new NameValuePair("fileName", uploadFileName);
        metaList[1] = new NameValuePair("fileExtName", fileExtName);
        metaList[2] = new NameValuePair("fileLength", String.valueOf(fileLength));

        // ??????
        try {
            files = client.upload_file(fileBuff, fileExtName, metaList);
        } catch (Exception e) {
            System.out.println("Upload file \"" + uploadFileName + "\"fails");
        }
        trackerServer.close();
        return files;
    }
    private static byte[] getFileBuffer(InputStream inStream, long fileLength) throws IOException {

        byte[] buffer = new byte[256 * 1024];
        byte[] fileBuffer = new byte[(int) fileLength];

        int count = 0;
        int length = 0;

        while ((length = inStream.read(buffer)) != -1) {
            for (int i = 0; i < length; ++i) {
                fileBuffer[count + i] = buffer[i];
            }
            count += length;
        }
        return fileBuffer;
    }

}
