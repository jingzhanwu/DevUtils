# DevUtils
Android开发工具类，常用的文件操作，bitmap操作，数据库操作，Toast显示，Dialog，权限管理等

如何使用：


allprojects {
    repositories {
        ...
        ...
        maven {url 'https://dl.bintray.com/jingzhanwu/jzw/'}
    }
}

compile 'com.jzw.dev:devutils:1.2.0'

·支持数据库操作，继承BaseDao，实现对应的方法即可。

·Bitmap相关操作，使用BitmapUtil

·文件相关操作，使用FileUtil

·APP 缓存管理，使用DataCleanManager

·显示图片，使用GlideUtils

·权限操作，使用PermissionUtil

·SharedPreference操作使用SharedPrefUtils类

·Toast显示使用ToastUtil

·简单的dialog显示，使用DialogUtil

·日期和时间有CalendarUtil，DateUtil

·自定义的TitleBar，支持高度，背景色，图标自定义

       <com.dev.jzw.helper.widget.CustomTitleBar
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          app:jzw_bar_backColor="#333"
          app:jzw_bar_backResources="@drawable/icon_back"
          app:jzw_bar_rightResources="@drawable/icon1"
          app:jzw_bar_showLeftView="true"
          app:jzw_bar_showRightView="false"
          app:jzw_bar_title="标题"
          app:jzw_bar_titleColor="#ccc"
          app:jzw_bar_titleSize="18sp" />
.大图查看器
   PictureView.with(this)
                   .setFiles(new ArrayList<File>(), 0)
                   .create();
                   
想体验MVP开发的点这里https://github.com/jingzhanwu/MvpBase

Retrofit+Rxjava的一个请求库
https://github.com/jingzhanwu/RetrofitRxjavaClient
