/* wcy_info.h */

#ifndef _CLIENTINFO_H
#define _CLIENTINFO_H

//发生给服务器的聊天消息
typedef struct {
    char from[100];
    char info[1000];
    char to[20][100];
    int to_num;
}CHAT_INFO;
int add_CHAT_INFO_friend(char* friend_name,CHAT_INFO *ret){
    if(strlen(friend_name)<=100&&ret->to_num<20){
        strcpy(ret->to[ret->to_num++],friend_name);
        return 1;
    }
    return -1;
}
void print_CHAT_INFO(CHAT_INFO*p){
    printf("CHAT_INFO——\n");
    for(int i=0;i<p->to_num;i++){
        printf("friend_name:%s\n",p->to[i]);
    }
    printf("my_name:%s\n info:%s\n",p->from,p->info);
}


//注册，登录，注销的信息
typedef struct {
    char name[100];
    char secret[100];
    char fifo[200];
}User;
int get_User(char* my_name,char* my_secret,char *my_fifo, User *ret){
    if(strlen(my_name)>=100||strlen(my_secret)>=100||strlen(my_fifo)>=200){
        return -1;
    }else{
        strcpy(ret->name,my_name);
        strcpy(ret->secret,my_secret);
        strcpy(ret->fifo,my_fifo);
        return 1;
    }
}


//服务器发送给用户的信息
typedef enum {
    WCY_SERVER_SYSTEM,
    YOUR_FRIEND
} WHO;
typedef enum {
    SUCCESS,
    FALSE,
    LOGOUT
} STATE;
typedef struct {
    char sender_name[100];
    char info[1000];
    char timeString[100];
    WHO who;
    STATE state;
} SYS_MEG;

#endif


