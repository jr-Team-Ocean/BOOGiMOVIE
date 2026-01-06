console.log("login.js loaded...")

// JS로 Cookie 가져오기
function getCookie(key){
    const cookies = document.cookie
    console.log(cookies)
    const cookieList = cookies.split(";").map(cookie => cookie.split("="))

    const obj = {}; // 비어있는 객체 생성

    for(let i=0; i<cookieList.length; i++){
        obj[cookieList[i][0]] = cookieList[i][1]; // K = V
    }

    return obj[key]; 
}

// 쿠키에 saveId가 있을 경우
if(document.getElementsByName("memberId")[0] != null){
    const saveId = getCookie("saveId");

    if(saveId != undefined){
        document.querySelector("input[name='memberId']").value = saveId;
        document.querySelector("input[name='saveId']").checked = true;
    }
}


// Google login
function decodeJWT(token) {
    let base64Url = token.split(".")[1];
    let base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    let jsonPayload = decodeURIComponent(
        atob(base64)
        .split("")
        .map(function (c) {
            return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
    }

function handleCredentialResponse(response) {

    console.log("Encoded JWT ID token: " + response.credential);

    const responsePayload = decodeJWT(response.credential);

    console.log("Decoded JWT ID token fields:");
    console.log("  Full Name: " + responsePayload.name);
    console.log("  Given Name: " + responsePayload.given_name);
    console.log("  Family Name: " + responsePayload.family_name);
    console.log("  Unique ID: " + responsePayload.sub);
    console.log("  Profile image URL: " + responsePayload.picture);
    console.log("  Email: " + responsePayload.email);
}