#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/select.h>
#include <sys/prctl.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <signal.h>
#include <pthread.h>
#include <errno.h>
#include <syslog.h>
#include <semaphore.h>
#include "config.h"
#include "wcy_info.h"


sem_t sem_server_log;//保证日志输出的有序性
/**
 * 添加日志信息到server.log中，通过printf实现，
 * 同时打印strerror(errno)方便服务器根据日志纠错，无错可以忽略该信息
 * @param dat 想要记录的日志信息
 */
void add_log_server(char*dat){
    sem_wait(&sem_server_log);
    printf("服务器名字: %s\n",config->server_name);
    printf("服务器版本: %s\n",config->server_version);
    printf("时间: %s\n",getTimeString());
    printf("操作信息: %s\n",dat);
    printf("strerror(errno): %s\n\n\n",strerror(errno));
    fflush(stdout);
    sem_post(&sem_server_log);
}

//服务器内存中存储的用户信息的结构体
struct Server_User{
    char user_name[100];
    char user_secret[100];
    char user_fifo[200];//可以修改——find_data,change_data控制
    int online_state;//可以修改——find_data,change_data控制
    sem_t failure_file;//控制用户代表的发生失败文件信息的一致性
    sem_t log_file;//控制用户代表的日志文件信息的一致性
};
struct Server_User*reg_user;//访问权限——由find_data控制
int register_number=0;//可以修改——find_data,change_data控制
int online_number=0;//可以修改——find_data,change_data控制
sem_t find_data,change_data;//信号量，控制并行数据的一致性
sem_t sem_fail_temp_file;//临时文件fail_temp_file的使用

/**
 * 初始化维护用户数据
 * 生成10个find_data，1个change_data，
 * 1个sem_fail_temp_file，1个sem_server_log
 */
void Server_User_Init(){
    reg_user=malloc(sizeof(struct Server_User)*config->max_reg_users);
    // 检查内存分配是否成功
    if (reg_user == NULL) {
        add_log_server("EXIT: Server_User_Init——Failed to allocate memory for registered users");
        exit(1);
    }
    // 初始化其他全局变量
    register_number = 0;
    online_number = 0;
    if(sem_init(&find_data,0,10)<0){
        add_log_server("EXIT: Server_User_Init——sem_init find_data error");
        exit(1);
    }
    if(sem_init(&change_data,0,1)<0){
        add_log_server("EXIT: Server_User_Init——sem_init change_data error");
        exit(1);
    }
    if(sem_init(&sem_fail_temp_file,0,1)<0){
        add_log_server("EXIT: Server_User_Init——sem_init sem_fail_temp_file error");
        exit(1);
    }
    if(sem_init(&sem_server_log,0,1)<0){
        add_log_server("EXIT: Server_User_Init——sem_init sem_server_log error");
        exit(1);
    }
}
/**
 * 注册用户
 * @param 信号量 10个find_data和1个change_data
 * @return 用户不存在，返回1，并将信息添加;用户存在，返回0
 */
int add_user(char *my_name,char*my_secret,char*fifo){
    sem_wait(&find_data);
    for(int i=0;i<register_number;i++)
        if(strcmp(reg_user[i].user_name,my_name)==0){
            sem_post(&find_data);
            return 0;
        }
    sem_post(&find_data);

    sem_wait(&change_data);
    for(int i=0;i<10;i++) sem_wait(&find_data);
    strcpy(reg_user[register_number].user_name,my_name);
    strcpy(reg_user[register_number].user_secret,my_secret);
    strcpy(reg_user[register_number].user_fifo,fifo);
    reg_user[register_number].online_state=0;
    if(sem_init(&reg_user[register_number].log_file,0,1)<0){
        add_log_server("sem_init error");
        exit(1);
    }
    if(sem_init(&reg_user[register_number].failure_file,0,1)<0){
        add_log_server("sem_init error");
        exit(1);
    }
    register_number++;
    for(int i=0;i<10;i++) sem_post(&find_data);
    sem_post(&change_data);
    return 1;
}
/**
 * 查询用户名和密码
 * @param 信号量 1个find_data
 * @return 正确返回[编号]; 错误返回-1
 */
int find_user(char my_name[100],char my_secret[100]){
    sem_wait(&find_data);
    for(int i=0;i<register_number;i++)
        if(strcmp(reg_user[i].user_name,my_name)==0&&strcmp(reg_user[i].user_secret,my_secret)==0){
            sem_post(&find_data);
            return i;
        }
    sem_post(&find_data);
    return -1;
}
/**
 * 查询用户名的index的编号
 * @param 信号量 1个find_data
 * @return 存在返回编号，reg_user[编号];不存在返回-1
 */
int find_index(char my_name[100]){
    sem_wait(&find_data);
    for(int i=0;i<register_number;i++)
        if(strcmp(reg_user[i].user_name,my_name)==0){
            sem_post(&find_data);
            return i;
        }
    sem_post(&find_data);
    return -1;
}
/**
 * 登录用户——如果my_name和my_secret对上了，会对user_fifo覆盖为my_fifo
 * @param 信号量 10个find_data和1个change_data
 * @return 正确返回[编号]; 用户名和密码错误，返回-1; 已经登录错误，返回-2; 登录人数超标错误，返回-3;
 */
int login_user(char my_name[100],char my_secret[100],char my_fifo[200]){
    int p=find_user(my_name,my_secret);
    if(p<0) return -1;
    
    sem_wait(&change_data);
    if(reg_user[p].online_state==1){
        sem_post(&change_data);
        return -2;
    }
    if(online_number==config->max_online_users){
        sem_post(&change_data);
        return -3;
    }
    for(int i=0;i<10;i++) sem_wait(&find_data);
    reg_user[p].online_state=1;
    strcpy(reg_user[p].user_fifo,my_fifo);
    online_number++;
    for(int i=0;i<10;i++) sem_post(&find_data);
    sem_post(&change_data);

    return p;
}
/**
 * 注销用户——用户正确就进行注销
 * @param 信号量 10个find_data和1个change_data
 */
void logout_user(char my_name[100]){
    int p=find_index(my_name);
    if(p<0||p>=register_number) return;
    sem_wait(&change_data);
    for(int i=0;i<10;i++) sem_wait(&find_data);
    if(reg_user[p].online_state==1){
        reg_user[p].online_state=0;
        online_number--;
    }
    for(int i=0;i<10;i++) sem_post(&find_data);
    sem_post(&change_data);
}
/**
 * 注销用户——用户正确就进行注销
 * @param 信号量 1个find_data
 * @return data不是参数，而是存储返回值的
 */
void get_online_data(char data[900]){
    char op[800];strcpy(op,"");
    sem_wait(&find_data);
    for(int i=0;i<register_number;i++)
        if(reg_user[i].online_state==1){
            strcat(op,reg_user[i].user_name);
            strcat(op,"\n");
        }
    snprintf(data,900,"当前有%d个人在线:\n%s",online_number,op);
    sem_post(&find_data);
}


//将未发送的信息写入接受者user_index的failure_file文件中
void save_failure_meg(int user_index,SYS_MEG meg){
    //根据user_index构造文件路径file_name
    char file_name[250];
    strcpy(file_name,config->send_fail);
    sem_wait(&find_data);
    if(user_index<0||user_index>=register_number){
        sem_post(&find_data);
        return;
    }
    strcat(file_name,reg_user[user_index].user_name);
    sem_post(&find_data);

    //写meg日志到file_name
    sem_wait(&reg_user[user_index].failure_file);
    int fd = open(file_name, O_WRONLY | O_CREAT | O_APPEND, 0600);
    if (fd < 0) {
        add_log_server("ERROR: Failed to open or create log file");
        exit(1);
    }
    write(fd,&meg,sizeof(SYS_MEG));
    close(fd);
    sem_post(&reg_user[user_index].failure_file);
}

//将信息写入sender的发送日志中,save_user_log函数是消息类型的，save_user_log_o函数是事件类型的
void save_user_log(char*e,char*sender,char*reciever,char*t,int state){
    //构造文件路径file_name
    char file_name[250];
    strcpy(file_name,config->logfiles_users);
    int user_index=find_index(sender);
    sem_wait(&find_data);
    if(user_index<0||user_index>=register_number){
        sem_post(&find_data);
        add_log_server("WARNING: save_user_log——user_index<0||user_index>=register_number");
        return;
    }
    strcat(file_name,reg_user[user_index].user_name);
    sem_post(&find_data);
    //写日志到file_name
    sem_wait(&reg_user[user_index].log_file);
    int fd = open(file_name, O_WRONLY | O_CREAT | O_APPEND, 0600);
    if (fd < 0) {
        add_log_server("ERROR: Failed to open or create log file");
        exit(1);
    }
    char temp[1000],op[10];
    if(state==0) strcpy(op,"false");
    else strcpy(op,"true");
    snprintf(temp, 1000, "%s(sender:%s, reciever:%s, %s, %s)\n\n", e, sender, reciever, t, op);
    write(fd,temp,strlen(temp));
    sem_post(&reg_user[user_index].log_file);
}
void save_user_log_o(char*e,char*user_name){
    //构造文件路径
    char file_name[250];strcpy(file_name,config->logfiles_users);
    int user_index=find_index(user_name);
    sem_wait(&find_data);
    if(user_index<0||user_index>=register_number){
        sem_post(&find_data);
        add_log_server("WARNING: save_user_log_o——user_index<0||user_index>=register_number");
        return;
    }
    strcat(file_name,reg_user[user_index].user_name);
    sem_post(&find_data);
    //写日志
    sem_wait(&reg_user[user_index].log_file);
    int fd = open(file_name, O_WRONLY | O_CREAT | O_APPEND, 0600);
    if (fd < 0) {
        add_log_server("ERROR: Failed to open or create log file");
        exit(1);
    }
    char temp[1000];
    snprintf(temp, 1000, "%s(user_name:%s, %s, %s)\n\n", e, user_name, e, getTimeString());
    write(fd,temp,strlen(temp));
    sem_post(&reg_user[user_index].log_file);
}


//仅限制为聊天消息——将数据发送给to_fd，包含对发生失败后的保存和处理，以及日志记录
void handle_send_chat(char to_fd[100],int user_index,SYS_MEG meg){
    sem_wait(&find_data);
    if(user_index<0||user_index>=register_number){
        sem_post(&find_data);
        add_log_server("WARNING: handle_send_chat——user_index<0||user_index>=register_number");
        return;
    }
    if(access(to_fd, F_OK)==-1||reg_user[user_index].online_state==0){
        save_failure_meg(user_index,meg);//发生失败信息存储
        save_user_log("未成功发送的信息",meg.sender_name,reg_user[user_index].user_name,meg.timeString,0);//发生失败的日志记录
        sem_post(&find_data);
        return;
    }
    sem_post(&find_data);

    int fifo_to = open(to_fd, O_WRONLY);
    if(fifo_to == -1){
        save_failure_meg(user_index,meg);//发生失败信息存储
        sem_wait(&find_data);
        save_user_log("未成功发送的信息",meg.sender_name,reg_user[user_index].user_name,meg.timeString,0);//发生失败的日志记录
        sem_post(&find_data);
        return;
    }
    write(fifo_to,&meg,sizeof(SYS_MEG));
    sem_wait(&find_data);
    save_user_log("成功发送的信息",meg.sender_name,reg_user[user_index].user_name,meg.timeString,1);//发生失败的日志记录
    sem_post(&find_data);
    close(fifo_to);
}

//仅限于系统通知消息——发生失败不保存，并且发生的消息不会记入用户的日志文件中
void handle_send_sysinfo(char to_fd[100],SYS_MEG meg){
    if(access(to_fd, F_OK)==-1){
        return;
    }
    int fifo_to = open(to_fd, O_WRONLY);
    if(fifo_to == -1){
        close(fifo_to);
        return;
    }
    write(fifo_to,&meg,sizeof(SYS_MEG));
    close(fifo_to);
}

//仅限于系统消息对在线用户的广播——发生失败不保存，并且发生的消息不会记入用户的日志文件中
void handle_send_sysinfo_all(SYS_MEG meg){
    sem_wait(&find_data);
    for(int i=0;i<register_number;i++)
        if(reg_user[i].online_state==1){
            handle_send_sysinfo(reg_user[i].user_fifo,meg);
        }
    sem_post(&find_data);
}


//将user_index的failure_file文件中的数据进行重发
void resend_failure_meg(int user_index){
    //构造文件路径
    char file_name[250];
    strcpy(file_name,config->send_fail);
    sem_wait(&find_data);
    if(user_index<0||user_index>=register_number){
        sem_wait(&find_data);
        add_log_server("WARNING: resend_failure_meg——user_index<0||user_index>=register_number");
        return;
    }
    strcat(file_name,reg_user[user_index].user_name);
    sem_post(&find_data);
    
    //重发
    if(access(file_name, F_OK)==-1) return;
    sem_wait(&sem_fail_temp_file);
        
        //数据拷贝
        sem_wait(&reg_user[user_index].failure_file);
        int fd_temp1 = open(config->fail_temp_file, O_WRONLY | O_CREAT | O_TRUNC, 0600);
        if (fd_temp1 == -1) {
            add_log_server("Failed to open fail_temp_file");
            return;
        }
        int fd_temp2 = open(file_name, O_RDONLY);
        if (fd_temp2 == -1) {
            add_log_server("Failed to open file_name");
            close(fd_temp1);
            return;
        }
        char buffer[1000];
        ssize_t bytesRead;
        while ((bytesRead = read(fd_temp2, buffer, 1000)) > 0) {
            if (write(fd_temp1, buffer, bytesRead) != bytesRead) {
                perror("Failed to write to fail_temp_file");
                close(fd_temp1);close(fd_temp2);
                return;
            }
        }
        if (bytesRead == -1) add_log_server("Failed to read from file_name");
        close(fd_temp1);close(fd_temp2);
        
        // 重新打开 fd_temp2 以清空文件内容
        fd_temp2 = open(file_name, O_WRONLY | O_TRUNC);
        if (fd_temp2 == -1) {
            add_log_server("Failed to truncate file_name");
            return;
        }
        close(fd_temp2);
        sem_post(&reg_user[user_index].failure_file);

        //进行重发
        fd_temp1 = open(config->fail_temp_file, O_RDONLY);
        SYS_MEG meg;
        while(read(fd_temp1,&meg,sizeof(SYS_MEG))>0){
            sem_wait(&find_data);
            handle_send_chat(reg_user[user_index].user_fifo,user_index,meg);
            sem_post(&find_data);
        }
        close(fd_temp1);

    sem_post(&sem_fail_temp_file);
}


/**
 * 多线程处理用户注册的函数
 * @param arg 注册的用户包信息User
 * @return NULL
 */
void *handle_register(void *arg)
{
    User user = *((User *)arg);
    int ret = add_user(user.name,user.secret,user.fifo);
    if(ret==0){
        SYS_MEG meg;
        strcpy(meg.info,"注册失败！用户名已被使用！");
        strcpy(meg.timeString,getTimeString());
        meg.who=WCY_SERVER_SYSTEM;
        meg.state=FALSE;
        handle_send_sysinfo(user.fifo,meg);
    }else{
        SYS_MEG meg;
        strcpy(meg.info,"注册成功！");
        strcpy(meg.timeString,getTimeString());
        meg.who=WCY_SERVER_SYSTEM;
        meg.state=SUCCESS;
        handle_send_sysinfo(user.fifo,meg);
        save_user_log_o("注册事件",user.name);
    }
    return NULL;
}

/**
 * 多线程处理用户登录的函数
 * @param arg 登录的用户包信息User
 * @return NULL
 */
void *handle_login(void *arg)
{
    User user = *((User *)arg);
    int ret = login_user(user.name,user.secret,user.fifo);
    if(ret==-1){
        SYS_MEG meg;
        strcpy(meg.info,"用户名或者密码错误！");
        strcpy(meg.timeString,getTimeString());
        meg.who=WCY_SERVER_SYSTEM;
        meg.state=FALSE;
        handle_send_sysinfo(user.fifo,meg);
    }else if(ret==-2){
        SYS_MEG meg;
        strcpy(meg.info,"该帐号已经登录！");
        strcpy(meg.timeString,getTimeString());
        meg.who=WCY_SERVER_SYSTEM;
        meg.state=FALSE;
        handle_send_sysinfo(user.fifo,meg);
    }else if(ret==-3){
        SYS_MEG meg;
        strcpy(meg.info,"在线人数过多，请稍后重试！");
        strcpy(meg.timeString,getTimeString());
        meg.who=WCY_SERVER_SYSTEM;
        meg.state=FALSE;
        handle_send_sysinfo(user.fifo,meg);
    }else{
        SYS_MEG meg;
        strcpy(meg.info,"登录成功！");
        strcpy(meg.timeString,getTimeString());
        meg.who=WCY_SERVER_SYSTEM;
        meg.state=SUCCESS;
        handle_send_sysinfo(user.fifo,meg);
        save_user_log_o("登录事件",user.name);
        /*构造群发在线用户信息*/
        char data[900];
        get_online_data(data);
        snprintf(meg.info,sizeof(meg.info),"%s登录上线\n%s",user.name,data);
        handle_send_sysinfo_all(meg);
        /*处理之前未成功发生的信息*/
        resend_failure_meg(find_index(user.name));
    }
    return NULL;
}

/**
 * 多线程处理用户注销的函数
 * @param arg 注销的用户包信息User
 * @return NULL
 */
void *handle_logout(void *arg)
{
    // 用户发送的退出信息
    User user = *((User *)arg);
    logout_user(user.name);
    SYS_MEG meg;
    strcpy(meg.info,"注销成功！");
    strcpy(meg.timeString,getTimeString());
    meg.who=WCY_SERVER_SYSTEM;
    meg.state=LOGOUT;
    handle_send_sysinfo(user.fifo,meg);
    save_user_log_o("注销事件",user.name);

    /*构造群发在线用户信息*/
    meg.state=SUCCESS;
    char data[900];
    get_online_data(data);
    snprintf(meg.info,sizeof(meg.info),"%s已经下线\n%s",user.name,data);
    handle_send_sysinfo_all(meg);

    return NULL;
}

/**
 * 多线程处理用户发送消息的函数
 * @param arg 用户包发生的信息包CHAT_INFO
 * @return NULL
 */
void *handle_sendmsg(void *arg)
{
    CHAT_INFO msg = *((CHAT_INFO *)arg);
    SYS_MEG meg;
    strcpy(meg.info,msg.info);
    strcpy(meg.timeString,getTimeString());
    strcpy(meg.sender_name,msg.from);
    meg.who=YOUR_FRIEND;
    meg.state=SUCCESS;
    for(int i=0;i<msg.to_num;i++){
        int index=find_index(msg.to[i]);
        if(index==-1) continue;
        else{
            sem_wait(&find_data);
            if(strcmp(reg_user[index].user_name,meg.sender_name)==0){
                sem_post(&find_data);
                continue;
            }
            handle_send_chat(reg_user[index].user_fifo,index,meg);
            sem_post(&find_data);
        } 
    }
    return NULL;
}




//打开四个管道——打开后这几个全局变量都保持不变
int fifo_fd_resgister,fifo_fd_login,fifo_fd_chat, fifo_fd_logout;
//打开四个管道，设置fifo_fd_resgister,fifo_fd_login,fifo_fd_chat,fifo_fd_logout
void create_four_fifo(){
    /* 创建四个公共管道 */
    if (access (config->register_fifo, F_OK) == -1) {
        int res = mkfifo (config->register_fifo, 0700) ;
        if (res != 0) {
            char buffer[400];
            snprintf(buffer, 400, "EXIT: create_four_fifo——FIFO %s was not created. %s", config->register_fifo, strerror(errno));
            add_log_server(buffer);
            exit (1);
        }
    }
    if (access (config->login_fifo, F_OK) == -1) {
        int res = mkfifo (config->login_fifo, 0700) ;
        if (res != 0) {
            char buffer[400];
            snprintf(buffer, 400, "EXIT: create_four_fifo——FIFO %s was not created. %s", config->login_fifo, strerror(errno));
            add_log_server(buffer);
            exit (1);
        }
    }
    if (access (config->msg_fifo, F_OK) == -1) {
        int res = mkfifo (config->msg_fifo, 0700) ;
        if (res != 0) {
            char buffer[400];
            snprintf(buffer, 400, "EXIT: create_four_fifo——FIFO %s was not created. %s", config->msg_fifo, strerror(errno));
            add_log_server(buffer);
            exit (1);
        }
    }
    if (access (config->logout_fifo, F_OK) == -1) {
        int res = mkfifo (config->logout_fifo, 0700) ;
        if (res != 0) {
            char buffer[400];
            snprintf(buffer, 400, "EXIT: create_four_fifo——FIFO %s was not created. %s", config->logout_fifo, strerror(errno));
            add_log_server(buffer);
            exit (1);
        }
    }

    /* 打开四个公共管道 */
    fifo_fd_resgister = open(config->register_fifo, O_RDONLY|O_NONBLOCK);    
    if(fifo_fd_resgister == -1){
        char buffer[400];
        snprintf(buffer, 400, "EXIT: create_four_fifo——Could not open %s for read only access. %s", config->register_fifo, strerror(errno));
        add_log_server(buffer);
        exit (1);
    }
    fifo_fd_login = open(config->login_fifo, O_RDONLY|O_NONBLOCK);
    if(fifo_fd_login == -1){
        char buffer[400];
        snprintf(buffer, 400, "EXIT: create_four_fifo——Could not open %s for read only access. %s", config->login_fifo, strerror(errno));
        add_log_server(buffer);
        exit (1);
    }
    fifo_fd_chat = open(config->msg_fifo, O_RDONLY|O_NONBLOCK);
    if(fifo_fd_chat == -1){
        char buffer[400];
        snprintf(buffer, 400, "EXIT: create_four_fifo——Could not open %s for read only access. %s", config->msg_fifo, strerror(errno));
        add_log_server(buffer);
        exit (1);
    }
    fifo_fd_logout = open(config->logout_fifo, O_RDONLY|O_NONBLOCK);
    if(fifo_fd_chat == -1){
        char buffer[400];
        snprintf(buffer, 400, "EXIT: create_four_fifo——Could not open %s for read only access. %s", config->logout_fifo, strerror(errno));
        add_log_server(buffer);
        exit (1);
    }
}


//监听处理四个命名管道发送过来的请求
void listen(){
    create_four_fifo();//打开四个管道
    Server_User_Init();//初始化维护用户数据
    add_log_server("START: server is success listening!");

    //创建select多路复用的位图
    fd_set read_fd_set;
    FD_ZERO(&read_fd_set);
    FD_SET(fifo_fd_resgister, &read_fd_set);
    FD_SET(fifo_fd_login, &read_fd_set);
    FD_SET(fifo_fd_chat, &read_fd_set);
    FD_SET(fifo_fd_logout, &read_fd_set);
    int max_fds = fifo_fd_resgister>fifo_fd_login?fifo_fd_resgister:fifo_fd_login;
    max_fds=max_fds>fifo_fd_chat?max_fds:fifo_fd_chat;
    max_fds=max_fds>fifo_fd_logout?max_fds:fifo_fd_logout;

    // 设置线程属性，设置为分离状态，线程结束后自动回收资源
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr,PTHREAD_CREATE_DETACHED);

    //处理select的后台的文件描述符的请求
    while (1){
        fd_set temp_set=read_fd_set;
        int select_fd = select(max_fds+1, &temp_set, NULL, NULL, NULL);
        
        if(select_fd==-1){
            add_log_server("EXIT: error select return!");
            exit(1);
        }else if(select_fd==0){
            continue;
        }

        if (FD_ISSET(fifo_fd_resgister, &temp_set)){
            User user;
            if(read(fifo_fd_resgister, &user, sizeof(user))>0){
                add_log_server("register");
                pthread_t thread_id;
                pthread_create(&thread_id, &attr, handle_register, (void *)(&user));
            }
        }
        if (FD_ISSET(fifo_fd_login, &temp_set)){
            User user;
            if(read(fifo_fd_login, &user, sizeof(user))>0){
                add_log_server("login");
                pthread_t thread_id;
                pthread_create(&thread_id, &attr, handle_login, (void *)(&user));
            }
        }
        if (FD_ISSET(fifo_fd_logout, &temp_set)){
            User user;
            if(read(fifo_fd_logout, &user, sizeof(user))>0){
                add_log_server("logout");
                pthread_t thread_id;
                pthread_create(&thread_id, &attr, handle_logout, (void *)(&user));
            }
        }
        if (FD_ISSET(fifo_fd_chat, &temp_set)){
            CHAT_INFO meg;
            if(read(fifo_fd_chat, &meg, sizeof(meg))>0){
                add_log_server("chat");
                pthread_t thread_id;
                pthread_create(&thread_id, &attr, handle_sendmsg, (void *)(&meg));
            }   
        }
    }
    add_log_server("EXIT: listen——error exit!");
}


//创建守护进程，将输出挂载到日志文件server.log中
void create_daemon()
{
    //创建子进程，父进程退出
    pid_t pid=fork();
    if(pid==-1){
        perror("create_daemon fork1");
        exit(1);
    }
    else if(pid>0){
        exit(0);
    }

    //创建新的会话，完全脱离控制终端
    pid_t sid=setsid();
    if(sid==-1){
        perror("create_daemon setsid");
        exit(1);
    }

    //再次调用fork, 父进程exit，主要是为了确保进程无法通过open /dev/tty 再次获得终端,
    //因为调用open时, 系统会默认为会话首进程创建控制终端
    pid=fork();
    if(pid==-1){
        perror("create_daemon fork2");
        exit(1);
    }
    else if(pid>0){
        exit(0);
    }

    //改变当前工作目录为根目录
    if(chdir("/")==-1){
        perror("create_daemon chdir");
        exit(1);
    }

    //设置权限掩码
    umask(0);

    //命名守护进程,将进程名修改为规定的格式
    if (prctl(PR_SET_NAME, config->server_name, NULL, NULL, NULL) != 0) {
        perror("Failed to set process name");
        exit(1);
    }

    //忽略SIGHUP信号，防止守护进程因终端关闭而被意外终止
    //忽略SIGCHLD信号，系统会自动清理子进程资源，防止僵尸进程的产生
    //忽略SIGTERM信号，防止守护进程被意外终止
    if (signal(SIGHUP, SIG_IGN) == SIG_ERR) {
        perror("Failed to ignore SIGHUP signal");
        exit(1);
    }
    if (signal(SIGCHLD, SIG_IGN) == SIG_ERR) {
        perror("Failed to ignore SIGCHLD signal");
        exit(1);
    }
    if (signal(SIGTERM, SIG_IGN) == SIG_ERR) {
        perror("Failed to ignore SIGTERM signal");
        exit(1);
    }

    //关闭文件描述符，重定向标准输入,标准错误到/dev/null, 标准输出重定向到服务器日志文件中
    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    close(STDERR_FILENO);
    open("/dev/null",O_RDONLY);// STDIN_FILENO
    open(config->logfiles_server, O_WRONLY | O_CREAT | O_APPEND, 0600);// STDOUT_FILENO
    open("/dev/null",O_WRONLY);// STDERR_FILENO
}

int main(){
    load_config();//读取配置文件
    create_daemon();//创建守护进程
    listen();//监听函数
    return 0;
}

