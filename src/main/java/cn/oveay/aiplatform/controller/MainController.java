package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.basebean.User;
import cn.oveay.aiplatform.service.LoginService;
import cn.oveay.aiplatform.service.RegisterService;
import cn.oveay.aiplatform.service.ResourceService;
import cn.oveay.aiplatform.utils.autoid.Nanoflake;
import cn.oveay.aiplatform.utils.sms.SMSUtil;
import cn.oveay.aiplatform.utils.token.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 13:04
 * 文件说明：主控制流
 */
@Slf4j
@Controller
public class MainController {
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @Value("${token-timeout}")
    private Integer tokenTimeOut;

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;

    @RequestMapping("/login")
    public String loginIndex(Model model, HttpServletRequest request) {
        String token = Nanoflake.getNanoflake();
        Token.set(request.getSession().getId(), token, tokenTimeOut);
        model.addAttribute("token", token);
        return "login";
    }

    @RequestMapping("/login/login")
    public String login(Model model, HttpServletRequest request, String token, String phone, String password, String vcode) {
        String vCode = (String) request.getSession().getAttribute("vCode");
        Integer tryTimes = (Integer) request.getSession().getAttribute("tryTimes");
        if (tryTimes == null) {
            tryTimes = 0;
            request.getSession().setAttribute("tryTimes", 0);
        } else {
            request.getSession().setAttribute("tryTimes", tryTimes + 1);
        }
        if (tryTimes >= 2) {
            request.getSession().setAttribute("loginError", "true");
        }
        if (tryTimes >= 3) {
            if (!vcode.toUpperCase().equals(vCode.toUpperCase())){
                model.addAttribute("vcode", "验证码错误");
                reSet(model, phone, password);
                return "forward:/login";
            }
        }
        if(phone.trim().equals("") || password.trim().equals("")){
            model.addAttribute("form", "所有信息必须都填写");
            reSet(model, phone, password);
            return "forward:/login";
        }
        if(phone.length() != 11){
            model.addAttribute("phone", "电话号码错误");
            reSet(model, phone, password);
            return "forward:/login";
        }
        if(password.length() < 8){
            model.addAttribute("password", "密码太短");
            reSet(model, phone, password);
            return  "forward:/login";
        }
        if(password.length() > 20){
            model.addAttribute("password", "密码太长");
            reSet(model, phone, password);
            return  "forward:/login";
        }

        if(!Token.check(request.getSession().getId(), token)){
            model.addAttribute("form", "您的签名不正确，表单失效");
            reSet(model, phone, password);
            return "forward:/login";
        }

        User user = loginService.login(phone, password);
        if (user == null) {
            model.addAttribute("form", "密码或电话号码错误");
            reSet(model, phone, password);
            return "forward:/login";
        }
        request.removeAttribute("loginError");
        request.removeAttribute("tryTimes");

        request.getSession().setAttribute("user", user);
        return "redirect:/index";
    }

    @RequestMapping("/register")
    public String registerIndex(Model model, HttpServletRequest request) {
        String token = Nanoflake.getNanoflake();
        Token.set(request.getSession().getId(), token, tokenTimeOut);
        model.addAttribute("token", token);
        return "register";
    }

    @RequestMapping("/verify")
    public String verify(Model model,  HttpServletRequest request) throws ServletException, IOException {
        String token = Nanoflake.getNanoflake();
        Token.set(request.getSession().getId(), token, tokenTimeOut);
        model.addAttribute("token", token);
        return "verify";
    }

    @RequestMapping("/register/register")
    public String register(Model model, HttpServletRequest request, String token, String phone, String password, String repassword, String vcode) {
        String vCode = (String) request.getSession().getAttribute("vCode");
        if(vcode.trim().equals("") || phone.trim().equals("") || password.trim().equals("") || repassword.trim().equals("")){
            model.addAttribute("form", "所有信息必须都填写");
            reSet(model, phone, password);
            return "forward:/register";
        }
        if (!vcode.toUpperCase().equals(vCode.toUpperCase())){
            model.addAttribute("vcode", "验证码错误");
            reSet(model, phone, password);
            return "forward:/register";
        }
        if(phone.length() != 11){
            model.addAttribute("phone", "电话号码错误");
            reSet(model, phone, password);
            return "forward:/register";
        }
        if(!password.equals(repassword)){
            model.addAttribute("password", "两次密码不一致");
            reSet(model, phone, password);
            return  "forward:/register";
        }
        if(password.length() < 8){
            model.addAttribute("password", "密码太短");
            reSet(model, phone, password);
            return  "forward:/register";
        }
        if(password.length() > 20){
            model.addAttribute("password", "密码太长");
            reSet(model, phone, password);
            return  "forward:/register";
        }

        if(!Token.check(request.getSession().getId(), token)){
            model.addAttribute("form", "您的签名不正确，表单失效");
            reSet(model, phone, password);
            return "forward:/register";
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        if(!registerService.check(user)){
            model.addAttribute("form", "该号码已被使用");
            reSet(model, phone, password);
            return "forward:/register";
        }

        String code = SMSUtil.sendMsg(phone);
//        log.warn("code: " + code);
        Token.set(request.getSession().getId() + "P", code, tokenTimeOut);

        request.getSession().setAttribute("ruser", user);
        return "forward:/verify";
    }

    private void reSet(Model model, String email, String password){
        if(!email.trim().equals(""))
            model.addAttribute("rphone", email);
        if(!password.trim().equals(""))
            model.addAttribute("rpassword", password);
    }

    @RequestMapping("/check")
    public String check(Model model, String token, String verifycode, HttpServletRequest request){
        if(!Token.check(request.getSession().getId(), token)){
            model.addAttribute("form", "您的签名不正确，表单失效");
            return "forward:/verify";
        }
        User user = (User) request.getSession().getAttribute("ruser");
        if(user == null){
            model.addAttribute("form", "未知错误");
            return "forward:/verify";
        }
        if(!Token.check(request.getSession().getId() + "P", verifycode)){
            model.addAttribute("form", "短信验证码错误");
            return "forward:/verify";
        }
        request.getSession().setAttribute("user", registerService.register(user));;
        return "redirect:/index";
    }

    @RequestMapping("/album")
    public String album(HttpServletRequest request) {
        ResourceService.LabelModel labelModel = (ResourceService.LabelModel) request.getSession().getAttribute("labelModel");
        if (labelModel == null) {
            labelModel = new ResourceService.LabelModel();
            request.getSession().setAttribute("labelModel", labelModel);
        }
        return "album";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index";
    }

    /**
     * 只是用来测试Spring boot的
     * @return ResponseBody表示返回的字符在网页上直接显示
     */
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }
}
