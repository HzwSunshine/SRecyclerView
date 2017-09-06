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
use Gradle:
    [compile 'com.github.hzw:srecyclerview:1.1.4'](https://github.com/HzwSunshine/SRecyclerView)
  

# ProGuard
-keep public class * implements com.hzw.srecyclerview.SRecyclerViewModule



