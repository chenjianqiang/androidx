package com.cjq.androidx;

import android.app.Application;

import androidx.room.Room;

import com.cjq.androidx.database.MyDataBase;
import com.cjq.androidx.tools.AppExecutors;
import com.facebook.stetho.Stetho;

public class MyApplication extends Application {
    private static MyDataBase myDataBase;
    public static final AppExecutors EXECUTORS = new AppExecutors();

    @Override
    public void onCreate() {
        super.onCreate();
        app_init();
    }

    private void app_init() {
        //facebook的Android调试工具Stetho介绍，可以在chrome查看本地数据库 地址 chrome://inspect/#devices
        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());

        if (myDataBase == null) {
            myDataBase = Room.databaseBuilder(this, MyDataBase.class, "my_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            //在更新数据库的模式（schema）后，一些设备上的数据库可能仍然是旧的模式版本。如果 Room 无法找到将设备的数据库从旧版本升级到当前版本的迁移规则，将出现 IllegalStateException。
            //为了防止这种情况发生时应用崩溃，在创建数据库时调用 fallbackToDestructiveMigration() 方法，这样 Room 将会重建应用的数据库表（将直接删除原数据库表中的所有数据）。
            //如果要主线程访问数据库，在构造数据库时调用 allowMainThreadQueries()，否则 Room 不支持在主线程上进行数据库访问
        }
    }
    public static MyDataBase getMyDataBase() {
        return myDataBase;
    }

}
