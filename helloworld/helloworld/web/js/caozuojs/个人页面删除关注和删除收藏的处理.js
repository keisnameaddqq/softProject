// 个人页面的删除关注
async function deletetGuanZhu(guanZhuAuthorId) {
  if (window.confirm("你确定要取消对这个up主的关注吗？")) {
    console.log("用户要取消对这个up主的关注");
    //处理数据的发送
    const formData = new FormData(); // 创建一个 FormData 对象
    formData.append('guanZhuAuthorId', guanZhuAuthorId);
    formData.append("need", "DeleteGuanZhu");
    formData.append('userId', userId);
    formData.append('token', token);
    try {
      const response = await fetch("./SmallNeed", { // 使用 Fetch API 将文件上传到服务器
        method: 'POST',
        body: formData,
      });
      const DeleteGuanZhuResult = await response.text();
      console.log("DeleteGuanZhuResult = " + DeleteGuanZhuResult);
      if (DeleteGuanZhuResult == "true") {
        alert("取消关注成功!");
      } else {
        console.log("取消关注失败！");
      }
    } catch (e) {
      console.log("取消关注失败！");
    }
  }
}

// 个人页面的删除收藏
async function deletetShouCang(shouCangAticleIdr) {
  if (window.confirm("你确定要删除该收藏吗？")) {
    console.log("用户要要删除该收藏");
    //处理数据的发送
    const formData = new FormData(); // 创建一个 FormData 对象
    formData.append('articleId', shouCangArticleId);
    formData.append("need", "DeleteShouCang");
    formData.append('userId', userId);
    formData.append('token', token);
    try {
      const response = await fetch("./SmallNeed", { // 使用 Fetch API 将文件上传到服务器
        method: 'POST',
        body: formData,
      });
      const DeleteShouCangResult = await response.text();
      console.log("DeleteShouCangResult = " + DeleteShouCangResult);
      if (DeleteShouCangResult == "true") {
        alert("取消收藏成功!");
      } else {
        console.log("取消收藏失败！");
      }
    } catch (e) {
      console.log("取消收藏失败！");
    }
  }
}