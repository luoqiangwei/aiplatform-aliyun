package cn.oveay.aiplatform.utils.vcode.servlet;


import cn.oveay.aiplatform.utils.vcode.utils.VerifyCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;


@SuppressWarnings("serial")
@Controller
@RequestMapping("/verify")
public class VerifyCodeServlet {
	@RequestMapping("/createVerify")
	public void createVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		VerifyCode vc = new VerifyCode();
		BufferedImage image = vc.getImage();//获取一次性验证码图片
		// 该方法必须在getImage()方法之后来调用
		//System.err.println(vc.getText());//获取图片上的文本
		VerifyCode.output(image, response.getOutputStream());//把图片写到指定流中
		
		// 把文本保存到session中，为LoginServlet验证做准备
		request.getSession().setAttribute("vCode", vc.getText());
	}
}
