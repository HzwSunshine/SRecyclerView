# SRecyclerView
有刷新和加载功能的RecyclerView</br>
博客地址：http://blog.csdn.net/hzwailll/article/details/75285924

示例apk： [点击下载](https://raw.githubusercontent.com/HzwSunshine/SRecyclerView/master/example.apk)


#### 主要功能有：
1. 下拉刷新，滑动到底部加载
2. 支持添加或删除多个头部和尾部
3. 支持自定义刷新头部和加载尾部
4. 支持全局配置刷新头部，加载尾部
5. 支持代码设置刷新头部，加载尾部（满足某个列表的特殊要求）
6. 支持全局及代码设置加载中布局，空布局和错误布局
6. 支持加载中布局，空布局和错误布局显示时的下拉刷新，及点击刷新重试
7. 支持加载尾部的无数据和加载错误的显示及加载错误时的点击加载重试
8. 支持设置LinearLayoutManager的分割线，以及纵向时分割线的左右距离
9. 支持数据不满一屏时的上滑加载
10. 支持列表Item的点击事件
11. 附带一个简易的适配器，大大减少适配器的代码
12. 附带的简易适配器，可用于SRecyclerView，也可用于普通RecyclerView
13. 默认已设置为纵向（VERTICAL）的LinearLayoutManager，请勿重复设置

#### 不支持：
1. 不支持StaggeredGridLayoutManager流式布局的刷新和加载
2. 不支持横向布局的刷新和加载
3. 不支持滑动到底部时，上拉一段距离才触发加载功能的方式
4. 不支持类似于ListView式的空布局设置方式，空布局需要为一个单独布局


![image](https://github.com/HzwSunshine/SRecyclerView/blob/master/srecyclerview.gif)


##  Gradle
**Use Gradle**:&nbsp;&nbsp;&nbsp;&nbsp;

     compile 'com.github.hzw:srecyclerview:1.2.8'


*SRecyclerView中使用了com.android.support:design:26.1.0，如果需要使用你项目中的版本，可用以下方式：*

    compile ("com.github.hzw:srecyclerview:1.2.8"){ exclude group:'com.android.support' }



## 混淆
-keep public class * implements com.hzw.srecyclerview.SRecyclerViewModule


## 使用
**xml**

```
    <com.hzw.srecyclerview.SRecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:dividerColor="#BEBEBE"
        app:dividerHeight="0.8dp"
        app:dividerLeftMargin="10dp"
        app:dividerRightMargin="0dp">
    </com.hzw.srecyclerview.SRecyclerView>
```

**code**

```
        //如果设置了加载监听，就是需要刷新加载功能，如果没有设置加载监听，那么就没有下拉与底部加载
        recyclerView.setLoadListener(new SRecyclerView.LoadListener() {
            @Override
            public void refresh() {
            }

            @Override
            public void loading() {
            }
        });

        //item的点击事件
        recyclerView.setItemClickListener(new SRecyclerView.ItemClickListener() {
            @Override
            public void click(View v, int position) {
            }
        });

        //可以在xml中配置分割线，也可以在代码中设置分割线
        recyclerView.setDivider(Color.LTGRAY, 3, 30, 0);

        //可以添加一个或多个尾部和头部
        recyclerView.addHeader(header);
        recyclerView.removeHeader(header);
        recyclerView.addFooter(footer);
        recyclerView.removeFooter(footer);

        //刷新完成
        recyclerView.refreshComplete();
        //刷新错误，空数据时会显示错误布局
        recyclerView.refreshError();

        //加载更多完成
        recyclerView.loadingComplete();
        //加载更多没有数据
        recyclerView.loadNoMoreData();
        //加载更多出现错误
        recyclerView.loadingError();

        //设置刷新和加载更多是否可用
        recyclerView.setRefreshEnable(enable);
        recyclerView.setLoadingEnable(enable);

        //代码的刷新，应该在setAdapter方法之后调用，true表示有刷新动画，false无动画
        recyclerView.startRefresh(true);

        //更多使用方法请参考demo...
```

**全局配置**

如果需要自定义刷新头部或加载尾部的样式，以自定义刷新头部为例，有两种方式：
1. 新建类并实现AbsRefreshHeader，并在代码中调用**SrecyclerView.setRefreshHeader(new YourRefreshHeader(context))**，为当前的刷新列表设置你自定义的刷新头部
2. 使用全局配置方式，如下所示：

```     
        // 1. SRecyclerView的刷新头部和加载尾部的全局配置需要新建一个类，并实现SRecyclerViewModule接口
        public class TestSRVModule implements SRecyclerViewModule {
            @Override
            public AbsRefreshHeader getRefreshHeader(Context context) {
                return new TestRefreshHeader(context);
            }
            
            //对应的还可以配置全局的加载尾部，空布局和错误布局...
        }
        
        // 2. 并在AndroidManifest.xml中添加meta-data，name为实现类的路径，value必须为："SRecyclerViewModule"
        <meta-data
            android:name="com.hzw.srecyclerviewproject.TestSRVModule"
            android:value="SRecyclerViewModule" />
```
以上是对全局配置的示例，当然你也可以不用做任何配置，如果默认的样式能满足你的要求的话！<br/>
为了满足某个列表有特殊的刷新头部或加载尾部或空布局或错误布局的情况，可以在代码中为这个列表单独设置，即方式1，方式1的优先级大于方式2 
```
        //一下两个方法需要在setAdapter方法之前设置才有效
        recyclerView.setRefreshHeader(new TestRefreshHeader(context));
        recyclerView.setLoadingFooter(new TestLoadFooter(context));
        
        //代码中设置一个空布局和错误布局
        recyclerView.setEmptyView(new TestEmptyView(context));
        recyclerView.setErrorView(new TestErrorView(context));
```

<br/><br/>


## Update History

> * 2019.3.7     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.2.8 </br>
支持刷新下滑灵敏度的设置

> * 2019.1.29     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.2.7 </br>
解决了一些小问题，精简代码

> * 2018.9.4     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.2.4 </br>
添加对加载中布局，错误布局的支持

> * 2018.7.11     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.2.2 </br>
完善代码

> * 2018.7.9     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.2.1 </br>
完善代码，修复bug

> * 2018.7.3     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.2.0 </br>
完善代码，修改了上滑加载数据的策略，体验更好

> * 2018.6.28     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.8 beta3 </br>
修改空布局的添加方式，完善相关代码

> * 2018.2.8     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.8 beta1 </br>
优化加载更多的逻辑；移除优化AbsLoadFooter的部分方法；增加loadingError()方法；

> * 2017.10.10     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.7 </br>
修复多手势下拉时，刷新头部偏移的bug

> * 2017.9.25     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.6 </br>
完善加载尾部的加载逻辑，完善一些小细节

> * 2017.9.18     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.5 </br>
修改获取全局配置的逻辑，解决头部的手势偏移问题，完善尾部加载逻辑，添加分组测试类

> * 2017.9.6     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.4 </br>
修改添加头部和尾部的逻辑，可以在setAdapter之前或之后添加了

> * 2017.8.30    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.3 </br>
完善自动刷新时，手势操作可能引起的刷新异常

> * 2017.8.24    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.2 </br>
解决多手势下拉时的刷新问题，完善刷新加载逻辑，完善代码

> * 2017.7.20    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.1 </br>
完善下拉手势和加载更多的逻辑，完善测试类及注释

> * 2017.7.19    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.0 </br>
完善刷新逻辑，版本更新为1.1.0

> * 2017.7.18    &nbsp;&nbsp;&nbsp;&nbsp;版本：1.0.0 </br>
完成SRecyclerView，测试并使用了一段时间后提交到GitHub，同时提交到JCenter

License
-------

    Copyright 2017 hzwsunshine

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

