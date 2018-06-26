# DevUtils
Android开发工具类，常用的文件操作，bitmap操作，数据库操作，Toast显示，Dialog，权限管理等

如何使用：


compile 'com.jzw.dev:devutils:2.5'

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

.反射操作 ReflectUtil

.汉字拼音转换类 Trans2PinYinUtil  FirstLetterUtil

.下拉刷新控件 JSwipeRefreshLayout

.地理位置信息转换 LocationUtil

.支持方京东地址选择view

.支持缩放的 PhotoView

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
                   
    1、查看网络图片，带有下载功能
             PictureView.with(this)
                       .setUrls(urls, 0)
                       .enableDownload(true)
                       .create();
                       
    2、开启关闭 删除 和下载功能
          PictureView.enableDelete(true)
          PictureView.enableDownload(true)
     

.仿京东多级列表选择器

      1、设置容器view
         content = findViewById(R.id.content);

      2、初始化数据
         具体的实体实现ISelectorEntry接口,并实现对应方法
         public class DicTestInfo implements ISelectorEntry<DicTestInfo> {

             private String id;
             private String value;
             private String type;
             private String orgName;
             private List<DicTestInfo> list;

             @Override
             public String getSelectorName() {
                 return getValue();
             }

             @Override
             public String getSelectorId() {
                 return getId();
             }

             @Override
             public List<DicTestInfo> getSelectorChildreen() {
                 return getList();
             }

            }

       3、初始化选择器

          SelectorProvider provider = new SelectorProvider(this, 3);
             provider.setData(list);

              //其他设置的方法省略

             provider.setOnSelectListener(new SelectorProvider.OnSelectorListener() {
                 @Override
                 public void onSelected(List<ISelectorEntry> datas) {
                     DicTestInfo dic5 = (DicTestInfo) datas.get(0);

                 }
             });

             provider.setOnDialogCloseListener(new SelectorProvider.OnDialogCloseListener() {
                 @Override
                 public void dialogclose() {
                     //关闭回调

                 }
             });

        4、添加选择器到viewGroup

            View view = provider.getSelectorView();
            content.addView(view, 0);


想体验MVP开发的点这里
https://github.com/jingzhanwu/MvpBase

一个包含拍照 录制 高度定制的多媒体库
https://github.com/jingzhanwu/MediaLibrary

Retrofit+Rxjava的一个请求库
https://github.com/jingzhanwu/RetrofitRxjavaClient
