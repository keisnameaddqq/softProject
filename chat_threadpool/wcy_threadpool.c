#include <pthread.h>
#include <stdbool.h>
#include <stdlib.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <signal.h>
#include "wcy_threadpool.h"
#include "config.h"

//为pool的log_files添加日志记录，需要使用pool->mutex锁
void add_to_log(char*buf,Thread_Pool* pool){
    pthread_mutex_lock(&pool->mutex);
    FILE *file = fopen(pool->log_file, "a");
    if (file == NULL) {
        perror("Error opening log file");
        pthread_mutex_unlock(&pool->mutex);
        return;
    }
    fprintf(file,"\n\nThread_Pool\n时间: %s\n",getTimeString());
    fprintf(file, "事件: %s\n", buf);
    fclose(file);
    pthread_mutex_unlock(&pool->mutex);
}

//任务线程，负责从任务队列中获取任务并执行
void*worker(void*arg){
    Thread_Pool* pool=(Thread_Pool*)arg;
    while(1){
        pthread_mutex_lock(&pool->mutex);
        //如果任务队列为空 且线程池没有关闭 线程休眠
        while(pool->que_front==pool->que_rear&&pool->shutdown==0){
            pthread_cond_wait(&pool->cond,&pool->mutex);
        }
        //如果线程池关闭
        if(pool->shutdown==1){
            pthread_mutex_unlock(&pool->mutex);
            char buf[100];
            snprintf(buf,100,"线程池被关闭 线程%ld 退出...",pthread_self());
            add_to_log(buf,pool);
            pthread_exit((void*)0);
        }
        //从任务队列里获取一个任务，并执行
        TP_Task* task=pool->que_front->next;
        pool->que_front->next=pool->que_front->next->next;
        if(pool->que_front->next==NULL)
            pool->que_rear=pool->que_front;
        //释放互斥锁
        pthread_mutex_unlock(&pool->mutex);
        
        char buf[100];
        snprintf(buf,100,"线程池 线程%ld 被分派工作",pthread_self());
        add_to_log(buf,pool);
        task->function(task->arg);
        snprintf(buf,100,"线程池 线程%ld 工作完成被回收",pthread_self());
        add_to_log(buf,pool);
        
        free(task);
    }
}

//往任务队列添加任务，并唤醒任务线程
void thread_pool_add(Thread_Pool* pool,void*(*func)(void*),void*arg){
    TP_Task* task=(TP_Task*)malloc(sizeof(TP_Task));
    if(task==NULL){
        char buf[100];
        snprintf(buf,100,"ERROR: 线程池 thread_pool_add 函数中 malloc task");
        add_to_log(buf,pool);
        return;
    }
    task->function=func;
    task->arg=arg;
    task->next=NULL;

    pthread_mutex_lock(&pool->mutex);
    pool->que_rear->next=task;
    pool->que_rear=task;
    pthread_mutex_unlock(&pool->mutex);

    pthread_cond_signal(&pool->cond);
}

//销毁线程池
void threead_pool_destory(Thread_Pool* pool){
    pool->shutdown=1;
    pthread_cond_broadcast(&pool->cond);
    // for(int i=0;i<pool->pthread_num;i++){
    //     pthread_cond_signal(&pool->cond);
    // }
    while(pool->que_front!=NULL){
        TP_Task* temp=pool->que_front;
        pool->que_front=pool->que_front->next;
        free(temp);
    }
    pthread_mutex_destroy(&pool->mutex);
    pthread_cond_destroy(&pool->cond);
    free(pool);
}


//创建线程池并返回，创建失败返回NULL
Thread_Pool* create_thread_pool(int pthread_num,char*log_file){
    // 申请结构体
    Thread_Pool* pool=(Thread_Pool*)malloc(sizeof(Thread_Pool));
    if(pool==NULL){
        // printf("ERROR: malloc Thread_Pool failure!");
        return NULL;
    }
    pool->pthread_num=pthread_num;
    strcpy(pool->log_file,log_file);

    // 初始化任务队列
    pool->que_front=(TP_Task*)malloc(sizeof(TP_Task));
    if(pool->que_front==NULL){
        // printf("ERROR: malloc pool->que_front failure!");
        free(pool);
        return NULL;
    }
    pool->que_rear=pool->que_front;
    pool->que_front->next=NULL;

    //初始化线程号
    pool->thread_id=(pthread_t*)malloc(sizeof(pthread_t)*pthread_num);
    if(pool->thread_id==NULL){
        // printf("ERROR: malloc pool->thread_id failure!");
        free(pool->que_rear);
        free(pool);
        return NULL;
    }
    
    //初始化互斥锁和条件变量和标志位
    pthread_mutex_init(&pool->mutex,NULL);
    pthread_cond_init(&pool->cond,NULL);
    pool->shutdown=0;

    //初始化线程
    for(int i=0;i<pthread_num;i++){
        if(pthread_create(&pool->thread_id[i],NULL,worker,pool)!=0){
            // printf("ERROR: pthread_create worker failure!");
            free(pool->que_rear);
            free(pool->thread_id);
            pthread_mutex_destroy(&pool->mutex);
            pthread_cond_destroy(&pool->cond);
            free(pool);
            return NULL;
        }
        pthread_detach(pool->thread_id[i]);
    }
}

