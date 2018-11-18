package com.we.weblog.web.controller.admin;

import cn.hutool.crypto.SecureUtil;
import com.vue.adminlte4j.model.UIModel;
import com.we.weblog.domain.User;
import com.we.weblog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 用户信息的修改
 *
 * @author tangwei
 * @date 2018/11/5 19:55
 */
@Controller
@RequestMapping("/admin/user")
public class UserController {


    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;


    /**
     *
     *  处理修改用户资料的请求

     * @param user
     * @param result
     * @param session
     */
    @PostMapping(value = "save")
    @ResponseBody
    public UIModel saveProfile(@Valid @ModelAttribute User user, BindingResult result, HttpSession session) {
        try{
            //对Contoller层的成熟校验 不知道有什么用
            if (result.hasErrors()) {
                for (ObjectError error : result.getAllErrors()) {
                    return new UIModel().fail().msg("用户资料修改失败");
                }
            }
            userService.saveByUser(user);
            //这里修改了不删Session会怎么样 我这里应该暂时不用处理吧

        }catch (Exception e){
        }
        return UIModel.success().msg("修改信息成功");
    }


    /**
     * 更改密码
     * @param beforePass
     * @param newPass
     * @param userId
     * @param session
     */
    @PostMapping(value = "updatePass")
    @ResponseBody
    public UIModel changePass(@ModelAttribute("beforePass") String beforePass,
                                 @ModelAttribute("newPass") String newPass,
                                 @ModelAttribute("userId") String userId,
                                 HttpSession session) {
            try {
                User user = userService.findByUserIdAndUserPass(userId, beforePass);
                if (null != user) {
                    userService.saveByUser(user);
                } else {
                    return UIModel.fail().msg("改用户不存在");

                }
            } catch (Exception e) {
                LOG.error("修改密码失败：{}", e.getMessage());
                return UIModel.fail().msg("修改密码失败");
            }
        return UIModel.success().msg("修改密码成功");
    }

}
