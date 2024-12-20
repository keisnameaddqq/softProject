package Customer;


import Communication.AboutLoginMessage;
import Communication.AfterLoginFaceMessage;
import Communication.Game2Message;
import Communication.MyMessage;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Problem:
 * Author:
 * Date:2023/11/1417:58
 **/

//客户端供应调用的常量数据
class CustomerConstData {
    //保存头像的路径
    public final static String touxiangPath="C:\\Users\\86198\\Desktop\\Java程序设计\\课程相关代码\\GUI充血\\src\\main\\java\\Customer\\imag\\touxiang.jpg";
    //保存 操作数+token 或者 操作数+S_name 的文件
    public final static String customer_file="C:\\Users\\86198\\Desktop\\Java程序设计\\课程相关代码\\GUI充血\\src\\main\\java\\Customer\\customer_data.txt";
    //服务段ip地址和端口
    public final static String server_IpAddress="localhost";
    public final static int server_Post=1001;
}

//用于客户端和服务器端进行数据传输时的对象传输
class UserInCustomer{


    //登录成功后实现的各种页面操作
    CustomerAfterLogin customerAfterLogin=new CustomerAfterLogin();
    class CustomerAfterLogin{
        String userName,telephone;
        int userId;
        ArrayList<Byte> touxiang;
        HashMap<Integer,String> allFriends;
        String token;

        //初始化数据，开启登录后的窗口
        public void kaiQi(){
            //先获取数据
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state= AfterLoginFaceMessage.AfterLoginFaceMessage_GetAllData;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            userName=myMessageGet.afterLoginFaceMessage.userName;
            userId=myMessageGet.afterLoginFaceMessage.userId;
            token=myMessageGet.afterLoginFaceMessage.token;
            touxiang= myMessageGet.afterLoginFaceMessage.touxiang;
            telephone=myMessageGet.afterLoginFaceMessage.telephone;
            allFriends=myMessageGet.afterLoginFaceMessage.allFriends;
            setTouxiang();
            //启动页面
            if(finishLogin.user_frame!=null){
                finishLogin.user_frame.setEnabled(true);
            }else {
                finishLogin.op();
            }
        }

        //将touxiang对象的Byte流加载到头像文件touxiangPath中
        private void setTouxiang(){
            File save_File=new File(CustomerConstData.touxiangPath);
            try {
                byte[] dataArray = new byte[touxiang.size()];
                for (int i = 0; i < touxiang.size(); i++) {
                    dataArray[i] = touxiang.get(i);
                }
                Files.write(save_File.toPath(), dataArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //根据UserConstData.touxiangPath中实际的头像数据，实现更新ArrayList<Byte>touxiang这行数据
        private void changTouXiang(){
            adjustTouxiang();
            ArrayList<Byte> newtouxiang=new ArrayList<>();
            File file = new File(CustomerConstData.touxiangPath);
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[1024];
                int bytesRead;
                while((bytesRead=bufferedInputStream.read(buffer,0,buffer.length))!=-1){
                    for(int i=0;i<bytesRead;i++){
                        newtouxiang.add(buffer[i]);
                    }
                }
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            touxiang=newtouxiang;
            MyMessage myMessageSend = new MyMessage();
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_UpdateTouxiang;
            myMessageSend.afterLoginFaceMessage.touxiang=new ArrayList<Byte>(newtouxiang);
            MyMessage myMessageGet = SendAndGet(myMessageSend);
        }
        //更新服务器的用户昵称
        private String change_name(String newName){
            userName=newName;
            MyMessage myMessageSend = new MyMessage();
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_UpdateUsername;
            myMessageSend.afterLoginFaceMessage.userName=newName;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            if(myMessageGet.afterLoginFaceMessage.updateUsernameResult) {
                return "true";
            } else {
                return "false";
            }
        }
        //改变头像大小与页面设置相符
        private void adjustTouxiang(){
            try {
                // 读取原始图片
                BufferedImage originalImage = ImageIO.read(new File(CustomerConstData.touxiangPath));
                // 调整图片大小
                int targetWidth = 100;
                int targetHeight = 100;
                BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
                graphics2D.dispose();
                // 将调整大小后的图片写入新文件
                ImageIO.write(resizedImage, "jpg", new File(CustomerConstData.touxiangPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //find-检查是否存在具有给定friendName/friendid的好友
        private boolean doesFriendNameExist(String friendName){
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DoesFriendNameExist;
            myMessageSend.afterLoginFaceMessage.friendName=friendName;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            return myMessageGet.afterLoginFaceMessage.doesFriendNameExistResult;
        }
        private boolean doesFriendIdExist(int friendId){
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DoesFriendIdExist;
            myMessageSend.afterLoginFaceMessage.friendId=friendId;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            return myMessageGet.afterLoginFaceMessage.doesFriendIdExistResult;
        }
        //find-添加一个通过用户名查找好友并返回Map<Integer, String>的方法
        public Map<Integer, String> findFriendsByUsername(String friendName) {
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_FindFriendsByUsername;
            myMessageSend.afterLoginFaceMessage.friendName=friendName;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            return myMessageGet.afterLoginFaceMessage.findFriendsResult;
        }
        //find-添加一个通过用户ID查找好友并返回Map<Integer, String>的方法
        public Map<Integer, String> findFriendsByUserId(long friendId) {
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_FindFriendsByUserId;
            myMessageSend.afterLoginFaceMessage.friendId= (int) friendId;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            return myMessageGet.afterLoginFaceMessage.findFriendsResult;
        }
        //delete-添加一个通过用户名删除好友的方法
        public void deleteFriendsByUsername(String friendName) {
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DeleteFriendsByUsername;
            myMessageSend.afterLoginFaceMessage.friendName= friendName ;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
        }
        //delete-添加一个通过用户ID删除好友的方法
        public void deleteFriendsByUserId(long friendId) {
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DeleteFriendsByUserId;
            myMessageSend.afterLoginFaceMessage.friendId = (int) friendId;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
        }
        //add-添加一个好友的方法
        private void addFriends(int friendId, String friendName) {
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterfac_AddFriends;
            myMessageSend.afterLoginFaceMessage.friendId = friendId;
            myMessageSend.afterLoginFaceMessage.friendName = friendName;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
        }
        //添加一个返回所有好友信息的方法，返回类型为Map<Integer, String>
        public Map<Integer, String> getAllFriends() {
            MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForLoginFace;
            myMessageSend.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_GetAllFriends;
            MyMessage myMessageGet = SendAndGet(myMessageSend);
            this.allFriends=myMessageGet.afterLoginFaceMessage.allFriends;
            return this.allFriends;
        }

        //登录成功后的用户界面
        FinishLogin finishLogin=new FinishLogin();
        class FinishLogin {

            public JFrame user_frame;

            FinishLogin(){
            }

            //页面实现
            public void op() {
                user_frame = new JFrame("用户页面");
                user_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 设置窗口关闭时什么都不操作
                user_frame.setSize(400, 500);

                //设置关闭窗口的操作
                user_frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.out.println("user_frame windows close...");
                        // 然后关闭窗口
                        user_frame.dispose();
                    }
                });

                //面板
                JPanel panel = new JPanel();user_frame.add(panel);panel.setLayout(null);
                //昵称展示
                JLabel namep=new JLabel("昵称：");namep.setBounds(110,10,40,25);panel.add(namep);
                JLabel namek=new JLabel(userName);namek.setBounds(150,10,120,25);panel.add(namek);
                //更改昵称
                JButton nameChangeButton=new JButton("更改昵称");nameChangeButton.setBounds(280,10,90,25);panel.add(nameChangeButton);
                nameChangeButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String inputName = JOptionPane.showInputDialog("请输入想要修改的昵称：");
                        String ret= change_name(inputName);
                        if(!"true".equals(ret)){
                            JOptionPane.showMessageDialog(user_frame, ret, "错误", JOptionPane.ERROR_MESSAGE);
                        }else{
                            userName=inputName;
                            namek.setText(inputName);
                            JOptionPane.showMessageDialog(user_frame, "昵称修改成功", "通知", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                } );

                //更改密码
                JButton passwordChangeButton=new JButton("更改密码");passwordChangeButton.setBounds(280,40,90,25);panel.add(passwordChangeButton);
                passwordChangeButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String inputName = JOptionPane.showInputDialog("请输入想要修改的密码：");
                    }
                } );

                //头像展示
                JLabel imageLabel = new JLabel();imageLabel.setIcon(new ImageIcon(CustomerConstData.touxiangPath));imageLabel.setBounds(0, 0, 100, 100);panel.add(imageLabel);
                //头像更改
                JButton uploadButton = new JButton("更换头像");uploadButton.setBounds(0, 100, 100, 25);panel.add(uploadButton);
                uploadButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new FileNameExtensionFilter("JPG & GIF Images","jpg","gif"));
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            try {
                                System.out.println("selectedFile="+selectedFile);
                                Files.copy(selectedFile.toPath(), Paths.get(CustomerConstData.touxiangPath), StandardCopyOption.REPLACE_EXISTING);
                                changTouXiang();
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 使用ImageIO来加载图片并强制刷新
                                            BufferedImage image = ImageIO.read(new File(CustomerConstData.touxiangPath));
                                            imageLabel.setIcon(new ImageIcon(image));
                                            imageLabel.revalidate();
                                            imageLabel.repaint();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

                //好友列表
                JLabel namef=new JLabel("好友列表：");namef.setBounds(0,140,100,25);panel.add(namef);
                //创建一个列表模型
                DefaultListModel<String> listModel = new DefaultListModel<>();getNowFriendList(listModel);
                JList<String> friendList = new JList<>(listModel);friendList.setOpaque(true);friendList.setBackground(new Color(255,255,255));

                //创建一个列表模型，并将列表添加到滚动面板，以便当好友过多时可以滚动
                JScrollPane scrollPane = new JScrollPane(friendList);scrollPane.setBounds(0,170,200,200);panel.add(scrollPane);

                //添加好友按钮
                JButton addFriendButton=new JButton("添加好友");addFriendButton.setBounds(10,375,90,25);panel.add(addFriendButton);
                addFriendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input = JOptionPane.showInputDialog("输入想要添加的好友的用户名或id",null );
                        if(input!=null){
                            if(doesFriendNameExist(input)||(keptZhengNumber(input)&&doesFriendIdExist(Integer.parseInt(input)))){
                                JOptionPane.showMessageDialog(user_frame, "已成为好友！无需再添加！", "错误", JOptionPane.ERROR_MESSAGE);
                            }else{
                                boolean isUsernameExists=false,isIdExists=false;
                                MyMessage myMessageSend1 = new MyMessage();myMessageSend1.MessageAbout=MyMessage.AskForLoginFace;
                                myMessageSend1.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_IsUsernameExists;
                                myMessageSend1.afterLoginFaceMessage.isUsername=input;
                                MyMessage myMessageGet1 = SendAndGet(myMessageSend1);
                                isUsernameExists=myMessageGet1.afterLoginFaceMessage.isUsernameExistsResult;
                                if(keptZhengNumber(input)){
                                    MyMessage myMessageSend2 = new MyMessage();myMessageSend2.MessageAbout=MyMessage.AskForLoginFace;
                                    myMessageSend2.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_IsIdExists;
                                    myMessageSend2.afterLoginFaceMessage.isId=Integer.parseInt(input);
                                    MyMessage myMessageGet2 = SendAndGet(myMessageSend2);
                                    isIdExists=myMessageGet2.afterLoginFaceMessage.isIdExistsResult;
                                }
                                if(isIdExists||isUsernameExists){
                                    if(isUsernameExists) {
                                        MyMessage myMessageSend3 = new MyMessage();myMessageSend3.MessageAbout=MyMessage.AskForLoginFace;
                                        myMessageSend3.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_GetIdByName;
                                        myMessageSend3.afterLoginFaceMessage.getByUsername=input;
                                        MyMessage myMessageGet3 = SendAndGet(myMessageSend3);
                                        long frienId = myMessageGet3.afterLoginFaceMessage.getById;
                                        addFriends((int) frienId, input);
                                        JOptionPane.showMessageDialog(null,"好友 id:"+frienId+" 用户名:"+input+" 添加成功！");
                                    }else {
                                        MyMessage myMessageSend4 = new MyMessage();myMessageSend4.MessageAbout=MyMessage.AskForLoginFace;
                                        myMessageSend4.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_GetNameById;
                                        myMessageSend4.afterLoginFaceMessage.getById=Integer.parseInt(input);
                                        MyMessage myMessageGet4 = SendAndGet(myMessageSend4);
                                        String friendName = myMessageGet4.afterLoginFaceMessage.getByUsername;
                                        addFriends(Integer.parseInt(input), friendName);
                                        JOptionPane.showMessageDialog(null,"好友 id:"+Integer.parseInt(input)+" 用户名:"+friendName+" 添加成功！");
                                    }
                                    getNowFriendList(listModel);
                                }
                                else {
                                    JOptionPane.showMessageDialog(user_frame, "你输入的用户名或id错误", "错误", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                });
                //删除好友按钮
                JButton deleteFriendButton=new JButton("删除好友");deleteFriendButton.setBounds(10,405,90,25);panel.add(deleteFriendButton);
                deleteFriendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input = JOptionPane.showInputDialog("输入想要删除的好友的用户名或id",null );
                        if(input!=null){
                            if(doesFriendNameExist(input)||(keptZhengNumber(input)&&doesFriendIdExist(Integer.parseInt(input)))){
                                Map<Integer, String> friendsData = null;
                                if(doesFriendNameExist(input)) {
                                    friendsData = findFriendsByUsername(input);
                                    deleteFriendsByUsername(input);
                                }else if(keptZhengNumber(input)&&doesFriendIdExist(Integer.parseInt(input))) {
                                    friendsData = findFriendsByUserId(Integer.parseInt(input));
                                    deleteFriendsByUserId(Integer.parseInt(input));
                                }
                                assert friendsData != null;
                                for (Map.Entry<Integer, String> entry : friendsData.entrySet()) {
                                    JOptionPane.showMessageDialog(null,"成功删除你的好友（id:"+entry.getKey()+" 用户名:"+entry.getValue()+"）！");
                                }
                                getNowFriendList(listModel);
                            }else {
                                JOptionPane.showMessageDialog(user_frame, "你输入的用户名或id错误，不存在该好友", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
                //查找好友按钮
                JButton findFriendButton=new JButton("查找好友");findFriendButton.setBounds(10,435,90,25);panel.add(findFriendButton);
                findFriendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input = JOptionPane.showInputDialog("输入想要查找的好友的用户名或id",null );
                        if(input!=null){
                            if(doesFriendNameExist(input)||(keptZhengNumber(input)&&doesFriendIdExist(Integer.parseInt(input)))){
                                Map<Integer, String> friendsData = null;
                                if(doesFriendNameExist(input)) {
                                    friendsData = findFriendsByUsername(input);
                                }else if(keptZhengNumber(input)&&doesFriendIdExist(Integer.parseInt(input))) {
                                    friendsData = findFriendsByUserId(Integer.parseInt(input));
                                }
                                assert friendsData != null;
                                for (Map.Entry<Integer, String> entry : friendsData.entrySet()) {
                                    JOptionPane.showMessageDialog(null,"你的好友：\n id:"+entry.getKey()+" 用户名:"+entry.getValue());
                                }
                            } else {
                                JOptionPane.showMessageDialog(user_frame, "不存在该好友", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                    }
                });

                //游戏列表
                JLabel nameg=new JLabel("游戏列表：");nameg.setBounds(220,140,100,25);panel.add(nameg);
                //添加游戏按钮——石头剪刀布（人机）
                JButton game1Button=new JButton("石头剪刀布（人机）");game1Button.setBounds(220,170,150,25);panel.add(game1Button);
                game1Button.setOpaque(true);game1Button.setBackground(Color.CYAN);
                game1Button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new MyGame().scissorsStoneClothWithComputer.playGame();
                    }
                });
                //添加游戏按钮——石头剪刀布（好友）
                JButton game2Button=new JButton("石头剪刀布（好友）");game2Button.setBounds(220,200,150,25);panel.add(game2Button);
                game2Button.setOpaque(true);game2Button.setBackground(Color.CYAN);
                game2Button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new MyGame().scissorsStoneClothWithFriend.playGame();
                    }
                });

                user_frame.setVisible(true);
            }

            //直接从数据库获取好友列表，返回一个DefaultListModel<String>对象，直接给JList<String>调用
            private void getNowFriendList(DefaultListModel<String> listModel){
                listModel.clear();
                //添加一些好友到列表模型
                Map<Integer, String> allFriends = getAllFriends();
                for (Map.Entry<Integer, String> entry : allFriends.entrySet()) {
                    listModel.addElement("id："+entry.getKey()+"  用户名："+entry.getValue());
                }
            }

            //s是整数
            private boolean keptZhengNumber(String s){
                for(int i=0;i<s.length();i++){
                    if(!((s.charAt(i)<='9'&&s.charAt(i)>='0')||(s.charAt(i)=='-'&&i==0))){
                        return false;
                    }
                }
                return true;
            }

        }
    }


    //登录成功前实现的各种页面操作
    CustomerBeforeLogin customerBeforeLogin=new CustomerBeforeLogin();
    class CustomerBeforeLogin {

        //frameregister和frameforget页面都会用到这两个变量，实现验证码的计时发
        private Timer timer,timer1;
        private int remainingSeconds = 60,remainingSeconds1=60;
        //注册页面对象的辅助变量
        private JFrame frameregister=null;
        //忘记密码页面对象的辅助变量
        private JFrame frameforget=null;
        //解绑电话号码页面对象的辅助变量
        private JFrame framephone=null;
        //登录页面对象的辅助变量
        public JFrame framelogin=null;
        private String S_Name=null;//三选按钮中保存用户名的
        private String caozuoshu="";//三选按钮的状态,2/1/0不记账密、记住账号、保持登录（默认）
        private String token=null;//存储 toke 的临时变量

        // 实现注册页面，让用户输入账号、密码和验证码
        public void register() {
            frameregister = new JFrame("注册");  // 创建一个新的JFrame窗口
            frameregister.setSize(400, 300);  // 设置窗口大小
            frameregister.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);  // 设置窗口关闭时的操作

            //设置关闭窗口的操作
            frameregister.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("registerwindows close...");
                    // 然后关闭窗口
                    frameregister.setVisible(false);
                    framelogin.setVisible(true);
                }
            });

            JPanel panel = new JPanel();  // 创建一个新的JPanel面板
            frameregister.add(panel);  // 将面板添加到窗口中
            panel.setLayout(null);  // 设置面板布局为null

            //用户输入框
            JLabel userLabel = new JLabel("用户名:");userLabel.setBounds(10, 20, 60, 25);panel.add(userLabel);
            JTextField usernameField = new JTextField(20);usernameField.setBounds(80, 20, 165, 25);panel.add(usernameField);

            //密码输入框
            JLabel passwordLabel1 = new JLabel("密码:");passwordLabel1.setBounds(10, 50, 60, 25);panel.add(passwordLabel1);
            JPasswordField passwordField1 = new JPasswordField(20);passwordField1.setBounds(80, 50, 165, 25);passwordField1.setEchoChar('*');panel.add(passwordField1);
            //复选框
            JCheckBox checkBox1 = new JCheckBox("显示密码");checkBox1.setBounds(250, 50, 135, 27);panel.add(checkBox1);
            checkBox1.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) { // 被选中
                        passwordField1.setEchoChar((char) 0); // 设置密码显示
                    } else {
                        passwordField1.setEchoChar('*'); // 设置密码隐藏
                    }
                }
            });

            //密码确认输入框
            JLabel passwordLabel2 = new JLabel("确认密码:");passwordLabel2.setBounds(10, 80, 60, 25);panel.add(passwordLabel2);
            JPasswordField passwordField2 = new JPasswordField(20);passwordField2.setBounds(80, 80, 165, 25);passwordField2.setEchoChar('*');panel.add(passwordField2);
            //复选框
            JCheckBox checkBox2 = new JCheckBox("显示密码");checkBox2.setBounds(250, 80, 135, 27);panel.add(checkBox2);
            checkBox2.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) { // 被选中
                        passwordField2.setEchoChar((char) 0); // 设置密码显示
                    } else {
                        passwordField2.setEchoChar('*'); // 设置密码隐藏
                    }
                }
            });

            //电话号码输入框
            JLabel telLabel = new JLabel("电话号码:");telLabel.setBounds(10, 110, 60, 25);panel.add(telLabel);
            JTextField telField = new JTextField(20);telField.setBounds(80, 110, 165, 25);panel.add(telField);

            //验证码输入框
            JTextField verificationCodeField = new JTextField(20);verificationCodeField.setBounds(50, 140, 100, 25);panel.add(verificationCodeField);
            //获取验证码的按钮框
            JButton getVerificationCodeButton = new JButton("获取验证码");getVerificationCodeButton.setBounds(160, 140, 120, 25);panel.add(getVerificationCodeButton);
            getVerificationCodeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getVerificationCodeButton.setEnabled(false);
                    remainingSeconds = 60;
                    timer.start();
                    String tel=telField.getText();

                    if(tel==null||tel.length()==0){
                        JOptionPane.showMessageDialog(frameregister, "请输入电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        MyMessage myMessageSend = new MyMessage();
                        myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_isTelephoneRegular;
                        myMessageSend.aboutLoginMessage.tel=tel;
                        MyMessage myMessageGet=SendAndGet(myMessageSend);
                        if(!myMessageGet.aboutLoginMessage.isTelephoneRegularResult){
                            JOptionPane.showMessageDialog(frameregister, "电话号码不正确或不是大陆电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                        } else{
                            JOptionPane.showMessageDialog(frameregister, "此部分功能尚无法实现，无法发送短信验证码，所以验证码可为空", "通知", JOptionPane.PLAIN_MESSAGE);
                        }
                    }

                }
            });
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingSeconds--;
                    if (remainingSeconds <= 0) {
                        timer.stop();
                        getVerificationCodeButton.setEnabled(true);
                        getVerificationCodeButton.setText("获取验证码");
                    } else {
                        getVerificationCodeButton.setText("重新发送(" + remainingSeconds + ")");
                    }
                }
            });

            //注册按钮
            JButton registerButton = new JButton("注册"); registerButton.setBounds(100, 170, 100, 25);panel.add(registerButton);
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name=usernameField.getText();
                    String secret1=new String(passwordField1.getPassword());
                    String secret2=new String(passwordField2.getPassword());
                    String tel=telField.getText();
                    String ret;
                    if(!secret1.equals(secret2)){
                        JOptionPane.showMessageDialog(frameregister, "两个密码不一样", "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        MyMessage myMessageSend1 = new MyMessage();
                        myMessageSend1.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_regular;
                        myMessageSend1.aboutLoginMessage.userAskName=name;
                        myMessageSend1.aboutLoginMessage.password=secret1;
                        MyMessage myMessageGet1=SendAndGet(myMessageSend1);
                        ret = myMessageGet1.aboutLoginMessage.regularResult;
                        if(!"true".equals(ret)){
                            JOptionPane.showMessageDialog(frameregister, ret, "错误", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            MyMessage myMessageSend2 = new MyMessage();
                            myMessageSend2.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_isTelephoneRegular;
                            myMessageSend2.aboutLoginMessage.tel=tel;
                            MyMessage myMessageGet2=SendAndGet(myMessageSend2);
                            if(!myMessageGet2.aboutLoginMessage.isTelephoneRegularResult){
                                JOptionPane.showMessageDialog(frameregister, "电话号码不正确或不是大陆电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                            else{
                                MyMessage myMessageSend3 = new MyMessage();
                                myMessageSend3.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_register;
                                myMessageSend3.aboutLoginMessage.userAskName=name;
                                myMessageSend3.aboutLoginMessage.tel=tel;
                                myMessageSend3.aboutLoginMessage.password=secret1;
                                MyMessage myMessageGet3=SendAndGet(myMessageSend3);
                                ret=myMessageGet3.aboutLoginMessage.registerResult;
                                System.out.println("注册");
                                if("false".equals(ret)){
                                    JOptionPane.showMessageDialog(frameregister, "用户名已存在或者手机号已被绑定", "错误", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(frameregister, "注册成功\n获得id:"+ret, "通知", JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }

                    }

                }
            });

            frameregister.setVisible(true);  // 设置窗口为可见
        }

        // 实现登录页面，让用户输入账号、密码和验证码
        public void login(){
            framelogin = new JFrame("登录");  // 创建一个新的JFrame窗口
            framelogin.setSize(400, 300);  // 设置窗口大小
            framelogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置窗口关闭时的操作

            JPanel panel = new JPanel();framelogin.add(panel);panel.setLayout(null);

            //用户输入框
            JLabel userLabel = new JLabel("用户名或id:");userLabel.setBounds(10, 20, 80, 25);panel.add(userLabel);
            JTextField usernameField = new JTextField(20);usernameField.setBounds(80, 20, 165, 25);usernameField.setText(S_Name);panel.add(usernameField);

            // 密码输入框
            JLabel passwordLabel = new JLabel("密码:");passwordLabel.setBounds(30, 50, 60, 25);panel.add(passwordLabel);
            JPasswordField passwordField = new JPasswordField(20);passwordField.setBounds(80, 50, 165, 25); passwordField.setEchoChar('*');panel.add(passwordField);
            // 密码复选框
            JCheckBox checkBox = new JCheckBox("显示密码");checkBox.setBounds(250, 50, 135, 27);panel.add(checkBox);
            checkBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) { // 被选中
                        passwordField.setEchoChar((char) 0); // 设置密码显示
                    } else {
                        passwordField.setEchoChar('*'); // 设置密码隐藏
                    }
                }
            });

            //登录按钮
            JButton loginButton = new JButton("登录");loginButton.setBounds(100, 80, 100, 25);panel.add(loginButton);
            //注册按钮
            JButton registerButton = new JButton("注册");registerButton.setBounds(10, 80, 60, 25);panel.add(registerButton);
            //忘记密码按钮
            JButton forgotPasswordButton = new JButton("忘记密码");forgotPasswordButton.setBounds(230, 80, 100, 25);panel.add(forgotPasswordButton);
            //更改绑定按钮
            JButton changePhoneButton = new JButton("手机更绑");changePhoneButton.setBounds(40, 110, 100, 25);panel.add(changePhoneButton);
            //第三方账号
            JButton otherButton = new JButton("第三方账号");otherButton.setBounds(160, 110, 100, 25);panel.add(otherButton);
            //登录、注册、忘记密码的操作
            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == loginButton) {
                        System.out.println("登录按钮被点击");
                        S_Name=usernameField.getText();System.out.println("用户名: " + S_Name);
                        String Sercet=new String(passwordField.getPassword());

                        MyMessage myMessageSend = new MyMessage();
                        myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_login;
                        myMessageSend.aboutLoginMessage.userAskName=S_Name;
                        myMessageSend.aboutLoginMessage.password=Sercet;
                        MyMessage myMessageGet=SendAndGet(myMessageSend);

                        if(!myMessageGet.aboutLoginMessage.loginResult){
                            JOptionPane.showMessageDialog(framelogin, "你输入的用户名(或id)或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                        } else {
                            token=myMessageGet.aboutLoginMessage.token;
                            JOptionPane.showMessageDialog(framelogin, "登录成功", "登录成功", JOptionPane.PLAIN_MESSAGE);
                            customerAfterLogin.kaiQi();
//                        framelogin.setVisible(false);
                        }

                    } else if (e.getSource() == registerButton) {
                        System.out.println("注册按钮被点击");
                        framelogin.setVisible(false);
                        if(frameregister!=null){
                            frameregister.setVisible(true);
                        }
                        else {
                            register();
                        }
                    } else if(e.getSource()==forgotPasswordButton){
                        System.out.println("忘记密码按钮被点击");
                        framelogin.setVisible(false);
                        if(frameforget!=null){
                            frameforget.setVisible(true);
                        }
                        else {
                            forget();
                        }
                    }else if(e.getSource()==changePhoneButton){
                        System.out.println("手机更绑按钮被点击");
                        framelogin.setVisible(false);
                        if(framephone!=null){
                            framephone.setVisible(true);
                        }
                        else {
                            phone();
                        }
                    }else if(e.getSource()==otherButton){
                        System.out.println("第三方账号按钮被点击");
//                        MyMessage myMessageSend = new MyMessage();
//                        myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_bangding;
//                        MyMessage myMessageGet=SendAndGet(myMessageSend);
                    }

                }
            };
            loginButton.addActionListener(actionListener);
            registerButton.addActionListener(actionListener);
            forgotPasswordButton.addActionListener(actionListener);
            changePhoneButton.addActionListener(actionListener);
            otherButton.addActionListener(actionListener);

            //保持登录的判断操作
            if("0".equals(caozuoshu)&&token.length()>12){
                MyMessage myMessageSend = new MyMessage();
                myMessageSend.aboutLoginMessage.state = AboutLoginMessage.AboutLogin_login_token;
                myMessageSend.aboutLoginMessage.token=token;
                MyMessage myMessageGet=SendAndGet(myMessageSend);
                if(!myMessageGet.aboutLoginMessage.login_tokenResult){
                    JOptionPane.showMessageDialog(framelogin, "token已过期，请重新登录", "错误", JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(framelogin, "登录成功", "登录成功", JOptionPane.PLAIN_MESSAGE);
                    token=myMessageGet.aboutLoginMessage.token;
                    customerAfterLogin.kaiQi();
                }
            }
            caozuoshu="0";
            //三选按钮
            ButtonGroup group = new ButtonGroup();
            JRadioButton noRemember = new JRadioButton("不记账密");
            JRadioButton rememberAccount = new JRadioButton("记住账号");
            JRadioButton keepLogin = new JRadioButton("保持登录");
            group.add(noRemember);
            group.add(rememberAccount);
            group.add(keepLogin);
            keepLogin.setSelected(true);
            noRemember.setBounds(0,140,100,50);
            rememberAccount.setBounds(100,140,100,50);
            keepLogin.setBounds(200,140,100,50);
            panel.add(noRemember);
            panel.add(rememberAccount);
            panel.add(keepLogin);

            ActionListener actionListener_p = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (noRemember.isSelected()) {
                        caozuoshu = "2";
                    } else if (rememberAccount.isSelected()) {
                        caozuoshu = "1";
                    } else {
                        caozuoshu = "0"; // 默认为“保持登录”
                    }
                    System.out.println("选中的值是：" + caozuoshu);
                }
            };
            noRemember.addActionListener(actionListener_p);
            rememberAccount.addActionListener(actionListener_p);
            keepLogin.addActionListener(actionListener_p);

            //窗口可视化
            framelogin.setVisible(true);

            //关闭窗口，结束程序，保存必要的信息到customer_file下
            framelogin.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("loginwindow ending...");
                    customerSaveLoginData();
                    // 然后关闭窗口
                    framelogin.dispose();
                }

                private void customerSaveLoginData() {
                    S_Name=usernameField.getText();
                    try {
                        //2/1/0不记账密、记住账号、保持登录（默认）
                        BufferedWriter writer =new BufferedWriter(new FileWriter(CustomerConstData.customer_file));
                        if(caozuoshu==null||"".equals(caozuoshu)) {
                            caozuoshu="0";
                        }
                        writer.write(caozuoshu+"\n");
                        if("0".equals(caozuoshu)){
                            writer.write(token+"\n");
                        }
                        else if("1".equals(caozuoshu)){
                            writer.write(S_Name+"\n");
                        }
                        writer.close();
                        System.out.println("数据已成功写入文件");
                    } catch (IOException eo) {
                        System.out.println("发生错误: " + eo.getMessage());
                    }
                }
            });

        }

        // 实现忘记密码页面
        public void forget(){
            frameforget = new JFrame("忘记密码");  // 创建一个新的JFrame窗口
            frameforget.setSize(400, 300);  // 设置窗口大小
            frameforget.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);  // 设置窗口关闭时的操作

            JPanel panel = new JPanel();frameforget.add(panel);panel.setLayout(null);

            //设置关闭窗口的操作
            frameforget.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("registerwindows close...");
                    // 然后关闭窗口
                    frameforget.setVisible(false);
                    framelogin.setVisible(true);
                }
            });

            //用户输入框
            JLabel userLabel = new JLabel("用户名或id:");userLabel.setBounds(10, 20, 70, 25);panel.add(userLabel);
            JTextField usernameField = new JTextField(20);usernameField.setBounds(80, 20, 165, 25);panel.add(usernameField);

            // 新密码输入框
            JLabel passwordLabel1 = new JLabel("新密码:");passwordLabel1.setBounds(10, 50, 60, 25);panel.add(passwordLabel1);
            JPasswordField passwordField1 = new JPasswordField(20);passwordField1.setBounds(80, 50, 165, 25);passwordField1.setEchoChar('*');panel.add(passwordField1);
            // 新密码复选框
            JCheckBox checkBox1 = new JCheckBox("显示密码");checkBox1.setBounds(250, 50, 135, 27);panel.add(checkBox1);
            checkBox1.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) { // 被选中
                        passwordField1.setEchoChar((char) 0); // 设置密码显示
                    } else {
                        passwordField1.setEchoChar('*'); // 设置密码隐藏
                    }
                }
            });


            // 新密码确认输入框
            JLabel passwordLabel2 = new JLabel("密码确认:");passwordLabel2.setBounds(10, 80, 60, 25);panel.add(passwordLabel2);
            JPasswordField passwordField2 = new JPasswordField(20);passwordField2.setBounds(80, 80, 165, 25);passwordField2.setEchoChar('*');panel.add(passwordField2);
            // 新密码复选框
            JCheckBox checkBox2 = new JCheckBox("显示密码");checkBox2.setBounds(250, 80, 135, 27);panel.add(checkBox2);
            checkBox2.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) { // 被选中
                        passwordField2.setEchoChar((char) 0); // 设置密码显示
                    } else {
                        passwordField2.setEchoChar('*'); // 设置密码隐藏
                    }
                }
            });


            //电话号码输入框
            JLabel telLabel = new JLabel("电话号码:");telLabel.setBounds(10, 110, 60, 25);panel.add(telLabel);
            JTextField telField = new JTextField(20);telField.setBounds(80, 110, 165, 25);panel.add(telField);

            //验证码输入框
            JTextField verificationCodeField = new JTextField(20);verificationCodeField.setBounds(50, 140, 100, 25);panel.add(verificationCodeField);
            //获取验证码的按钮框
            JButton getVerificationCodeButton = new JButton("获取验证码");getVerificationCodeButton.setBounds(160, 140, 120, 25);panel.add(getVerificationCodeButton);
            getVerificationCodeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getVerificationCodeButton.setEnabled(false);
                    remainingSeconds = 60;
                    timer.start();
                    String tel=telField.getText();
                    if(tel==null||tel.length()==0){
                        JOptionPane.showMessageDialog(frameforget, "请输入电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        MyMessage myMessageSend = new MyMessage();
                        myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_isTelephoneRegular;
                        myMessageSend.aboutLoginMessage.tel=tel;
                        MyMessage myMessageGet=SendAndGet(myMessageSend);
                        if(!myMessageGet.aboutLoginMessage.isTelephoneRegularResult){
                            JOptionPane.showMessageDialog(frameforget, "电话号码不正确或不是大陆电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                        else{
                            JOptionPane.showMessageDialog(frameforget, "此部分功能尚无法实现，无法发送短信验证码，所以验证码可为空", "通知", JOptionPane.PLAIN_MESSAGE);
                        }
                    }

                }
            });
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingSeconds--;
                    if (remainingSeconds <= 0) {
                        timer.stop();
                        getVerificationCodeButton.setEnabled(true);
                        getVerificationCodeButton.setText("获取验证码");
                    } else {
                        getVerificationCodeButton.setText("重新发送(" + remainingSeconds + ")");
                    }
                }
            });

            //确定按钮
            JButton registerButton = new JButton("确定");registerButton.setBounds(100, 170, 100, 25);panel.add(registerButton);
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name=usernameField.getText();
                    String secret1=new String(passwordField1.getPassword());
                    String secret2=new String(passwordField1.getPassword());
                    String tel=telField.getText();
                    String ret;
                    if(!secret1.equals(secret2)){
                        JOptionPane.showMessageDialog(frameforget, "两个密码不一样", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        MyMessage myMessageSend1 = new MyMessage();
                        myMessageSend1.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_regular_Password;
                        myMessageSend1.aboutLoginMessage.password=secret1;
                        MyMessage myMessageGet1=SendAndGet(myMessageSend1);

                        if(!"true".equals(ret=myMessageGet1.aboutLoginMessage.regular_PasswordResult)){
                            JOptionPane.showMessageDialog(frameforget, ret, "错误", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            MyMessage myMessageSend2 = new MyMessage();
                            myMessageSend2.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_isTelephoneRegular;
                            myMessageSend2.aboutLoginMessage.tel=tel;
                            MyMessage myMessageGet2=SendAndGet(myMessageSend2);
                            if(!myMessageGet2.aboutLoginMessage.isTelephoneRegularResult){
                                JOptionPane.showMessageDialog(frameforget, "电话号码不正确或不是大陆电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                            else{
                                MyMessage myMessageSend3 = new MyMessage();
                                myMessageSend3.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_reset_secret;
                                myMessageSend3.aboutLoginMessage.tel=tel;
                                myMessageSend3.aboutLoginMessage.userAskName=name;
                                myMessageSend3.aboutLoginMessage.newpassword=secret1;
                                MyMessage myMessageGet3=SendAndGet(myMessageSend3);
                                if(!myMessageGet3.aboutLoginMessage.reset_secretResult){
                                    JOptionPane.showMessageDialog(frameforget, "电话号码或账号不正确", "错误", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(frameforget, "修改成功", "正确", JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }

                    }

                }
            });

            frameforget.setVisible(true);
        }

        // 实现解绑电话号码页面，让用户输入账号、密码和验证码
        public void phone(){
            framephone = new JFrame("手机更绑");  // 创建一个新的JFrame窗口
            framephone.setSize(400, 300);  // 设置窗口大小
            framephone.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);  // 设置窗口关闭时的操作

            JPanel panel = new JPanel();  // 创建一个新的JPanel面板
            framephone.add(panel);  // 将面板添加到窗口中
            panel.setLayout(null);  // 设置面板布局为null

            //设置关闭窗口的操作
            framephone.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("phonewindows close...");
                    // 然后关闭窗口
                    framephone.setVisible(false);
                    framelogin.setVisible(true);
                }
            });

            //用户输入框
            JLabel userLabel = new JLabel("用户名或id:");userLabel.setBounds(10, 20, 80, 25);panel.add(userLabel);
            JTextField usernameField = new JTextField(20);usernameField.setBounds(100, 20, 165, 25);panel.add(usernameField);

            //旧电话号码输入框
            JLabel telLabel1 = new JLabel("旧电话号码:");telLabel1.setBounds(10, 50, 80, 25);panel.add(telLabel1);
            JTextField telField1 = new JTextField(20);telField1.setBounds(100, 50, 165, 25);panel.add(telField1);
            //验证码输入框
            JTextField verificationCodeField1 = new JTextField(20);verificationCodeField1.setBounds(50, 80, 100, 25);panel.add(verificationCodeField1);
            //获取验证码的按钮框
            JButton getVerificationCodeButton1 = new JButton("获取验证码");getVerificationCodeButton1.setBounds(160, 80, 120, 25);panel.add(getVerificationCodeButton1);
            getVerificationCodeButton1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getVerificationCodeButton1.setEnabled(false);
                    remainingSeconds = 60;
                    timer.start();
                    String tel=telField1.getText();
                    if(tel==null||tel.length()==0){
                        JOptionPane.showMessageDialog(framephone, "请输入电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        MyMessage myMessageSend = new MyMessage();
                        myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_isTelephoneRegular;
                        myMessageSend.aboutLoginMessage.tel=tel;
                        MyMessage myMessageGet=SendAndGet(myMessageSend);
                        if(!myMessageGet.aboutLoginMessage.isTelephoneRegularResult){
                            JOptionPane.showMessageDialog(framephone, "电话号码不正确或不是大陆电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                        } else{
                            JOptionPane.showMessageDialog(framephone, "此部分功能尚无法实现，无法发送短信验证码，所以验证码可为空", "通知", JOptionPane.PLAIN_MESSAGE);
                        }
                    }

                }
            });
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingSeconds--;
                    if (remainingSeconds <= 0) {
                        timer.stop();
                        getVerificationCodeButton1.setEnabled(true);
                        getVerificationCodeButton1.setText("获取验证码");
                    } else {
                        getVerificationCodeButton1.setText("重新发送(" + remainingSeconds + ")");
                    }
                }
            });

            //新电话号码输入框
            JLabel telLabel2 = new JLabel("新电话号码:");telLabel2.setBounds(10, 110, 80, 25);panel.add(telLabel2);
            JTextField telField2 = new JTextField(20);telField2.setBounds(100, 110, 165, 25);panel.add(telField2);
            //验证码输入框
            JTextField verificationCodeField2 = new JTextField(20);verificationCodeField2.setBounds(50, 140, 100, 25);panel.add(verificationCodeField2);
            //获取验证码的按钮框
            JButton getVerificationCodeButton2 = new JButton("获取验证码");getVerificationCodeButton2.setBounds(160, 140, 120, 25);panel.add(getVerificationCodeButton2);
            getVerificationCodeButton2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getVerificationCodeButton2.setEnabled(false);
                    remainingSeconds1 = 60;
                    timer1.start();
                    String tel=telField2.getText();
                    if(tel==null||tel.length()==0){
                        JOptionPane.showMessageDialog(framephone, "请输入电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        MyMessage myMessageSend = new MyMessage();
                        myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_isTelephoneRegular;
                        myMessageSend.aboutLoginMessage.tel=tel;
                        MyMessage myMessageGet=SendAndGet(myMessageSend);
                        if(!myMessageGet.aboutLoginMessage.isTelephoneRegularResult){
                            JOptionPane.showMessageDialog(framephone, "电话号码不正确或不是大陆电话号码", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(framephone, "此部分功能尚无法实现，无法发送短信验证码，所以验证码可为空", "通知", JOptionPane.PLAIN_MESSAGE);
                        }
                    }


                }
            });
            timer1 = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingSeconds1--;
                    if (remainingSeconds1 <= 0) {
                        timer1.stop();
                        getVerificationCodeButton2.setEnabled(true);
                        getVerificationCodeButton2.setText("获取验证码");
                    } else {
                        getVerificationCodeButton2.setText("重新发送(" + remainingSeconds1 + ")");
                    }
                }
            });

            //确定按钮
            JButton registerButton = new JButton("确定");registerButton.setBounds(100, 170, 100, 25);panel.add(registerButton);
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name=usernameField.getText();
                    String old_tel=telField1.getText(),new_tel=telField2.getText();
                    String old_yanzheng=verificationCodeField1.getText(),new_yanzheng=verificationCodeField2.getText();
                /*
                此处可以添加对验证码正确性的检验
                * */
                    MyMessage myMessageSend = new MyMessage();
                    myMessageSend.aboutLoginMessage.state= AboutLoginMessage.AboutLogin_reset_tel;
                    myMessageSend.aboutLoginMessage.tel=old_tel;
                    myMessageSend.aboutLoginMessage.newtel=new_tel;
                    myMessageSend.aboutLoginMessage.userAskName=name;
                    MyMessage myMessageGet=SendAndGet(myMessageSend);
                    if(myMessageGet.aboutLoginMessage.reset_telResult){
                        JOptionPane.showMessageDialog(framephone, "修改成功", "通知", JOptionPane.PLAIN_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(framephone, "电话号码或用户名不正确", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            framephone.setVisible(true);
        }


    }

    //游戏
    class MyGame{
        ScissorsStoneClothWithFriend scissorsStoneClothWithFriend=new ScissorsStoneClothWithFriend();
        ScissorsStoneClothWithComputer scissorsStoneClothWithComputer= new ScissorsStoneClothWithComputer();

        //s是整数
        private boolean keptZhengNumber(String s){
            for(int i=0;i<s.length();i++){
                if(!((s.charAt(i)<='9'&&s.charAt(i)>='0')||(s.charAt(i)=='-'&&i==0))){
                    return false;
                }
            }
            return true;
        }

        //石头剪刀布 小游戏  好友互动版本
        public class ScissorsStoneClothWithFriend {
            public JFrame game2Frame;
            public JFrame game2FrameWait;

            public ScissorsStoneClothWithFriend(){

            }

            //实现game2FrameWait
            public void playGame() {
                game2FrameWait = new JFrame("小游戏——剪刀石头布（好友）——连接");
                game2FrameWait.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                game2FrameWait.setSize(300, 200);
                JPanel panel = new JPanel();  // 创建一个新的JPanel面板
                game2FrameWait.add(panel);  // 将面板添加到窗口中
                panel.setLayout(null);  // 设置面板布局为null

                //关闭窗口，结束程序
                game2FrameWait.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.out.println("game1_frame window ending...");
                        // 然后关闭窗口
                        game2FrameWait.dispose();
                    }
                });

                //myLock[0]保证在启用listenConnectButton和connectFriendButton无法同时执行
                int[] myLock = {1};

                //连接服务器按钮
                JButton listenConnectButton = new JButton("连接服务器聆听请求");listenConnectButton.setBounds(40, 10, 200, 25);panel.add(listenConnectButton);
                JLabel listenConnectStateJLabel = new JLabel();listenConnectStateJLabel.setBounds(40, 40, 240, 25);panel.add(listenConnectStateJLabel);
                ActionListener listener1 = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(myLock[0] == 1){
                            myLock[0] = 0;
                            SwingUtilities.invokeLater(() -> {
                                listenConnectStateJLabel.setText("连接服务器聆听请求中...");
                                listenConnectStateJLabel.validate();
                                listenConnectStateJLabel.repaint();
                            });
                            SwingWorker<MyMessage, Void> worker = new SwingWorker<MyMessage, Void>() {
                                @Override
                                protected MyMessage doInBackground() {
                                    MyMessage myMessageSend = new MyMessage();
                                    myMessageSend.MessageAbout=MyMessage.AskForGame2;
                                    myMessageSend.game2Message.state= Game2Message.Game2Message_ConnectWithListen;
                                    return SendAndGet(myMessageSend);
                                }
                                @Override
                                protected void done() {
                                    try {
                                        MyMessage myMessageGet = get();
                                        if(myMessageGet.game2Message.ConnectWithListenResult){
                                            playGame(myMessageGet.game2Message.friendId);
                                            game2FrameWait.dispose();
                                        } else {
                                            listenConnectStateJLabel.setText("等待超时，无人请求连接！");
                                        }
                                        myLock[0] = 1;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            worker.execute();
                        }
                    }
                };
                listenConnectButton.addActionListener(listener1);

                //向服务器请求连接好友按钮
                JLabel friendLabel = new JLabel("好友用户名或id:");friendLabel.setBounds(10, 75, 95, 25);panel.add(friendLabel);
                JTextField friendField = new JTextField();friendField.setBounds(105, 75, 165, 25);panel.add(friendField);
                JButton connectFriendButton = new JButton("邀请好友进行游戏");connectFriendButton.setBounds(40, 105, 200, 25);panel.add(connectFriendButton);
                JLabel connectFriendStateJLabel = new JLabel();connectFriendStateJLabel.setBounds(40, 135, 240, 25);panel.add(connectFriendStateJLabel);
                ActionListener listener2 = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input=friendField.getText();
                        if(input==null || "".equals(input)){
                            SwingUtilities.invokeLater(() -> {
                                connectFriendStateJLabel.setText("请输入的好友用户名或id！");
                                connectFriendStateJLabel.validate();
                                connectFriendStateJLabel.repaint();
                            });
                        } else if(myLock[0]==1){
                            myLock[0] = 0;
                            boolean isUsernameExists=false,isIdExists=false;
                            MyMessage myMessageSend1 = new MyMessage();myMessageSend1.MessageAbout=MyMessage.AskForLoginFace;
                            myMessageSend1.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_IsUsernameExists;
                            myMessageSend1.afterLoginFaceMessage.isUsername=friendField.getText();
                            MyMessage myMessageGet1 = SendAndGet(myMessageSend1);
                            isUsernameExists=myMessageGet1.afterLoginFaceMessage.isUsernameExistsResult;
                            if(keptZhengNumber(friendField.getText())){
                                MyMessage myMessageSend2 = new MyMessage();myMessageSend2.MessageAbout=MyMessage.AskForLoginFace;
                                myMessageSend2.afterLoginFaceMessage.state=AfterLoginFaceMessage.AfterLoginFaceMessage_IsIdExists;
                                myMessageSend2.afterLoginFaceMessage.isId=Integer.parseInt(friendField.getText());
                                MyMessage myMessageGet2 = SendAndGet(myMessageSend2);
                                isIdExists=myMessageGet2.afterLoginFaceMessage.isIdExistsResult;
                            }
                            if(isUsernameExists||isIdExists){
                                SwingUtilities.invokeLater(() -> {
                                    connectFriendStateJLabel.setText("邀请好友进行游戏中...");
                                    connectFriendStateJLabel.validate();
                                    connectFriendStateJLabel.repaint();
                                });
                                SwingWorker<MyMessage, Void> worker = new SwingWorker<MyMessage, Void>() {
                                    @Override
                                    protected MyMessage doInBackground() {
                                        String text = friendField.getText();
                                        MyMessage myMessageSend = new MyMessage();
                                        myMessageSend.MessageAbout=MyMessage.AskForGame2;
                                        myMessageSend.game2Message.state= Game2Message.Game2Message_ConnectWithFriend;
                                        myMessageSend.game2Message.friendIdOrName=text;
                                        return SendAndGet(myMessageSend);
                                    }
                                    @Override
                                    protected void done() {
                                        try {
                                            MyMessage myMessageGet = get();
                                            if(myMessageGet.game2Message.ConnectWithFriendResult){
                                                playGame(myMessageGet.game2Message.friendId);
                                                game2FrameWait.dispose();
                                            } else {
                                                connectFriendStateJLabel.setText("等待超时，好友并无上线！");
                                            }
                                            myLock[0] = 1;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                worker.execute();
                            } else {
                                connectFriendStateJLabel.setText("输入的好友用户名或id错误！");
                                myLock[0]=1;
                            }

                        }
                    }
                };
                connectFriendButton.addActionListener(listener2);

                game2FrameWait.setVisible(true);
            }

            //实现game2Frame
            private void playGame(int friendId) {
                game2Frame = new JFrame("小游戏——剪刀石头布（好友）");
                game2Frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                game2Frame.setSize(300, 200);
                JPanel panel = new JPanel();  // 创建一个新的JPanel面板
                game2Frame.add(panel);  // 将面板添加到窗口中
                panel.setLayout(null);  // 设置面板布局为null

                //关闭窗口，结束程序
                game2Frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.out.println("game1_frame window ending...");
                        MyMessage myMessageSend = new MyMessage();myMessageSend.MessageAbout=MyMessage.AskForGame2;
                        myMessageSend.game2Message.state = Game2Message.Game2Message_DisConnect;
                        MyMessage myMessageGet = SendAndGet(myMessageSend);
                        // 然后关闭窗口
                        game2Frame.dispose();
                    }
                });

                JLabel promptLabel = new JLabel("请选择你的出拳：");promptLabel.setBounds(10, 20, 150, 25);panel.add(promptLabel);
                //0剪刀、1石头、2布：三个按钮
                JButton scissorsButton = new JButton("剪刀");scissorsButton.setBounds(180, 50, 60, 25);panel.add(scissorsButton);
                JButton rockButton = new JButton("石头");rockButton.setBounds(40, 50, 60, 25);panel.add(rockButton);
                JButton paperButton = new JButton("布");paperButton.setBounds(115, 50, 60, 25);panel.add(paperButton);
                //好友出拳标签
                JLabel[]nop = new JLabel[1];nop[0] = new JLabel("你的好友"+friendId+"出了：");nop[0].setBounds(10, 90, 150, 25);panel.add(nop[0]);
                //显示框好友出拳
                JLabel[]statusLabel = new JLabel[1];statusLabel[0] = new JLabel("");statusLabel[0].setBounds(160, 90, 50, 25);panel.add(statusLabel[0]);
                //获取好友出拳的状态
                JLabel statusLabel0State=new JLabel();statusLabel0State.setBounds(40, 120, 200, 25);panel.add(statusLabel[0]);
                //myLock[0]保证在启用listenConnectButton和connectFriendButton无法同时执行
                int[] myLock = {1};
                //获取数据
                ActionListener listener = new ActionListener() {
                    String[] choices = { "剪刀", "石头", "布"};
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(myLock[0]==1){
                            myLock[0]=0;
                            SwingUtilities.invokeLater(() -> {
                                statusLabel0State.setText("等待好友出拳中...");
                                statusLabel[0].setText("");
                                statusLabel0State.validate();
                                statusLabel0State.repaint();
                            });

                            SwingWorker<MyMessage, Void> worker = new SwingWorker<MyMessage, Void>() {
                                int userChoice = -1;

                                @Override
                                protected MyMessage doInBackground() {
                                    //获取自己的输入
                                    if(e.getSource()==rockButton){userChoice=1;}
                                    else if(e.getSource()==paperButton){userChoice=2;}
                                    else if(e.getSource()==scissorsButton){userChoice=0;}
                                    //获取好友的输入
                                    MyMessage myMessageSend = new MyMessage();
                                    myMessageSend.MessageAbout=MyMessage.AskForGame2;
                                    myMessageSend.game2Message.state= Game2Message.Game2Message_ConnectWithGameResult;
                                    myMessageSend.game2Message.ScissorsStoneCloth=userChoice;
                                    return SendAndGet(myMessageSend);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        MyMessage myMessageGet = get();
                                        int friendChoose =myMessageGet.game2Message.friendScissorsStoneCloth;
                                        //呈现结果
                                        String show = null;
                                        if(friendChoose==userChoice){
                                            show="平局! 你的好友也选择出了" + choices[friendChoose];
                                        } else if((friendChoose+1)%3==userChoice){
                                            show="你赢了! 你的好友选择出了" + choices[friendChoose];
                                        }else if((friendChoose-1+3)%3==userChoice){
                                            show="你输了! 你的好友选择出了" + choices[friendChoose];
                                        }
                                        statusLabel[0].setText(choices[friendChoose]);
                                        JOptionPane.showMessageDialog(game2Frame, show, "结果", JOptionPane.PLAIN_MESSAGE);
                                        myLock[0]=1;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            worker.execute();
                        }
                    }
                };
                rockButton.addActionListener(listener);paperButton.addActionListener(listener);scissorsButton.addActionListener(listener);

                game2Frame.setVisible(true);
            }

        }

        //石头剪刀布 小游戏  人机算法版本
        public class ScissorsStoneClothWithComputer {
            public JFrame game1Frame;

            public ScissorsStoneClothWithComputer(){
                s1=new SuanFa1();s2=new SuanFa2();s3=new SuanFa3();
            }

            //实现game1Frame
            public void playGame() {
                game1Frame = new JFrame("小游戏——剪刀石头布（人机）");
                game1Frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                game1Frame.setSize(300, 200);
                JPanel panel = new JPanel();  // 创建一个新的JPanel面板
                game1Frame.add(panel);  // 将面板添加到窗口中
                panel.setLayout(null);  // 设置面板布局为null

                //关闭窗口，结束程序
                game1Frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.out.println("game1_frame window ending...");
                        // 然后关闭窗口
                        game1Frame.dispose();
                    }
                });

                JLabel promptLabel = new JLabel("请选择你的出拳：");promptLabel.setBounds(10, 20, 150, 25);
                panel.add(promptLabel);
                //0剪刀、1石头、2布：三个按钮
                JButton scissorsButton = new JButton("剪刀");scissorsButton.setBounds(180, 50, 60, 25);
                panel.add(scissorsButton);
                JButton rockButton = new JButton("石头");rockButton.setBounds(40, 50, 60, 25);
                panel.add(rockButton);
                JButton paperButton = new JButton("布");paperButton.setBounds(115, 50, 60, 25);
                panel.add(paperButton);
                //三个标签
                JLabel[]nop = new JLabel[3];
                nop[0] = new JLabel("算法1：");nop[0].setBounds(10, 80, 50, 25);
                panel.add(nop[0]);
                nop[1] = new JLabel("算法2：");nop[1].setBounds(10, 110, 50, 25);
                panel.add(nop[1]);
                nop[2] = new JLabel("算法3：");nop[2].setBounds(10, 140, 50, 25);
                panel.add(nop[2]);
                //三个数据显示框
                JLabel[]statusLabel = new JLabel[3];
                statusLabel[0] = new JLabel("");statusLabel[0].setBounds(60, 80, 150, 25);
                panel.add(statusLabel[0]);
                statusLabel[1] = new JLabel("");statusLabel[1].setBounds(60, 110, 150, 25);
                panel.add(statusLabel[1]);
                statusLabel[2] = new JLabel("");statusLabel[2].setBounds(60, 140, 150, 25);
                panel.add(statusLabel[2]);
                //对游戏结果进行必要的操作
                double quanzhong[]=new double[]{0.33333,0.33333,0.33334};
                ActionListener listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int userChoice = -1;
                        String[] choices = { "剪刀", "石头", "布"};
                        if(e.getSource()==rockButton){
                            userChoice=1;
                        }else if(e.getSource()==paperButton){
                            userChoice=2;
                        }else if(e.getSource()==scissorsButton){
                            userChoice=0;
                        }
                        ArrayList<Integer> get_data=get(userChoice);
                        double jieguo[]=new double[]{0,0,0};
                        for(int kkptemp=0;kkptemp<3;kkptemp++){
                            jieguo[get_data.get(kkptemp)]+=quanzhong[kkptemp];
                        }
                        int max=jieguo[0]>jieguo[1]?0:1;max=jieguo[max]>jieguo[2]?max:2;
                        String show = null;
                        if(max==userChoice){
                            show="平局! 电脑也选择出了" + choices[max];
                        } else if((max+1)%3==userChoice){
                            show="你赢了! 电脑选择出了" + choices[max];
                        }else if((max-1+3)%3==userChoice){
                            show="你输了! 电脑选择出了" + choices[max];
                        }
                        JOptionPane.showMessageDialog(game1Frame, show, "结果", JOptionPane.PLAIN_MESSAGE);
                        for(int tempi=0;tempi<3;tempi++){
                            if(get_data.get(tempi)==userChoice){
                                statusLabel[tempi].setText("平局! 电脑也出了" + choices[userChoice]);
                                quanzhong[tempi]-=0.02;
                            } else if((get_data.get(tempi)+1)%3==userChoice){
                                statusLabel[tempi].setText("你赢了! 电脑出了" + choices[get_data.get(tempi)]);
                                quanzhong[tempi]-=0.08;
                            }else if((get_data.get(tempi)-1+3)%3==userChoice){
                                statusLabel[tempi].setText("你输了! 电脑出了" + choices[get_data.get(tempi)]);
                                quanzhong[tempi]+=0.10;
                            }
                            quanzhong[tempi]=quanzhong[tempi]<=0?0.02:quanzhong[tempi];
                        }
                        double zong=quanzhong[0]+quanzhong[1]+quanzhong[2];
                        quanzhong[0]=quanzhong[0]/zong;quanzhong[1]=quanzhong[1]/zong;quanzhong[2]=quanzhong[2]/zong;
                    }
                };
                rockButton.addActionListener(listener);paperButton.addActionListener(listener);scissorsButton.addActionListener(listener);

                game1Frame.setVisible(true);
            }

            private ArrayList<Integer> dataLog=new ArrayList<>();//0剪刀、1石头、2布

            private SuanFa1 s1;SuanFa2 s2;SuanFa3 s3;

            //0剪刀、1石头、2布,返回三个算法的结果
            private ArrayList<Integer> get(int shuru){
                ArrayList<Integer> ret = new ArrayList<>();
                FutureTask<Integer> futureTask1 = new FutureTask<Integer>(s1);new Thread(futureTask1).start();
                FutureTask<Integer> futureTask2 = new FutureTask<Integer>(s2);new Thread(futureTask2).start();
                FutureTask<Integer> futureTask3 = new FutureTask<Integer>(s3);new Thread(futureTask3).start();
                try {
                    ret.add(futureTask1.get());ret.add(futureTask2.get());ret.add(futureTask3.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                s2.setShangci(shuru);s3.setShangCi(shuru);
                dataLog.add(shuru);
                return ret;
            }
            //1/3算法
            class SuanFa1 implements Callable<Integer> {
                @Override
                public Integer call() throws Exception {
                    int randomNum = (int)(Math.random() * 9999);
                    return randomNum%3;
                }
            }
            //上次用户选择石头，本次系统出布，类似出其他的
            class SuanFa2 implements Callable<Integer>{
                int shangci=-1;
                @Override
                public Integer call() throws Exception {
                    if (shangci == -1) {
                        int randomNum = (int)(Math.random() * 9999);
                        return randomNum%3;
                    }
                    else {
                        return (shangci+1)%3;
                    }
                }
                public void setShangci(int s){
                    shangci=s;
                }
            }
            //依据用户选择历史，如果他上次出石头，下次出那种的概率高，就选择获胜的那个出发；
            class SuanFa3 implements Callable<Integer>{
                double[][]data=new double[3][3];
                int[]shangci=new int[2];
                SuanFa3(){
                    for(int i=0;i<3;i++){
                        for (int j=0;j<3;j++){
                            data[i][j]=3.33333;
                        }
                    }
                    shangci[0]=shangci[1]=-1;
                }
                @Override
                public Integer call() throws Exception {
                    if(shangci[1]==-1||(data[shangci[1]][0]==data[shangci[1]][1]&&data[shangci[1]][2]==data[shangci[1]][1])){
                        int randomNum = (int)(Math.random() * 9999);
                        return randomNum%3;
                    }
                    else{
                        int max=data[shangci[1]][1]>data[shangci[1]][0]?1:0;
                        max=data[shangci[1]][2]>data[shangci[1]][max]?2:max;
                        return (max+1)%3;
                    }
                }
                public void setShangCi(int s){
                    shangci[0]=shangci[1];
                    shangci[1]=s;
                    if(shangci[0]==-1){
                        return;
                    }
                    double zong=data[shangci[0]][0]+data[shangci[0]][1]+data[shangci[0]][2]+0.2;
                    for(int i=0;i<3;i++){
                        if(i==s){
                            data[shangci[0]][i]=(data[shangci[0]][i]+0.2)/zong;
                        }
                        else{
                            data[shangci[0]][i]=(data[shangci[0]][i])/zong;
                        }
                    }
                }
            }
        }

    }



    //读取文件的caozuoshu信息 和 token 或者名字 信息
    UserInCustomer(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(CustomerConstData.customer_file));
            String line;
            while ((line = bufferedReader.readLine())!= null) {
                customerBeforeLogin.caozuoshu=line;
                break;
            }
            if("0".equals(customerBeforeLogin.caozuoshu)) {
                while ((line = bufferedReader.readLine())!= null) {
                    customerBeforeLogin.token=line;
                    break;
                }
                customerBeforeLogin.S_Name="";
            }else if("1".equals(customerBeforeLogin.caozuoshu)){
                while ((line = bufferedReader.readLine())!= null) {
                    customerBeforeLogin.S_Name=line;
                    break;
                }
            }else if("2".equals(customerBeforeLogin.caozuoshu)){
                customerBeforeLogin.S_Name="";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(customerBeforeLogin.caozuoshu+"\n"+customerBeforeLogin.token);
        try {
            socket=new Socket(CustomerConstData.server_IpAddress,CustomerConstData.server_Post);
            objin=new ObjectInputStream(socket.getInputStream());
            objout=new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "连接服务器失败！");
        }
        customerBeforeLogin.login();

    }


    private Socket socket;
    private ObjectInputStream objin=null;
    private ObjectOutputStream objout=null;
    private MyMessage SendAndGet(MyMessage message){
        MyMessage get = null;
        try {
            objout.writeObject(message);
            System.out.println("SendAndGet_writeObject");
            get = (MyMessage) objin.readObject();
            System.out.println("SendAndGet_readObject");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("客户端与服务端的沟通异常！");
            e.printStackTrace();
        }
        return get;
    }

}

class test{
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        new UserInCustomer();
    }

}
