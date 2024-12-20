package Server;

import Server.StoreData.TokenAndBasicData.BasicDataInterface;
import Server.StoreData.TokenAndBasicData.TokenAndUserBaiscData;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Problem:
 * Author:王成耀
 * Date:2023/10/1717:18
 **/


// 用户名需要4-40个字符，只允许是大小写字母、数字，不允许特殊符号
// 密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种

//实现用户调用接口方法，这些方法去操作调用数据库
public class ConnectInterface implements LoginInterface {
    public static Logger warfare_logger = Logger.getLogger(ConnectInterface.class);

    //标准访问数据库的接口
    final static BasicDataInterface BASIC_DATA_INTERFACE =new TokenAndUserBaiscData();

    //验证电话号码tel的正确性（仅针对大陆电话），正确返回true，错误返回false
    @Override
    public boolean isTelephoneRegular(String tel){
        warfare_logger.info("ConnectInterface.isTelephoneRegular被调用\ntel="+tel+"\n");
        // 使用正则表达式匹配中国手机号码的格式
        String regex = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tel);
        return matcher.matches();
    }

    //对用户名name和密码secret进行格式的验证,格式正确返回"true",错误返回错误信息的字符串
    @Override
    public String regular(String name, String secret){
        warfare_logger.info("ConnectInterface.regular被调用\nname="+name+" secret=***********"+"\n");
        if(name.length()>40) {
            return "用户名太长了！"+"\n"+"用户名需要4-40个字符，只允许是大小写字母、数字，不允许特殊符号";
        } else if(name.length()<4) {
            return "用户名太短了！"+"\n"+"用户名需要4-40个字符，只允许是大小写字母、数字，不允许特殊符号";
        } else{
            for(int i=0;i<name.length();i++){
                if((name.charAt(i)<'0')&&(name.charAt(i)>'9')&&(name.charAt(i)>'z')&&(name.charAt(i)<'a')&&(name.charAt(i)>'Z')&&(name.charAt(i)<'A')){
                    return "用户名不符合规范！"+"\n"+"用户名需要4-40个字符，只允许是大小写字母、数字，不允许特殊符号";
                }
            }
            if(LoginInterface.panChinese(name)){
                return "用户名不符合规范！"+"\n"+"用户名需要4-40个字符，只允许是大小写字母、数字，不允许特殊符号";
            }
        }
        if(secret.length()>40) {
            return "密码太长了！"+"\n"+"密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种";
        } else if(secret.length()<8) {
            return "密码太短了！"+"\n"+"密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种";
        } else{
            int nump=0,zimup=0,teshup=0;
            for(int i=0;i<secret.length();i++){
                if((secret.charAt(i)>='0')&&(secret.charAt(i)<='9')){
                    nump=1;
                }
                else if((secret.charAt(i)<='z'&&secret.charAt(i)>='a')||(secret.charAt(i)<='Z'&&secret.charAt(i)>='A')){
                    zimup=1;
                }
                else{
                    teshup=1;
                }
            }
            if(LoginInterface.panChinese(secret)||nump+zimup+teshup<2){
                return "密码不符合规范！"+"\n"+"密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种";
            }
        }
        return "true";
    }

    //对密码secret进行格式的验证,格式正确返回"true",错误返回错误信息的字符串
    @Override
    public String regularPassword(String secret){
        if(secret.length()>40) {
            return "密码太长了！"+"\n"+"密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种";
        } else if(secret.length()<8) {
            return "密码太短了！"+"\n"+"密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种";
        } else{
            int nump=0,zimup=0,teshup=0;
            for(int i=0;i<secret.length();i++){
                if((secret.charAt(i)>='0')&&(secret.charAt(i)<='9')){
                    nump=1;
                }
                else if((secret.charAt(i)<='z'&&secret.charAt(i)>='a')||(secret.charAt(i)<='Z'&&secret.charAt(i)>='A')){
                    zimup=1;
                }
                else{
                    teshup=1;
                }
            }
            if(LoginInterface.panChinese(secret)||nump+zimup+teshup<2){
                return "密码不符合规范！"+"\n"+"密码需要8-40个字符，要求至少出现字母、数字和特殊符号这3种类型中的2种";
            }
        }
        return "true";
    }

    // 进行 用户名name和密码secret 的账号注册,如果没有重名账号，直接添加用户信息到后台中，并返回id；重名返回"false"
    @Override
    public String register(String name, String secret, String tel){
        warfare_logger.info("ConnectInterface.register被调用\nname="+name+" secret=*********** tel="+tel+"\n");
        if(BASIC_DATA_INTERFACE.isUsernameExists(name)|| BASIC_DATA_INTERFACE.isTelephoneExists(tel)){
            return "false";
        }
        else {
            long id= LoginInterface.generateRandom9DigitNumber();
            BASIC_DATA_INTERFACE.insertUser(id,name,String.valueOf((id+secret+id).hashCode()),tel);
            return Integer.toString((int) id);
        }
    }

    // 验证用户名name和密码secret是否正确，正确返回新的token，不正确返回”false“
    @Override
    public String login(String name, String secret) {
        warfare_logger.info("ConnectInterface.login被调用\nname="+name+" secret=***********\n");
        long id;
        String real_name=null;
        if(BASIC_DATA_INTERFACE.isUsernameExists(name)&&BASIC_DATA_INTERFACE.isNameAndPasswordMatch(name,String.valueOf((BASIC_DATA_INTERFACE.getIdByName(name)+secret+BASIC_DATA_INTERFACE.getIdByName(name)).hashCode()))){
            id=BASIC_DATA_INTERFACE.getIdByName(name);
            real_name=name;
        }else if(LoginInterface.keptZhengNumber(name)&&BASIC_DATA_INTERFACE.isIdExists(Integer.parseInt(name))&&BASIC_DATA_INTERFACE.isIdAndPasswordMatch(Integer.parseInt(name),String.valueOf((name+secret+name).hashCode()))){
            id=Integer.parseInt(name);
            real_name=BASIC_DATA_INTERFACE.getNameById(id);
        }else{
            return "false";
        }
        return BASIC_DATA_INTERFACE.getToken(id);
    }

    //改变 name（位用户名或者id） 绑定的电话为newtel,有改成功返回true，失败就是没有name这个账号
    @Override
    public boolean reset_tel(String name, String oldtel, String newtel){
        warfare_logger.info("ConnectInterface.reset_tel被调用\nname="+name+" oldtel="+oldtel+" newtel="+newtel+"\n");
        long id;
        if(BASIC_DATA_INTERFACE.isUsernameExists(name)&&BASIC_DATA_INTERFACE.isNameAndTelephoneExists(name,oldtel)){
            id=BASIC_DATA_INTERFACE.getIdByName(name);
        }else if(LoginInterface.keptZhengNumber(name)&&BASIC_DATA_INTERFACE.isIdExists(Integer.parseInt(name))&&BASIC_DATA_INTERFACE.isIdAndTelephoneExists(Integer.parseInt(name),oldtel)){
            id=Integer.parseInt(name);
        }else{
            return false;
        }
        BASIC_DATA_INTERFACE.updateTelephone(id,newtel);
        return true;
    }

    //根据 name和绑定的电话newtel 去修改 密码为 newpassword ，失败就是绑定的账号不对，返回false
    @Override
    public boolean reset_secret(String name, String tel, String newpassword){
        warfare_logger.info("ConnectInterface.reset_secret被调用\nname="+name+" tel="+tel+" newpassword=***********\n");
        long id;
        if(BASIC_DATA_INTERFACE.isUsernameExists(name)&&BASIC_DATA_INTERFACE.isNameAndTelephoneExists(name,tel)){
            id=BASIC_DATA_INTERFACE.getIdByName(name);
        }else if(LoginInterface.keptZhengNumber(name)&&BASIC_DATA_INTERFACE.isIdExists(Integer.parseInt(name))&&BASIC_DATA_INTERFACE.isIdAndTelephoneExists(Integer.parseInt(name),tel)){
            id=Integer.parseInt(name);
        }else{
            return false;
        }
        BASIC_DATA_INTERFACE.updateUserPassword(id,String.valueOf((id+newpassword+id).hashCode()));
        return true;
    }

    //通过token登录,可以登录返回true，不能返回false
    @Override
    public boolean login_token(String token){
        warfare_logger.info("ConnectInterface.login_token被调用\ntoken="+token+"\n");
        ArrayList<String> listtoken = BasicDataInterface.brokenToke(token);
        if(listtoken==null){
            return false;
        }
        return BASIC_DATA_INTERFACE.isIdAndTokenExists(Integer.parseInt(listtoken.get(0)), listtoken.get(1));
    }

    //调用RATE.bangding
    @Override
    public void bangding(){
        warfare_logger.info("ConnectInterface.bangding被调用\n");
        new RATE().bangding();
    }

    //通过name或者id获取name或者id
    @Override
    public int getIdByAny(String nameorid){
        String real_name=null;
        if(BASIC_DATA_INTERFACE.isUsernameExists(nameorid)){
            return (int) BASIC_DATA_INTERFACE.getIdByName(nameorid);
        }else if(LoginInterface.keptZhengNumber(nameorid)&&BASIC_DATA_INTERFACE.isIdExists(Integer.parseInt(nameorid))){
            return Integer.parseInt(nameorid);
        }else {
            return -1;
        }
    }
    @Override
    public String getNameByAny(String nameorid){
        if(BASIC_DATA_INTERFACE.isUsernameExists(nameorid)){
            return nameorid;
        }else if(LoginInterface.keptZhengNumber(nameorid)&&BASIC_DATA_INTERFACE.isIdExists(Integer.parseInt(nameorid))){
            return BASIC_DATA_INTERFACE.getNameById(Integer.parseInt(nameorid));
        }else {
            return null;
        }
    }

    //绑定第三方账号
    class RATE{
        public void bangding(){
            String input = JOptionPane.showInputDialog("输入请求", "1绑定账号 0解绑账号");
            if("0".equals(input)){
                jiebang();
                return;
            }
            else if(!"1".equals(input)){
                JOptionPane.showMessageDialog(null, "操作错误！");
                return;
            }
            input = JOptionPane.showInputDialog(null, "输入想要绑定账号的所在平台");
            String temp=input;
            input = JOptionPane.showInputDialog(null, "输入想要绑定的账号");
            JOptionPane.showMessageDialog(null, "绑定成功！");
        }
        public void jiebang(){
            String input = JOptionPane.showInputDialog("输入想要解绑账号的所在平台",null );
            String temp=input;
            input = JOptionPane.showInputDialog(null, "输入想要解绑的账号");
            JOptionPane.showMessageDialog(null, "解绑成功！");
        }
    }
}







