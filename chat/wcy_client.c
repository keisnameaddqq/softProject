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

/**
 * 输出规范化的错误信息
 * @param dat 为问题信息
 * @return
 */
void print_error(char*dat){
    printf("\n客户端名字：%s\n",config->server_name);
    printf("客户端版本：%s\n",config->server_version);
    printf("时间：%s\n",getTimeString());
    printf("问题：%s\n",dat);
    printf("errno:%s\n\n\n",strerror(errno));
    fflush(stdout);
}

//打开四个管道——打开后这几个全局变量都保持不变
int fifo_fd_resgister,fifo_fd_login,fifo_fd_chat,fifo_fd_logout;
/**
 * 打开四个管道，设置fifo_fd_resgister,fifo_fd_login,fifo_fd_chat,fifo_fd_logout
 */
void create_four_fifo(){
    /* 创建四个公共管道 */
    if (access (config->register_fifo, F_OK) == -1) {
        char buffer[300];
        snprintf(buffer, 300, "EXIT: FIFO %s was not created.", config->register_fifo);
        print_error(buffer);
        exit (1);
    }
    if (access (config->login_fifo, F_OK) == -1) {
        char buffer[300];
        snprintf(buffer, 300, "EXIT: FIFO %s was not created.", config->login_fifo);
        print_error(buffer);
        exit (1);
    }
    if (access (config->msg_fifo, F_OK) == -1) {
        char buffer[300];
        snprintf(buffer, 300, "EXIT: FIFO %s was not created.", config->msg_fifo);
        print_error(buffer);
        exit (1);
    }
    if (access (config->logout_fifo, F_OK) == -1) {
        char buffer[300];
        snprintf(buffer, 300, "EXIT: FIFO %s was not created.", config->logout_fifo);
        print_error(buffer);
        exit (1);
    }

    /* 打开四个公共管道 */
    fifo_fd_resgister = open(config->register_fifo, O_WRONLY);
    if(fifo_fd_resgister == -1){
        char buffer[300];
        snprintf(buffer, 300, "EXIT: Could not open %s for read only access. %s", config->register_fifo, strerror(errno));
        print_error(buffer);
        exit (1);
    }
    fifo_fd_login = open(config->login_fifo, O_WRONLY );
    if(fifo_fd_login == -1){
        char buffer[300];
        snprintf(buffer, 300, "EXIT: Could not open %s for read only access. %s", config->login_fifo, strerror(errno));
        print_error(buffer);
        exit (1);
    }
    fifo_fd_chat = open(config->msg_fifo, O_WRONLY );
    if(fifo_fd_chat == -1){
        char buffer[300];
        snprintf(buffer, 300, "EXIT: Could not open %s for read only access. %s", config->msg_fifo, strerror(errno));
        print_error(buffer);
        exit (1);
    }
    fifo_fd_logout = open(config->logout_fifo, O_WRONLY );
    if(fifo_fd_chat == -1){
        char buffer[300];
        snprintf(buffer, 300, "EXIT: Could not open %s for read only access. %s", config->logout_fifo, strerror(errno));
        print_error(buffer);
        exit (1);
    }
}

//存储用户的信息——在登录成功后就会一直保持不变
User client;
//打开专属管道的文件描述符——在登录成功后就会一直打开，由receiveMsg进行对fifo的删除
int client_fd;
//控制shell的输出的有序性，避免发生信息的显示被打断——由main进行创建和删除
sem_t content_sem;

/**
 * 完成用户注册的功能，在注册时采用随机生成的临时管道进行通信，用完即刻删除
 */
void Init_Register(){
    //生产随机的临时的命名管道
    do{
        srand(time(0));
        snprintf(client.fifo,sizeof(client.fifo),"%s%d%d",config->all_fifo,rand(),rand());
    }while (access(client.fifo, F_OK) != -1);
    if (mkfifo(client.fifo, 0777) != 0) {
        print_error("创建临行管道失败！");
        exit(1);
    }

    printf("\n\n欢迎进行注册！\n");
    printf("输入用户名(不要超过100字符): ");
    scanf("%s",client.name);
    printf("输入密码(不要超过100字符): ");
    scanf("%s",client.secret);

    if(write(fifo_fd_resgister,&client,sizeof(User))<=0){
        print_error("Init_Register——write Error");
        exit(1);
    }
    SYS_MEG meg;
    int fd = open(client.fifo, O_RDONLY);
    if(read(fd,&meg,sizeof(SYS_MEG))>0&&meg.state==SUCCESS){
        printf("注册成功！\n");
    }else{
        printf("注册失败: %s\n",meg.info);
    }
    close(fd);
    unlink(client.fifo);
}

/**
 * 完成用户登录的功能，根据输入的用户名打开对应的专属命名管道，若是登录成功，会将client_fd一并设置了
 */
int Init_Login(){
    printf("\n\n欢迎进行登录！\n");

    printf("输入用户名(不要超过100字符): ");
    scanf("%s",client.name);
    printf("输入密码(不要超过100字符): ");
    scanf("%s",client.secret);

    //根据输入的用户名构建专属的命名管道路径
    snprintf(client.fifo,sizeof(client.fifo),"%s%s",config->all_fifo,client.name);
    
    if(access(client.fifo, F_OK) != -1){
        print_error("ERROR: 该帐号大概率已经登录！命名管道已经存在！不允许再登录！");
        exit(1);
    }
    if (mkfifo(client.fifo, 0777) != 0) {
        print_error("Init_Register——mkfifo client.fifo error");
        exit(1);
    }
    
    //发送注册请求
    if(write(fifo_fd_login,&client,sizeof(User))<=0){
        print_error("Init_Login write Error");
        exit(1);
    }
    //等待回复，作出反应
    client_fd = open(client.fifo, O_RDONLY);
    SYS_MEG meg;
    read(client_fd,&meg,sizeof(SYS_MEG));
    if(meg.state==SUCCESS){
        printf("登录成功！\n");
        return 1;
    }else{
        printf("登录失败: %s\n",meg.info);
        close(client_fd);
        unlink(client.fifo);
        return 0;
    }
}

/**
 * 注册和登录的处理——登录成功将确定client的信息
 */
void Client_Init(){
    printf("\n\n欢迎使用%s %s软件\n",config->server_name,config->server_version);
    while(1){
        printf("\n功能(1.注册 2.登录):");
        int op,flag=0;
        scanf("%d",&op);
        // printf("op=%d\n",op);
        switch (op)
        {
            case 1:
                Init_Register();
                break;
            case 2:
                flag=Init_Login();
                break;
            default:
                printf("输入不合法！请重新输入\n");
                continue;
                break;
        }
        if(flag==1) break;
    }
}

/**
 * 处理用户聊天
 */
void Chat_Handle(){
    printf("欢迎进入聊天!\n");
    while(1){
        sem_wait(&content_sem);
        printf("\n\n功能(1发送聊天 2退出程序):");
        sem_post(&content_sem);
        int op;scanf("%d",&op);
        if(op==1){
            sem_wait(&content_sem);
            
            CHAT_INFO chat_info;
            chat_info.to_num=0;
            strcpy(chat_info.from,client.name);

            printf("输入方式消息的朋友的人数：");
            int n;scanf("%d",&n);

            printf("输入方式消息的朋友的名字：\n");
            for(int i=0;i<n;i++){
                char temp[100];
                scanf("%s",temp);
                add_CHAT_INFO_friend(temp,&chat_info);
            }

            printf("输入方式消息的内容：\n");getchar();
            fgets(chat_info.info, sizeof(chat_info.info), stdin);
            chat_info.info[strlen(chat_info.info)-1]='\0';

            write(fifo_fd_chat,&chat_info,sizeof(CHAT_INFO));

            printf("\nSend:\'%s\'\nTO:",chat_info.info);
            for(int i=0;i<n;i++){
                if(i!=0) printf(",");
                printf("%s",chat_info.to[i]);
            }
            printf("\nTime:%s\n\n",getTimeString());
            
            sem_post(&content_sem);
            
        }
        else if(op==2){
            write(fifo_fd_logout,&client,sizeof(User));
            break;
        }
        sleep(1);
    }
}


/**
 * 多线程客户端监听函数
 * @param arg 没有参数，用全局变量User client; int client_fd;进行操作
 * @return NULL
 */
void* receiveMsg(void* arg) {
    while (1) {
        SYS_MEG meg;
        int res = read(client_fd, &meg, sizeof(SYS_MEG));
        if(res==-1){
            print_error("receiveMsg——receiveMsg read Error");
            exit(1);
        }
        if (res > 0) {
            if(meg.state==LOGOUT) break;
            sem_wait(&content_sem);
            if(meg.who==WCY_SERVER_SYSTEM){
                printf("\n\nTime: %s\n[System_Note]: %s\n\n",meg.timeString,meg.info);
            }else if(meg.who==YOUR_FRIEND){
                printf("\n\nTime: %s\nReceive: %s\nFROM: %s\n\n",meg.timeString,meg.info,meg.sender_name);
            }else{
                printf("\n\nTime: %s\n[Info]: %s\n\n",meg.timeString,meg.info);
            }
            printf("\n\n功能(1发送聊天 2退出程序):");fflush(stdout);
            sem_post(&content_sem);
        }
    }
    printf("注销成功\n");
    close(client_fd);
    unlink(client.fifo);
}

int main()
{
    umask(0);
    load_config();//读取配置文件
    create_four_fifo();//打开命名管道
    Client_Init();//注册和登录的处理

    //创建信号量
    if(sem_init(&content_sem,0,1)<0){
        print_error("main——sem_init content_sem error");
        exit(1);
    }

    // 创建一个线程函数来接收消息
    pthread_t thread_id;
    pthread_create(&thread_id, NULL, receiveMsg, NULL);

    //聊天消息处理
    Chat_Handle();

    pthread_join(thread_id,NULL);

    //清除信号量
    if (sem_destroy(&content_sem) < 0) {
        print_error("main——sem_destroy content_sem error");
    }
    return 0;
}

