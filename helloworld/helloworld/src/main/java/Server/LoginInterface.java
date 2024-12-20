package Server;

import java.util.Random;

/**
 * Problem:
 * Author:
 * Date:2023/11/2120:29
 **/
public interface LoginInterface {

    /*一些规范*/
    //判断字符串中是否有中文
    static boolean panChinese(String s){
        return s.matches(".*[\u4e00-\u9fa5].*");
    }

    //s是整数
    static boolean keptZhengNumber(String s){
        System.out.println("s="+s);
        for(int i=0;i<s.length();i++){
            if(!((s.charAt(i)<='9'&&s.charAt(i)>='0')||(s.charAt(i)=='-'&&i==0))){
                return false;
            }
        }
        return true;
    }
    //对用户名name进行格式的验证,格式正确返回"true",错误返回错误信息的字符串
    static String regularName(String name){
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
            if(panChinese(name)){
                return "用户名不符合规范！"+"\n"+"用户名需要4-40个字符，只允许是大小写字母、数字，不允许特殊符号";
            }
        }
        return "true";
    }

    //生成一个id是9位的整数
    static long generateRandom9DigitNumber() {
        Random random = new Random();
        long min = 100000000L; // 最小的10位数
        long max = 999999999L; // 最大的10位数
        // 生成随机整数，范围在min和max之间
        long id=min + ((long) (random.nextDouble() * (max - min)));
        System.out.println("id为"+id+"的用户，注册成功！");
        return id;
    }

    //验证电话号码tel的正确性（仅针对大陆电话），正确返回true，错误返回false
    public boolean isTelephoneRegular(String tel);

    //对密码secret进行格式的验证,格式正确返回"true",错误返回错误信息的字符串
    String regularPassword(String secret);

    //对用户名name和密码secret进行格式的验证,格式正确返回"true",错误返回错误信息的字符串
    public String regular(String name, String secret);

    // 进行 用户名name和密码secret 的账号注册,如果没有重名账号，直接添加用户信息到后台中，并返回id；重名返回"false"
    public String register(String name,String secret,String tel);

    // 验证用户名name和密码secret是否正确，正确返回新的token，不正确返回”false“
    public String login(String name, String secret) ;

    //改变 name（位用户名或者id） 绑定的电话为newtel,有改成功返回true，失败就是没有name这个账号
    public boolean reset_tel(String name,String oldtel,String newtel);

    //根据 name和绑定的电话newtel 去修改 密码为 newpassword ，失败就是绑定的账号不对，返回false
    public boolean reset_secret(String name,String tel,String newpassword);

    //通过token登录,可以登录返回true，不能返回false
    public boolean login_token(String token);


    //调用RATE.bangding
    public void bangding();

    public int getIdByAny(String nameorid);
    public String getNameByAny(String nameorid);


}
