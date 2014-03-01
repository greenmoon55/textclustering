package fi.uef.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * Servlet implementation class for Servlet: UploadServlet
 * 
 */
public class CopyOfUploadServlet extends javax.servlet.http.HttpServlet implements
        javax.servlet.Servlet {
    File tmpDir = null;// 初始化上传文件的临时存放目录
    File saveDir = null;// 初始化上传文件后的保存目录

    public CopyOfUploadServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
			// 上传乱码问题
			response.setContentType("text/html;charset=gb2312");
			request.setCharacterEncoding("UTF-8");

			// 创建目录
			// 指定上传文件的临时目录, 在项目的upload_tmp_dir里,
			//C:\tomcat6\webapps/upload/upload_tmp_dir  最后不带\
			String realPath = request.getSession().getServletContext()
					.getRealPath(File.separator);
			String up_tempDir = realPath + "upload_tmp_dir";
			response.getWriter().println("upload_temp directory is: "+up_tempDir + "<br>");
			tmpDir = new File(up_tempDir);
			if (!tmpDir.isDirectory())
				tmpDir.mkdir();

			// 指定上传的目录,在项目的upload_tmp_dir里, C:\tomcat6\webapps/upload/upload_dir ,最后不带\
			String upload_Dir = realPath + "upload_dir";
			response.getWriter().println("upload directory is: "+upload_Dir + "<br>");
			saveDir = new File(upload_Dir);
			if (!saveDir.isDirectory())
				saveDir.mkdir();
			
            if (ServletFileUpload.isMultipartContent(request)) {
                DiskFileItemFactory dff = new DiskFileItemFactory();// 创建该对象
                dff.setRepository(tmpDir);// 指定上传文件的临时目录
                dff.setSizeThreshold(1024000);// 指定在内存中缓存数据大小,单位为byte
                ServletFileUpload sfu = new ServletFileUpload(dff);// 创建该对象
                sfu.setFileSizeMax(5000000);// 指定单个上传文件的最大尺寸
                sfu.setSizeMax(10000000);// 指定一次上传多个文件的总尺寸
                FileItemIterator fii = sfu.getItemIterator(request);// 解析request
                                                                    // 请求,并返回FileItemIterator集合
                while (fii.hasNext()) {
    				FileItemStream fis = fii.next();// 从集合中获得一个文件流
    				if (!fis.isFormField() && fis.getName().length() > 0) {// 过滤掉表单中非文件域
    				
    					String fileName=fis.getName().substring(
    							fis.getName().lastIndexOf(File.separator) + 1);
    					// D:\hao\桌面\contact_list.txt 完整的路径 fis.getName()
    					response.getWriter().println("full path of upload file:"+fis.getName() + "<br>");
    					//contact_list.txt  只有文件名
    					response.getWriter().println("upload file name is: "+fis.getName().substring(fis.getName().lastIndexOf(File.separator) + 1)+ "<br>");

    					BufferedInputStream in = new BufferedInputStream(fis
    							.openStream());// 获得文件输入流
    					BufferedOutputStream out = new BufferedOutputStream(
    							new FileOutputStream(new File(saveDir + File.separator
    									+ fileName)));// 获得文件输出流
    					Streams.copy(in, out, true);// 开始把文件写到你指定的上传文件夹
    				}
    			}

                response.getWriter().println("File upload successfully!!!");// 终于成功了,还不到你的上传文件中看看,你要的东西都到齐了吗
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}



