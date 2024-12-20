//点赞操作的处理
async function touchup(articleId, caozuo = "0") {
  let arr = document.getElementById(articleId + "isDianZhang");
  let brr = document.getElementById(articleId + "agreeNumber");
  let crr = document.getElementById(articleId + "article");
  if (caozuo === "初始") {
    if (arr.innerText == 1) {
      crr.style.backgroundColor = '#34ce34';
      crr.style.color = '#fff';
    }
    return;
  }
  if (arr.innerText == 0) {
    //处理数据的发送
    const formData = new FormData(); // 创建一个 FormData 对象
    formData.append('articleId', articleId);
    formData.append("need", "DianZang");
    formData.append('userId', userId);
    formData.append('token', token);
    try {
      const response = await fetch("./SmallNeed", { // 使用 Fetch API 将文件上传到服务器
        method: 'POST',
        body: formData,
      });
      const dianZangResult = await response.text();
      console.log("dianZangResult = " + dianZangResult);
      if (dianZangResult == "true") {
        arr.innerHTML = 1;
        brr.innerText = parseInt(brr.innerText) + 1;
        crr.style.backgroundColor = '#34ce34';
        crr.style.color = '#fff';
      } else {
        console.log("点赞失败！");
      }
    } catch (e) {
      console.log("点赞失败！");
    }
  }
}

//对收藏请求进行处理
async function IWantShouCang(articleId,caozuo="0") {
  let brr=document.getElementById("ShouCangButton"+articleId);
  let arr=document.getElementById("isShouCang"+articleId);
  if (caozuo === "初始") {
    if(arr.innerText == 1){
      brr.style.color = 'blue';
    }
    return;
  }
  if(arr.innerText==0){
    //处理数据的发送
    const formData = new FormData(); // 创建一个 FormData 对象
    formData.append('articleId', articleId);
    formData.append("need", "ShouCang");
    formData.append('userId', userId);
    formData.append('token', token);
    try {
      const response = await fetch("./SmallNeed", { // 使用 Fetch API 将文件上传到服务器
        method: 'POST',
        body: formData,
      });
      const ShouCangResult = await response.text();
      console.log("ShouCangResult = " + ShouCangResult);
      if (ShouCangResult == "true") {
        brr.style.color = 'blue';
      } else {
        console.log("收藏失败！");
      }
    } catch (e) {
      console.log("收藏失败！");
    }
  }
}

//返回一个文章缩略版的装饰的div块
function createArticleDiv(articleId, authorId, title, content, remarkNumber, isDianZhang, agreeNumber, userdata, isShouCang,authorNickName,authorSignature) {
  // 创建<div>元素
  const articleDiv = document.createElement('div');
  articleDiv.className = 'articles';
  articleDiv.style.height = 'auto';
  // 创建<h4>元素
  const h4Element = document.createElement('h3');
  // h4Element.style.float = 'left';
  h4Element.style.textAlign="center";
  const aElement = document.createElement('a');
  aElement.innerText = title;
  aElement.style.color="#5a3408";
  h4Element.appendChild(aElement);
  // 创建清除浮动的<div>元素
  const clearDiv1 = document.createElement('div');
  clearDiv1.style.clear = 'both';
  // 创建内容<div>元素
  const contentDiv = document.createElement('div');
  contentDiv.style.overflow = 'hidden';
  contentDiv.style.maxHeight = '200px';
  contentDiv.style.display = 'block';
  contentDiv.style.paddingLeft = '20px';
  contentDiv.innerHTML = content;
  // 创建右侧链接
  const rightLink = document.createElement('a');
  rightLink.style.float = 'right';
  rightLink.href = `./searcharticle.html${userdata}&articleId=${articleId}&authorId=${authorId}`;
  rightLink.innerText = '查看全部';
  const iElement = document.createElement('i');
  iElement.innerText = '';
  rightLink.appendChild(iElement);
  // 创建清除浮动的<div>元素
  const clearDiv2 = document.createElement('div');
  clearDiv2.style.clear = 'both';
  // 作者信息
  const authordata = document.createElement('div');
  let timestamp = new Date().getTime();
  authordata.innerHTML+=
      `<img src="./userTouXiang/${authorId}.png?ver=${timestamp}" width="25px" height="25px" style="margin-left: 20px;">`+
      `<span style="margin-left: 10px;font-size: 16px;font-weight: bold;margin-top: 5px;color: rgb(24 17 17 / 40%)">${authorNickName}</span> . `+
      `<span style="color: rgb(124 120 95 / 60%);padding-top: 7px;">${authorSignature}</span>`;
  // 创建功能区<div>元素
  const functionDiv = document.createElement('div');
  functionDiv.className = 'function-as';
  const ulElement = document.createElement('ul');
  const liElements = [
    `<li><a href="#"><i> <span id="remarkNumber">${remarkNumber}</span>条评论</i></a></li>`,
    `<li><a href="#"><i> 分享</i></a></li>`,
    `<li><a href="#"><i onclick="IWantShouCang(${articleId})" id="ShouCangButton${articleId}"> 收藏</i><span id="isShouCang${articleId}" style="display: none">${isShouCang}<span></span></a></li>`,
    `<li><a href="#"><i> 喜欢</i></a></li>`,
    `<li><a href="#"><i>...</i></a></li>`,
  ];
  liElements.remarkNumber = remarkNumber;
  liElements.forEach((liHTML) => {
    const liElement = document.createElement('li');
    liElement.innerHTML = liHTML;
    ulElement.appendChild(liElement);
  });

  // 创建赞同按钮<button>元素
  const agreeButton = document.createElement('button');
  agreeButton.type = 'button';
  agreeButton.onclick = function () {touchup(`${articleId}`);};
  agreeButton.id = `${articleId}article`;
  const agreeSpan = document.createElement('span');
  agreeSpan.id = `${articleId}agreeNumber`;
  agreeSpan.innerText = agreeNumber;
  agreeButton.innerHTML = `<i> 赞同 </i>`;
  agreeButton.appendChild(agreeSpan);
  const isDianZhangSpan = document.createElement('span');
  isDianZhangSpan.style.display = 'none';
  isDianZhangSpan.id = `${articleId}isDianZhang`;
  isDianZhangSpan.innerText = isDianZhang;
  agreeButton.appendChild(isDianZhangSpan);
  // 将所有子元素添加到<articleDiv>中
  articleDiv.appendChild(h4Element);
  articleDiv.appendChild(clearDiv1);
  articleDiv.appendChild(contentDiv);
  articleDiv.appendChild(rightLink);
  articleDiv.appendChild(clearDiv2);
  articleDiv.appendChild(authordata);
  functionDiv.appendChild(ulElement);
  functionDiv.appendChild(agreeButton);
  articleDiv.appendChild(functionDiv);
  return articleDiv;
}

