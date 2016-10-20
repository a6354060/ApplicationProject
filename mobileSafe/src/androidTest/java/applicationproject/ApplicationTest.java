package applicationproject;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.jcxy.MobileSafe.bean.AppInfo;
import com.jcxy.MobileSafe.db.dao.BlackNumberDao;
import com.jcxy.MobileSafe.engine.AppInfoUtils;

import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    /**
     * 测试添加黑名单
     */
    public void test() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        for (int i = 0; i < 200; i++) {
            blackNumberDao.addNumber(13000000000L + i + "", 0);
        }

    }


    public void test1() {
        AppInfoUtils appInfoUtils = new AppInfoUtils(getContext());
        long start = currentTimeMillis();
        List<AppInfo> appInfos = appInfoUtils.getAppInfos();
        long end = System.currentTimeMillis();
        System.out.println(end-start+"毫秒");

    }


}