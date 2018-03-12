package com.yudianbank.tms.util;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 工具类
 *
 * @author Song Lea
 */
public final class ProjectUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUtil.class);

    // 日期转换常量
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DAY_DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_DATE_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String DAY_DATE_SUFFIX = " 00:00:00";
    public static final String DATE_FORMAT_YYYY_MM_DD_CN = "yyyy年MM月dd日";
    public static final String DEFAULT_CHARSET = "UTF-8";

    // 返回的日志内容常量
    public static final String LOG_DATE = "date";
    public static final String LOG_CONTENT = "content";
    public static final String LOG_DIR_PREFIX = "/data/tms-statistics/";
    public static final int LOG_DEFAULT_LINE = 1000;

    // 处理作业的线程池常量
    public static final int PER_QUERY_NUMBER = 10000;
    public static final ExecutorService EXECUTOR_SERVICE = threadPoolExecutor();

    // 入库前进行判断是否已经入库的校验SQL常量
    public static final String CHECK_CAR_TRANSPORT_REPORT = "SELECT id FROM YD_TMS_REPORT_STATISTICS " +
            "WHERE send_date = ? LIMIT 1";
    public static final String CHECK_PROFIT_REPORT = "SELECT id FROM YD_TMS_PROFIT_STATISTICS " +
            "WHERE statisticsDate = ? LIMIT 1";



    // 分隔符定义
    public static final String SEMICOLON_SEPARATOR = ";";
    public static final String COMMA_SEPARATOR = ",";
    public static final String COLON_SEPARATOR = ":";
    public static final String UNDERLINE_SEPARATOR = "_";

    public enum SmsContentEnum {
        TOTAL("1"), COMPANY("2"), APP("3"), PAY_AMT("4");

        private String code;

        public String toString() {
            return code;
        }

        SmsContentEnum(String code) {
            this.code = code;
        }
    }

    private ProjectUtil() {
    }

    public static Long object2Long(Object obj) {
        return (StringUtils.isEmpty(obj) || "null".equals(obj)) ? null : Long.valueOf(obj.toString());
    }

    public static Integer object2Integer(Object obj) {
        return obj == null ? null : Integer.valueOf(obj.toString());
    }

    // 由格式来转换日期为字符串
    public static String dateFormatByPattern(Date date, String pattern) {
        return null != date ? new SimpleDateFormat(pattern).format(date) : null;
    }

    // 取当前时间但不需要时分秒
    public static Date getDayNoHourMinSecond(Date baseDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // 取指定天数的时间
    public static long getTodayLastMilliseconds() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime() - System.currentTimeMillis();
    }

    // 输出指定时间指定天数的时间
    public static Date getSpecifiedDate(Date baseDate, int specified) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.DATE, specified);
        return calendar.getTime();
    }

    // 输出指定时间指定天数的时间
    public static String getSpecifiedDateStr(Date baseDate, int specified, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(Calendar.DATE, specified);
        return dateFormatByPattern(calendar.getTime(), pattern);
    }

    // 返回固定格式的日期(会抛出转换异常)
    public static Date getDateByStr(String date, String pattern) throws ParseException {
        return date != null ? new SimpleDateFormat(pattern).parse(date.trim()) : null;
    }

    // 固定格式【yyyy-MM-dd HH:mm:ss】来转换日期为字符串
    public static String defaultDateFormat(Date date) {
        return dateFormatByPattern(date, DEFAULT_DATE_FORMAT);
    }

    // 返回固定格式的日期,若日期格式不对则返回空
    public static Date safeGetDateByStr(String date, String pattern) {
        try {
            return date != null ? new SimpleDateFormat(pattern).parse(date.trim()) : null;
        } catch (ParseException e) {
            LOGGER.error("日期【{}】转换异常！{}", date, e.getMessage());
        }
        return null;
    }

    // 判断指定天数的日期是一年中的第几周
    public static int dateToWeekOfYear(String baseDate, int specified, String pattern) throws ParseException {
        Date date = getDateByStr(baseDate, pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, specified);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    // 指定日期所有月的第一天
    public static Date getMonthFirstDayByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // 取昨天
    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    // 判断日期是一周中的第几天
    public static int dayOfWeek(Date date, int specified, boolean chinese) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // chinese时此处必须减一,因为Calendar的一周的首天为周日而不是中国的周一
        calendar.add(Calendar.DATE, chinese ? specified - 1 : specified);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    // 获取指定天数的日期
    public static List<String> getDaysListInSpecified(int specified) {
        List<String> result = new ArrayList<>();
        for (int i = 0, len = specified + 1; i < len; i++) {
            result.add(getSpecifiedDateStr(new Date(), i, DAY_DATE_FORMAT));
        }
        return result;
    }

    // 取两个日期之间的日期
    public static List<String> getBetweenDateStr(Date begin, Date end, String pattern) {
        List<String> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(begin);
        while (begin.getTime() <= end.getTime()) {
            result.add(dateFormatByPattern(tempStart.getTime(), pattern));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            begin = tempStart.getTime();
        }
        return result;
    }

    // 利用MD5进行加密
    public static String encoderByMd5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //加密后的字符串
            return Base64.getEncoder().encodeToString(md5.digest(str.getBytes(DEFAULT_CHARSET)));
        } catch (Exception e) {
            LOGGER.error("使用MD5加密失败！", e);
        }
        return UUID.randomUUID().toString();
    }

    // 返回给定文件倒序给定的行数
    public static String getFileReverseByLine(RandomAccessFile randomAccessFile, int line) throws IOException {
        if (Objects.isNull(randomAccessFile) || randomAccessFile.length() == 0)
            return null; // 文件无内容
        List<String> cacheList = new ArrayList<>(line);
        long cache = randomAccessFile.length() - 1; // 保存上次循环的指针位置
        for (int i = 0; i < line; i++) {
            long pos = cache;
            while (pos > 0) {
                pos--;
                randomAccessFile.seek(pos);
                if (randomAccessFile.readByte() == '\n') break; // 读到换行符
            }
            if (pos == 0) randomAccessFile.seek(0); // 文件开始处
            byte[] bytes = new byte[(int) (cache - pos)];
            if (bytes.length > 0) {
                randomAccessFile.read(bytes); // 读指定字节长度的内容
                cacheList.add("<p>" + new String(bytes, DEFAULT_CHARSET) + "</p>");
            }
            cache -= (cache - pos); // 找到下次循环的指针位置
            if (randomAccessFile.getFilePointer() == 0) break; // 找到文件开始处则直接跳出循环
        }
        StringBuilder builder = new StringBuilder("");
        for (int i = cacheList.size() - 1; i >= 0; i--)
            builder.append(cacheList.get(i));
        return builder.toString();
    }

    // 对应31天日期的顺序排列
    public static List<String> getMonthDaysList() {
        List<String> result = new ArrayList<>();
        for (int i = 31; i > 0; i--)
            result.add(getSpecifiedDateStr(new Date(), -i, DAY_DATE_FORMAT));
        return result;
    }

    private static final int CORE_THREAD_POOL_SIZE = 20;
    private static final int MAX_THREAD_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 300;
    private static final int QUEUE_CAPACITY_SIZE = 100;

    /*
   * 当池子大小小于corePoolSize，就新建线程，并处理请求
   * 当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去workQueue中取任务并处理
   * 当workQueue放不下任务时，就新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize，就用RejectedExecutionHandler来做拒绝处理
   * 当池子的线程数大于corePoolSize时，多余的线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
   * CallerRunsPolicy:它直接在execute方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
   */
    private static ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    // 批量更新
    public static void batchInsertImpl(List<?> insertList, Session session) {
        for (int i = 0, len = insertList.size(); i < len; i++) {
            session.save(insertList.get(i));
            if (i % 30 == 0) {
                session.flush();
                session.clear();
            }
        }
    }
}