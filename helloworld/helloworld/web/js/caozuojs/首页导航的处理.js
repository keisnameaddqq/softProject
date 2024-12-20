//首页导航栏的处理
async function navul(value) {
  const navheader = '<ul class="nav table-bordered">' +
      '<li><a onclick="navul(\'关注\')">关注</a></li>' +
      '<li><a onclick="navul(\'推荐\')">推荐</a></li>' +
      '<li><a onclick="navul(\'热榜\')">热榜</a></li>' +
      '<li><a onclick="navul(\'视频\')">视频</a></li>' +
      '</ul>';
  const navContainer = document.getElementById("articleContainer");
  navContainer.innerHTML = navheader;
  if (value == "关注") {
    await getShouYeData("关注");
  } else if (value == "推荐") {
    await getShouYeData("推荐");
  } else if (value == "热榜") {
    await getShouYeData("热榜");
  } else if (value == "视频") {
    await getShouYeData("视频");
  }
}

async function getShouYeData(canshu) {
  //处理数据的发送
  const formData = new FormData(); // 创建一个 FormData 对象
  formData.append("canshu", canshu);
  formData.append('userId', userId);
  formData.append('token', token);
  try {
    const response = await fetch("./FirstHtmlNavData", { // 使用 Fetch API 将文件上传到服务器
      method: 'POST',
      body: formData,
    });
    const FirstHtmlNavDatasResult = await response.json();
    console.log("FirstHtmlNavDatasResult = " + FirstHtmlNavDatasResult);
    // articleId 0, authorId 1, title 2, content 3, remarkNumber 4, isDianZhang 5,
    // agreeNumber 6, isShouCang 7, timestampinminutes 8, authorNickName 9, authorSignature 10
    for(FirstHtmlNavDataResult of FirstHtmlNavDatasResult){
      document.getElementById("articleContainer").appendChild(
          //articleId, authorId, title, content, remarkNumber, isDianZhang, agreeNumber, userdata, isShouCang,authorNickName,authorSignature
          createArticleDiv(FirstHtmlNavDataResult[0], FirstHtmlNavDataResult[1], FirstHtmlNavDataResult[2], FirstHtmlNavDataResult[3],
              FirstHtmlNavDataResult[4], FirstHtmlNavDataResult[5], FirstHtmlNavDataResult[6], userdata, FirstHtmlNavDataResult[7],
              FirstHtmlNavDataResult[9], FirstHtmlNavDataResult[10]));
      touchup(FirstHtmlNavDataResult[0],"初始");
      IWantShouCang(FirstHtmlNavDataResult[0],"初始");
    }
  } catch (e) {
    console.log("");
  }
}




