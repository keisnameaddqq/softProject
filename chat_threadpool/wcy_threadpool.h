#ifndef WCY_THREADPOOL_H
#define WCY_THREADPOOL_H

#include <signal.h>

// 任务结构体
typedef struct TP_Task{
    void* (*function)(void*);   // 运行函数
    void* arg;                  // 参数
    struct TP_Task* next;              // 下一个任务
}TP_Task;

typedef struct {
    char log_file[256];         // 存放日志的文件路径
    TP_Task* que_front;         // 任务队列头指针
    TP_Task *que_rear;          // 任务队列尾指针
    int pthread_num;            // 线程数量
    pthread_t *thread_id;       // 线程号
    pthread_mutex_t mutex;      // 互斥锁
    pthread_cond_t cond;        // 条件变量
    int shutdown;               // 线程池状态 0为开，1为关
} Thread_Pool;


//为pool的log_files添加日志记录，需要使用pool->mutex锁
void add_to_log(char*buf,Thread_Pool* pool);
//创建线程池并返回，创建失败返回NULL
Thread_Pool* create_thread_pool(int pthread_num,char*log_file);
//任务线程，负责从任务队列中获取任务并执行
void*worker(void*arg);
//往任务队列添加任务，并唤醒任务线程
void thread_pool_add(Thread_Pool* pool,void*(*func)(void*),void*arg);
//销毁线程池
void threead_pool_destory(Thread_Pool* pool);


#endif //WCY_THREADPOOL_H