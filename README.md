# CustomBanner
自定义的轮播图

##1、添加依赖：

>app\build.gradle
>dependencies {
>    ...
>    compile 'com.github.langxing:CustomBanner:1.0'
>}

>.\build.gradle
>allprojects {
>    repositories {
>        jcenter()
>        maven { url "https://jitpack.io" }
>    }
>}

##2、布局：
>    <com.example.bannerlibrary.Banner
>        android:id="@+id/banner"
>        android:layout_width="match_parent"
>        android:layout_height="match_parent"
>        android:visibility="gone"
>        app:dotHei="2dp"
>        app:dotMar="10dp"
>        app:dotWid="10dp"
>        app:dotScr="@drawable/dot_selector">

##3、属性说明：
       * dotHei 小圆点的高
       * dotWid 小圆点的宽
       * dotMar 小圆点左右间隔
       * dotScr 小圆点背景，只支持图片选择器

##4、Java：
>        banner = (Banner) findViewById(R.id.banner);
>        List<Integer> ids = new ArrayList<>();
>        ids.add(R.mipmap.image1);
>        ids.add(R.mipmap.image2);
>        banner.setIds(ids);        

##5、有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件(2647759254@qq.com)
* QQ: 2647759254
