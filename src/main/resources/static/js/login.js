/**
 * 登录界面处理js
 *
 * @author Song Lea
 */
var LOGIN = function () {

    // 界面DOM
    var urlPath = $('#urlPath').val();
    var $alertInfo = $('#alertInfo');
    var $tipInfo = $('#tipInfo');
    var $userName = $('#loginUsername');
    var $passWord = $('#loginPassword');
    var $code = $('#code');
    
    return {
        login: function () {
            var _user = $.trim($userName.val());
            if (_user === '') {
                $tipInfo.text("请输入用户名！");
                $alertInfo.show();
                return;
            }
            var _pass = $passWord.val();
            if (_pass === '') {
                $tipInfo.text("请输入密码！");
                $alertInfo.show();
                return;
            }
            var _code = $code.val();
            if (_code === '') {
                $tipInfo.text("请输入验证码！");
                $alertInfo.show();
                return;
            }
            $.ajax({
                dataType: "text",
                type: 'POST',
                url: urlPath + "/api/tms/schedule/sign/in",
                data: $("#loginForm").serialize(),
                success: function (data) {
                    if ("success" === data) {
                        location.href = urlPath + "/api/tms/schedule/handler/index";
                    } else {
                        $tipInfo.text(data);
                        $alertInfo.show();
                        // 验证码不正确时不重置整个表单,只是重置验证码输入框
                        if (data === '验证码不正确！') {
                            $code.val('');
                            $code.focus();
                        } else {
                            $("#loginForm")[0].reset();
                            $userName.focus();
                        }
                        $('#codeImage')[0].src = urlPath + '/api/tms/schedule/sign/getIdentifyCode?date=' + Math.random();
                    }
                }
            });
        },
        unamecr: function (e) {
            if (e.which === 13) {
                $passWord.focus();
            }
        },
        ucodecr: function (e) {
            if (e.which === 13) {
                $("#loginBtn").click();
            }
        },
        upasscr: function (e) {
            if (e.which === 13) {
                $code.focus();
            }
        },
        reset: function () {
            $alertInfo.hide();
        }
    }
}();

$(function () {
    $("#loginBtn").click(LOGIN.login);
    $("#resetBtn").click(LOGIN.reset);
    $("input[name=loginUsername]").keypress(LOGIN.unamecr);
    $("input[name=loginPassword]").keypress(LOGIN.upasscr);
    $("input[name=code]").keypress(LOGIN.ucodecr);
});