# HeadDragViewPro
一款BODY可以上下滑动，HEAD显示与隐藏，能节约布局空间的自定义View。

## Results demonstrate (效果演示)
![image](https://github.com/XinYiWorld/HeadDragViewPro/blob/master/result.gif)
## Download (集成指南)
1. first,edit your application build.gradle<br />
```Groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
<br />
2. second,edit your module build.gradle<br />
```Groovy
dependencies {
    ...
    compile 'com.github.XinYiWorld:HeadDragViewPro:1.0.0'
}
```


## Use (使用指南)
```Java
    HeadDragView hdv = (HeadDragView) findViewById(R.id.hdv);
    hdv.setOnDragUpdateListener(this);
    ...
    @Override
    public void onOpen() {
        Toast.makeText(this, "open", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClose() {
        Toast.makeText(this, "close", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDraging(float percent) {

    }
```

```Xml
     <xinyi.com.headdragview.HeadDragView
        android:id="@+id/hdv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        >
        <ImageView
            android:layout_width="match_parent"
            android:layout_height="100dp"
            android:background="@drawable/aa"
            android:scaleType="centerCrop"
            />
        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                >
                ...
            </LinearLayout>
        </ScrollView>
```

## Contact me (联系我)
* QQ邮箱:1349308479@qq.com

## Reward me (打赏)
  If you think it's helpful for you,you can reward me to show your encourage.(如果你觉得有用可以对我打赏以示鼓励)<br/>
  ![image](https://github.com/XinYiWorld/CZSuperAdapters/blob/master/wx.png)
  
