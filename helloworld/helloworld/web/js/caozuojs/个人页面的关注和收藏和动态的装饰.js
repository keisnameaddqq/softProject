//关注的div块，返回的html的字符串
//guanZhuAuthorId,guanZhuAuthorNickname,guanZhuAuthorSignature,guanZhuAuthorArticleNumber,guanZhuAuthorFanNumber
function getGuanZhuDiv(guanZhuAuthorId, guanZhuAuthorNickname, guanZhuAuthorSignature, guanZhuAuthorArticleNumber, guanZhuAuthorFanNumber) {
  let timestamp = new Date().getTime();
  // 构建头像部分
  const avatarHtml = `<div style="float: left; padding: 10px;"><img src="./userTouXiang/${guanZhuAuthorId}.png?ver=${timestamp}" alt="" height="60px" width="60px"></div>`;
  // 构建信息部分
  const infoHtml = `<div style="float: left;">
            <div style="padding-left: 10px; padding-top: 10px;color: rgb(51, 38, 232);">${guanZhuAuthorNickname}</div>
            <div style="padding-left: 10px; padding-top: 5px;font-size: 15px;">${guanZhuAuthorSignature}</div>
            <div style="padding-left: 50px; padding-top: 7px;color: darkgray;"><span>${guanZhuAuthorArticleNumber}</span>文章 . <span>${guanZhuAuthorFanNumber}</span>关注者</div>
        </div>`;
  // 构建关注按钮部分
  const buttonHtml = `<div style="float: right; padding-left: 10px; padding-top: 20px;"><button onclick="deletetGuanZhu(${guanZhuAuthorId})">取消关注 - </button></div>`;
  // 组合所有部分
  const finalHtml = `<div style="margin: 10px; height: 90px; overflow: hidden; background-color: rgba(220, 254, 118, 0.3); padding-right: 20px;">
            ${avatarHtml}
            ${infoHtml}
            ${buttonHtml}
        </div>`;
  return finalHtml;
}

//收藏的div块，返回的html的字符串
//shouCangArticleAuthorId, shouCangArticleId, shouCangArticleAuthorNickname, shouCangArticleTitle, shouCangArticleUpdateTime, shouCangArticleDianZangNumber, shouCangArticlePingLunNumber, userdata
function getShouCangDiv(shouCangArticleAuthorId, shouCangArticleId, shouCangArticleAuthorNickname, shouCangArticleTitle, shouCangArticleUpdateTime, shouCangArticleDianZangNumber, shouCangArticlePingLunNumber, userdata) {
  let timestamp = new Date().getTime();
  // 构建作者头像部分
  const authorAvatarHtml = `<div style="float: left; padding: 10px;"><img src="./userTouXiang/${shouCangArticleAuthorId}.png?ver=${timestamp}" alt="" height="60px" width="60px"></div>`;
  // 构建作者和文章标题信息部分
  const authorInfoHtml = `<div style="float: left;">
            <div style="padding-left: 10px; padding-top: 5px; font-size: 15px; color: rgb(51, 38, 232);">
                ${shouCangArticleAuthorNickname}
            </div>
            <div style="padding-left: 50px; padding-top: 10px; font-size: 20px; font-weight: bold; overflow: hidden; max-height: 40px; max-width: 420px;">
                <a href="./searcharticle.html${userdata}&articleId=${shouCangArticleId}&authorId=${shouCangArticleAuthorId}">${shouCangArticleTitle}<a>
            </div>
            <div style="padding-left: 50px; padding-top: 5px; font-size: 6px; color: darkgray;">
                <span>更新时间 ${shouCangArticleUpdateTime}</span> .
                <span>${shouCangArticleDianZangNumber}</span>评论 .
                <span>${shouCangArticlePingLunNumber}</span>点赞
            </div>
        </div>`;
  // 构建删除收藏按钮部分
  const deleteButtonHtml = `<div style="float: right; padding-left: 10px; padding-top: 20px;">
        <button onclick="deletetShouCang(${shouCangArticleId})">删除收藏 - </button>
    </div>`;
  // 组合所有部分
  const finalHtml = `
        <div style="margin: 10px; min-height: 90px; overflow: hidden; background-color: rgba(220, 254, 118, 0.3); padding-right: 20px;">
            ${authorAvatarHtml}
            ${authorInfoHtml}
            ${deleteButtonHtml}
        </div>
    `;
  return finalHtml;
}

//动态的div块，返回的html的字符串
//shouCangArticleAuthorId, shouCangArticleId, shouCangArticleAuthorNickname, shouCangArticleTitle, shouCangArticleUpdateTime, shouCangArticleDianZangNumber, shouCangArticlePingLunNumber, userdata
function getDongTaiDiv(shouCangArticleAuthorId, shouCangArticleId, shouCangArticleAuthorNickname, shouCangArticleTitle, shouCangArticleUpdateTime, shouCangArticleDianZangNumber, shouCangArticlePingLunNumber, userdata) {
    let timestamp = new Date().getTime();
    // 构建作者头像部分
    const authorAvatarHtml = `<div style="float: left; padding: 10px;"><img src="./userTouXiang/${shouCangArticleAuthorId}.png?ver=${timestamp}" alt="" height="60px" width="60px"></div>`;
    // 构建作者和文章标题信息部分
    const authorInfoHtml = `<div style="float: left;">
            <div style="padding-left: 10px; padding-top: 5px; font-size: 15px; color: rgb(51, 38, 232);">
                ${shouCangArticleAuthorNickname}
            </div>
            <div style="padding-left: 50px; padding-top: 10px; font-size: 20px; font-weight: bold; overflow: hidden; max-height: 40px; max-width: 420px;">
                <a href="./searcharticle.html${userdata}&articleId=${shouCangArticleId}&authorId=${shouCangArticleAuthorId}">${shouCangArticleTitle}<a>
            </div>
            <div style="padding-left: 50px; padding-top: 5px; font-size: 6px; color: darkgray;">
                <span>更新时间 ${shouCangArticleUpdateTime}</span> .
                <span>${shouCangArticleDianZangNumber}</span>评论 .
                <span>${shouCangArticlePingLunNumber}</span>点赞
            </div>
        </div>`;
    // 组合所有部分
    const finalHtml = `
        <div style="margin: 10px; min-height: 90px; overflow: hidden; background-color: rgba(220, 254, 118, 0.3); padding-right: 20px;">
            ${authorAvatarHtml}
            ${authorInfoHtml}
        </div>
    `;
    return finalHtml;
}


