#ifndef CONFIG_H
#define CONFIG_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LEN 256
//config.ini文件放置的路径
#define CONFIG_FILE "./config.ini"

//从配置文件中读取的数据存储在Config中
typedef struct{
    //[Server Information]
    char server_name[MAX_LEN];      // 服务器名字
    char server_version[MAX_LEN];   // 服务器版本
    
    //[Pipeline]
    char all_fifo[MAX_LEN/4];       // 存储命名管道的文件夹
    char register_fifo[MAX_LEN];    // 注册管道的路径
    char login_fifo[MAX_LEN];       // 登录管道的路径
    char msg_fifo[MAX_LEN];         // 发送消息的管道的路径
    char logout_fifo[MAX_LEN];      // 退出登录管道的路径
    
    //[Log File]
    char log_files[MAX_LEN];        // 存放日志文件的目录
    char logfiles_server[MAX_LEN];  // 存放服务器日志文件的目录
    char logfiles_users[MAX_LEN];   // 存放客户端日志文件的目录

    //[Administer Information]
    char send_fail[MAX_LEN];        // 发生失败的信息
    char fail_temp_file[MAX_LEN];   // 存储用户信息的文件

    //[Server Settings]
    int max_online_users;           // 最大在线人数
    int max_reg_users;              // 最大注册的用户数量
} Config;
Config*config;

//加载CONFIG_FILE文件的内容到config对象中
void load_config();

//获取时间的函数
char* getTimeString();

#endif //CONFIG_H