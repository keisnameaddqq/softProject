// 创建一个新的Audio对象并设置声音文件的路径
var clickSound = new Audio('./js/mausklick-82774.mp3');
// 设置声音的播放长度（以秒为单位）
var duration = 0.2;  // 这里设置为0.5秒
// 为整个文档添加一个点击事件监听器
document.addEventListener('click', function () {
  // 在点击事件触发时播放声音
  clickSound.play();
  // 在设定的时间后停止播放
  setTimeout(function () {
    clickSound.pause();
    clickSound.currentTime = 0;  // 将声音文件的播放位置重置为开始
  }, duration * 1000);  // 将时间转换为毫秒
});