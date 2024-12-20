
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const token = urlParams.get('token');
const userId = urlParams.get('userId');
console.log("token=" + token + " userId=" + userId);
const userdata = "?userId=" + encodeURIComponent(userId) + "&token=" + encodeURIComponent(token);
