package com.yudianbank.tms.model.vo;

/**
 * 平台校验返回的错误内容
 *
 * @author Song Lea
 */
public class ResponseData {

    public static final String SUCCESS = "操作成功！";
    public static final String FAILURE = "操作失败！";
    public static final String NO_CRON = "调度表达式不能为空！";
    public static final String ILLEGAL_CRON = "非法的调度表达式！";
    public static final String REPEAT_JOB_NAME = "重复的任务名！";
    public static final String NO_JOB_NAME = "作业名不能为空！";
    public static final String NO_TARGET_CLASS = "作业类不存在！";
    public static final String ILLEGAL_TARGET_CLASS = "作业类必须继承Job类！";
    public static final String EMPTY_TARGET_CLASS = "作业类不能为空！";
    public static final String NO_JOB_EXISTS = "作业已不存在！";
    public static final String ILLEGAL_CALCULATE_DATE = "非法的日期格式！";
    public static final String LARGE_CALCULATE_DATE = "统计日期应在今天之前！";
    public static final String NULL_CALCULATE_DATE = "统计日期不能为空！";
    // 时间段校验
    public static final String NULL_START_DATE = "开始日期不能为空！";
    public static final String NULL_END_DATE = "结束日期不能为空！";
    public static final String ILLEGAL_START_END_DATE = "结束日期不能早于开始日期！";
    public static final String TRYING_TO_DEAL_WITH = "正在努力处理中！";
    // 邮件通知
    public static final String HAS_HANDLER = "该统计日期的数据已经处理过，直接跳过！";
    public static final String INSERT_DATA_COUNT = "入库数据量：";
    // 登录界面
    public static final String NO_USER_NAME = "请输入用户名！";
    public static final String NO_PASSWORD = "请输入密码！";
    public static final String NO_VERIFICATION_CODE = "请输入验证码！";
    public static final String ERROR_VERIFICATION_CODE = "验证码不正确！";
    public static final String ERROR_USER_OR_PASSWORD = "用户名或密码不正确！";
    // 信息平台货源推送作业
    public static final String NO_SOURCE_GOODS_CONFIG = "未查询到有效的货源配置表数据！";
    public static final String NO_PUSH_CONFIG = "货源推送定时表未配置！";
    public static final String PUSH_CONFIG_HAS_DELETE = "该货源配置可能已删除,请刷新界面后再试！";
    public static final String PUSH_CONFIG_NO_APPLY_WEEK = "不在货源配置每周用车时间内！";
    public static final String PUSH_CONFIG_NO_VALIDITY_DAY = "指定天数不合法,只能为0(今天)或1(明天)或2(后天)或3(大后天)！";
    public static final String PUSH_CONFIG_NULL_CONFIG_ID = "请选择需要推送的货源！";
    public static final String PUSH_CONFIG_NULL_VALIDITY_DATE = "该货源配置的有效时间不完整！";
    public static final String PUSH_CONFIG_NOT_IN_VALIDITY_DATE = "不在货源配置的有效时间内！";
    public static final String PUSH_CONFIG_IS_RUNNING = "定时任务正在运行中，请稍后再试！";

    // 异常处理
    public enum ExceptionEnum {

        EXCEPTION_SYSTEM_BUSY(100, "系统正忙，请稍后重试！"),
        EXCEPTION_METHOD_NOT_SUPPORTED(101, "请求的方式不对(POST/GET)！"),
        EXCEPTION_LACK_PARAMETER(102, "请求的参数不完整！"),
        EXCEPTION_ITF_EMPTY_DATA(103, "HTTP接口调用未返回数据！"),
        EXCEPTION_ITF_CALL(104, "HTTP接口调用失败！"),
        EXCEPTION_ITF_CALL_TIMEOUT(105, "HTTP接口调用超时！"),
        EXCEPTION_ARGUMENT_TYPE_MISMATCH(106, "请求的参数格式不匹配！"),
        EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE(107, "请求的MINE类型不接受！"),
        EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED(108, "请求的MIME类型不支持！");

        private String msg;
        private int code;

        ExceptionEnum(int code, String msg) {
            this.msg = msg;
            this.code = code;
        }

        public String getMsg() {
            return this.msg;
        }

        public int getCode() {
            return this.code;
        }

        public ErrorInfo getResult(String url) {
            return new ErrorInfo<>(this.code, this.msg, url);
        }
    }
}