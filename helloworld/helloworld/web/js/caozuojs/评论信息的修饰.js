function createRemark(remarkid, pinglunuserid, pinglunuserName, pingluncontent) {
  // 获取当前时间戳
  const timestamp = new Date().getTime();

  // 创建<div>元素
  const remarkDiv = document.createElement('div');
  remarkDiv.id = `${remarkid}remark`;
  remarkDiv.style.width = '95%';
  remarkDiv.style.marginTop = '10px';
  remarkDiv.style.borderBottom = '2px solid #000';
  remarkDiv.style.padding = '0px 30px 10px 30px';

  // 每一条评论的评论人信息
  const authorDataDiv = document.createElement('div');
  authorDataDiv.id = `${remarkid}remarkauthordata`;

  const userImage = document.createElement('img');
  userImage.style.boxSizing = 'border-box';
  userImage.style.margin = '0px 0px 0px 20px';
  userImage.style.minWidth = '0px';
  userImage.style.maxWidth = '100%';
  userImage.style.backgroundColor = 'rgb(255, 255, 255)';
  userImage.style.width = '40px';
  userImage.style.height = '40px';
  userImage.style.borderRadius = '3px';
  userImage.style.flex = '0 0 auto';
  userImage.src = `./userTouXiang/${pinglunuserid}.png?ver=${timestamp}`;
  userImage.onclick = function() { remarkImageClick(pinglunuserid);};

  const userNameSpan = document.createElement('span');
  userNameSpan.style.fontSize = '15px'; // 设置字体大小
  userNameSpan.style.color = 'blue';
  userNameSpan.style.marginLeft = '10px';
  userNameSpan.innerText = pinglunuserName;

  const buttonZhiDing = document.createElement('button');
  buttonZhiDing.innerText = '置顶';
  buttonZhiDing.style.float = 'right';
  buttonZhiDing.onclick = function () { remarkZhiDing(remarkid); };

  const buttonDelete = document.createElement('button');
  buttonDelete.innerText = '删除';
  buttonDelete.style.float = 'right';
  buttonDelete.onclick = function () { remarkDelete(remarkid); };

  authorDataDiv.appendChild(userImage);
  authorDataDiv.appendChild(userNameSpan);
  authorDataDiv.appendChild(buttonZhiDing);
  authorDataDiv.appendChild(buttonDelete);

  // 每一条评论的评论内容
  const contentDiv = document.createElement('div');
  contentDiv.id = `${remarkid}remarkcontent`;
  contentDiv.style.margin = '7px 6px 7px 37px';
  contentDiv.innerHTML = pingluncontent;

  // 每一条评论的操作
  const doDiv = document.createElement('div');
  doDiv.id = `${remarkid}remarkdo`;
  const buttonReply = document.createElement('button');
  buttonReply.type = 'button';
  buttonReply.innerHTML = '<span style="display: inline-flex; align-items: center;">​<svg width="1.2em" height="1.2em" viewBox="0 0 24 24" class="ZDI ZDI--ChatBubbleFill24 css-15ro776" fill="currentColor"><path fill-rule="evenodd" d="M12 2.75a9.25 9.25 0 1 0 4.737 17.197l2.643.817a1 1 0 0 0 1.25-1.25l-.8-2.588A9.25 9.25 0 0 0 12 2.75Z" clip-rule="evenodd"></path></svg></span>回复';
  const buttonLike = document.createElement('button');
  buttonLike.type = 'button';
  buttonLike.style.transform = 'none';
  buttonLike.innerHTML = '<span style="display: inline-flex; align-items: center;">​<svg width="1.2em" height="1.2em" viewBox="0 0 24 24" class="ZDI ZDI--HeartFill24 css-15ro776" fill="currentColor"><path fill-rule="evenodd" d="M12.004 4.934c1.015-.944 2.484-1.618 3.98-1.618 3.48 0 6.53 3.265 6.15 7.614-.11 1.254-.686 2.55-1.458 3.753-.778 1.215-1.79 2.392-2.845 3.419-1.054 1.028-2.168 1.923-3.161 2.566a9.96 9.96 0 0 1-1.41.777c-.418.182-.862.32-1.268.32s-.848-.137-1.267-.317a9.918 9.918 0 0 1-1.407-.771c-.992-.64-2.103-1.53-3.156-2.555-1.052-1.024-2.062-2.2-2.84-3.417-.77-1.208-1.346-2.51-1.456-3.775-.38-4.349 2.67-7.614 6.15-7.614 1.484 0 2.983.673 3.988 1.618Z" clip-rule="evenodd"></path></svg></span>喜欢';
  doDiv.style.float = 'right';

  doDiv.appendChild(buttonReply);
  doDiv.appendChild(buttonLike);

  const clearDiv = document.createElement('div');
  clearDiv.style.clear = 'both';

  remarkDiv.appendChild(authorDataDiv);
  remarkDiv.appendChild(contentDiv);
  remarkDiv.appendChild(doDiv);
  remarkDiv.appendChild(clearDiv);

  return remarkDiv;
}

function remarkImageClick(pinglunuserid) {
  if(pinglunuserid!==userId){
    window.location.href = "./VisitUserHome.html?realUserId=" + encodeURIComponent(userId)
        +"&token=" + encodeURIComponent(token) + "&userId=" + encodeURIComponent(pinglunuserid);
  }
}

