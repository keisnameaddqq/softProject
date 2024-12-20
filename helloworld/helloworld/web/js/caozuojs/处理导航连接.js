//设置导航栏的连接地址
document.getElementById('list1').setAttribute('href', "./finishlogin.html" + userdata);
document.getElementById('list2').setAttribute('href', "./main.html" + userdata);
document.getElementById('list3').setAttribute('href', "./editer.html" + userdata);
document.getElementById('list4').setAttribute('href', "./create.html" + userdata);
document.getElementById('wqerqwerqqrerqwerqwreqrq').setAttribute('href', "./menduserdata.html" + userdata);
//设置导航栏头像位置
var timestamp = new Date().getTime();
document.getElementById('sdfadsfadfasdffds').setAttribute("src",
  './userTouXiang/' + userId + '.png?ver=' + timestamp);

//处理查询跳转
function getSearchInputValue() {
  // 获取查询框的内容
  var searchInputValue = document.getElementById('searchInput').value;
  window.location.href = "findresult.html" + userdata
    + "&findcontent=" + encodeURIComponent(searchInputValue);
  // 在这里可以对获取到的内容进行处理，例如进行搜索操作等
  console.log("查询框的内容为: " + searchInputValue);
}
//支持查询框的回车
document.getElementById('searchInput').addEventListener('keydown', function (e) {
  if (e.keyCode === 13) {  // 检查是否按下回车键
    document.getElementById("searchInputclick").click();  // 调用搜索函数
  }
});