# SRecyclerView
有刷新和加载功能的RecyclerView</br>
博客地址：http://blog.csdn.net/hzwailll/article/details/75285924

主要功能有：
1. 下拉刷新，滑到底部加载（也支持GridGridLayoutManager）
2. 支持添加多个头部和尾部（也支持GridGridLayoutManager）
3. 支持代码设置一个刷新头部和加载尾部（满足局部特殊的刷新头部和加载尾部）
4. 支持自定义刷新头部和加载尾部，手势等逻辑已处理，你只需写刷新界面逻辑即可
5. 可以全局配置刷新头部和加载尾部，以及一些其他配置
6. 支持设置LinearLayoutManager的分割线，以及纵向时分割线的左右距离
7. 支持设置一个EmptyView
8. 支持Item的点击事件
9. 附带一个简易的适配器，大大减少适配器的代码
10. 默认设置为纵向的LinearLayoutManager

![image](https://github.com/HzwSunshine/SRecyclerView/blob/master/srecyclerview.gif)


# Download
use Gradle:&nbsp;&nbsp;&nbsp;&nbsp;
    [compile 'com.github.hzw:srecyclerview:1.1.7'](https://github.com/HzwSunshine/SRecyclerView)
  

# ProGuard
-keep public class * implements com.hzw.srecyclerview.SRecyclerViewModule


# Update History

> * 2018.6.28     &nbsp;&nbsp;&nbsp;&nbsp;版本：1.1.8 beta2 </br>
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

