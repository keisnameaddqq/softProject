#include "config.h"
#include <time.h>

char* getTimeString() {
    time_t t;
    time(&t);
    char* timeStr = ctime(&t);
    int len = strlen(timeStr);
    if (timeStr[len - 1] == '\n')
        timeStr[len - 1] = '\0'; // 替换换行符为字符串结束符
    return timeStr;
}

void load_config() {
    config=(Config*)malloc(sizeof(Config));
    FILE *file = fopen(CONFIG_FILE, "r");
    if (file == NULL) {
        perror("Error opening config file");
        exit(1);
    }
    char line[256];
    while (fgets(line, sizeof(line), file) != NULL) {
        
        // 忽略空白行和以";"和"["开头的行
        if (line[0] == ';' || line[0] == '\n' || line[0] == '\r' || line[0] == '[')
            continue;
        
        char key[50], value[256];
        // 读取并给相应属性赋值。
        if (sscanf(line, "%49[^=]=%255[^\n]", key, value) == 2) {
            //[Server Information]
            if (strcmp(key, "SERVER_NAME") == 0)
                snprintf(config->server_name, sizeof(config->server_name), "%s", value);
            else if (strcmp(key, "SERVER_VERSION") == 0)
                snprintf(config->server_version, sizeof(config->server_version), "%s", value);
            
            //[Pipeline]
            else if (strcmp(key, "ALL_FIFO") == 0)
                snprintf(config->all_fifo, sizeof(config->all_fifo), "%s", value);
            else if (strcmp(key, "REG_FIFO") == 0)
                snprintf(config->register_fifo, sizeof(config->register_fifo), "%s", value);
            else if (strcmp(key, "LOGIN_FIFO") == 0)
                snprintf(config->login_fifo, sizeof(config->login_fifo), "%s", value);
            else if (strcmp(key, "MSG_FIFO") == 0)
                snprintf(config->msg_fifo, sizeof(config->msg_fifo), "%s", value);
            else if (strcmp(key, "LOGOUT_FIFO") == 0)
                snprintf(config->logout_fifo, sizeof(config->logout_fifo), "%s", value);
            
            //[Log File]
            else if (strcmp(key, "LOGFILES") == 0)
                snprintf(config->log_files, sizeof(config->log_files), "%s", value);
            else if (strcmp(key, "LOGFILES_SERVER") == 0){
                snprintf(config->logfiles_server, sizeof(config->logfiles_server), "%s", value);
                strcat(config->logfiles_server, "server.log");
            }
            else if (strcmp(key, "LOGFILES_USERS") == 0)
                snprintf(config->logfiles_users, sizeof(config->logfiles_users), "%s", value);
            else if (strcmp(key, "THREAD_POOL") == 0)
                snprintf(config->log_threadpool, sizeof(config->log_threadpool), "%s", value);
            
            //[Administer Information]
            else if (strcmp(key, "SEND_FAIL") == 0)
                snprintf(config->send_fail, sizeof(config->send_fail), "%s", value);            
            else if (strcmp(key, "FAIL_TEMP_FILE") == 0)
                snprintf(config->fail_temp_file, sizeof(config->fail_temp_file), "%s", value);
            
            //[Server Settings]
            else if (strcmp(key, "MAX_ONLINE_USERS") == 0)
                config->max_online_users = atoi(value);
            else if (strcmp(key, "MAX_REG_USERS") == 0)
                config->max_reg_users = atoi(value);
            else if (strcmp(key, "POOLSIZE") == 0)
                config->threadpool_size = atoi(value);
            
        }
    }
    fclose(file);
}