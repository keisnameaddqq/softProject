//用户名格式判读
function validateUsername(username) {
  // 用户名需要是4-40个字符，只允许是大小写字母和数字
  const regex = /^[a-zA-Z0-9]{4,40}$/;
  if (regex.test(username)) {
    return true;
  } else {
    alert("用户名格式不符合要求。请使用4-40个字符的大小写字母和数字。");
    return false;
  }
}
//密码格式判读
function validatePassword(password) {
  // 密码需要是8-40个字符，至少包含字母、数字和特殊符号中的2种
  const regex = /^(?=.*[a-zA-Z])(?=.*[0-9!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,40}$/;
  if (regex.test(password)) {
    return true;
  } else {
    alert("密码格式不符合要求。请使用8-40个字符，至少包含字母、数字和特殊符号中的2种。");
    return false;
  }
}
//电话号码格式判断
function validatePhoneNumber(phoneNumber) {
  // 中国手机号码通常以1开头，总共有11位数字
  const regex = /^1\d{10}$/;
  if (regex.test(phoneNumber)) {
    return true;
  } else {
    alert("电话号码格式不正确。请确保它是以1开头的11位数字的中国手机号码。");
    return false;
  }
}
//哈希函数
function hashCode(str) {
  var hash = 0;
  if (str.length === 0) return hash;
  for (var i = 0; i < str.length; i++) {
    var char = str.charCodeAt(i);
    hash = (hash << 5) - hash + char;
    hash |= 0; // 将hash转换为32位整数
  }
  return hash;
}